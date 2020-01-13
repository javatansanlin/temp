package com.equipment.service.impl;

import com.alibaba.fastjson.JSON;
import com.equipment.dao.EquipMacDao;
import com.equipment.model.old.EqVideo;
import com.equipment.model.old.IN_Info;
import com.equipment.model.old.IN_Video;
import com.equipment.model.old.VideoRequest;
import com.equipment.mqtt.PubMsg;
import com.equipment.service.EquipMacService;
import com.equipment.util.DateUtil;
import com.equipment.util.HttpRequestUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class EquipMacServiceImpl implements EquipMacService {

    @Autowired
    private EquipMacDao equipMacDao;

    /** 发布 */
    @Autowired
    private PubMsg pubMsgl;

    /**
     * 保存设备的mac地址
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveEquipMac(String mi, String mac, String ve, String sd){
        //判断设备是否已存在，已存在则不插入
        Long id = equipMacDao.selectMacByCode(mi);
        if(id != null){
            return;
        }
        String uuid = UUID.randomUUID().toString();
        equipMacDao.insert(mi, mac, ve, uuid, sd);
    }

    //根据mac地址获取物料信息
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> getEquipVideos(Long id){
        Map<String, Object> result = new HashMap<>();
        //通过id获取mac地址
        Map mac = equipMacDao.selectMac(id);
        Map<String, String> param = new HashMap<>();
        param.put("code_", mac.get("mac")+"");
        log.info(JSON.toJSONString(param));
        //获取物料信息
        String jsonStr = HttpRequestUtil.post("http://60.208.131.125:8082/sspAdmin/task/testPut", "["+JSON.toJSONString(param)+"]");
        log.info("请求结果jsonStr:"+jsonStr);

        //保存最新的物料信息
        Map equip = (Map) JSON.parse(jsonStr);
        if(!StringUtils.equals(equip.get("code")+"", "000")){
            //返回失败
            result.put("success", false);
            result.put("message", "操作失败");
            return result;
        }

        //修改之前的物料为历史物料，保存最新的物料信息
        String uuid = mac.get("uuid")+"";
        equipMacDao.updateMaterialNewFlag(0, uuid);
        //equipMacDao.deleteMaterial(id);

        //获得物料列表
        Map materials = (Map)equip.get(mac.get("mac")+"");
        Set<Map.Entry<String, Map>> set = materials.entrySet();
        String new_uuid = UUID.randomUUID().toString();
        for(Map.Entry<String, Map> entry : set){
            Map vo = entry.getValue();
            log.info("vo对象："+JSON.toJSONString(vo));
            //保存物料信息
            vo.put("mac_id", id);
            vo.put("uuid", new_uuid);
            vo.put("newFlag", 1);
            equipMacDao.insertMaterial(vo);
        }

        result.put("success", true);
        result.put("message", "操作成功");
        return result;
    }

    @Override
    public List<Map> showMaterial(Long id){
        List<Map> materials = equipMacDao.findMaterials(id);
        for(Map vo : materials){
            Integer material_count = vo.get("material_count") == null ? 0 : Integer.parseInt(vo.get("material_count")+"");
            Integer total_count = vo.get("total_count") == null ? 0 : Integer.parseInt(vo.get("total_count")+"");
            if(material_count == total_count){
                vo.put("state","已完成");
            }else{
                vo.put("state","未完成");
            }
        }
        return materials;
    }

    @Override
    public Map<String, Object> sendVideoMsg(Long id, String mi) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();

        //1 根据id查询物料信息
        List<Map> videos = equipMacDao.findMaterials(id);
        if(videos == null || videos.isEmpty()){
            log.info("未读取到物料信息，请重新获取物料");
            result.put("message", "未读取到物料信息，请重新获取物料");
            result.put("success", false);
            return result;
        }

        String uuid = videos.get(0).get("uuid")+"";
        String v = "";
        int index = 0;
        for(Map vo : videos){
            index++;
            EqVideo video = new EqVideo();
            //md5 小写
            String md5 = StringUtils.lowerCase(vo.get("material_md5")+"");
            video.setMd5(md5);
            video.setUrl(vo.get("content")+"");
            v = v+"\"V"+(index)+"\":"+JSON.toJSONString(video)+",";
        }

        //向设备发送指令
        String str = "{\"MI\":\""+mi+"\",\"AT\":\"ChangeVideos\","+v+"\"TI\":\""+90000000+"\",\"TS \":\""+ DateUtil.Date2TimeStamp(new Date())+"\"}";
        pubMsgl.publish(str, UUID.randomUUID().toString(),"SERVER/EQUIPMENT/"+mi);

        //同时修改对应的uuid
        equipMacDao.updateMacUUID(uuid, id);

        result.put("message", "操作成功");
        result.put("success", true);
        return result;
    }

    @Override
    public Map<String, Object> sendEquipVideoTimes(IN_Info info) throws Exception {
        Map<String, Object> result = new HashMap<>();
        //通过sd获取mac地址
        String mac = equipMacDao.selectMacBySD(info.getSD());
        String mi = info.getMI();
        String ve = info.getDD().getVE();
        List<Map> materials = equipMacDao.selectMateialId(mac);

        if(materials == null || materials.isEmpty()){
            log.info("该设备无物料信息，请重新获取物料");
            result.put("success", true);
            result.put("message", "操作成功");
            return result;
        }

        Map<String, Map> materialsMap = new HashMap<>();
        for(Map m : materials){
            String md5 = m.get("material_md5")+"";
            md5 = StringUtils.substring(md5, 0, 5);
            materialsMap.put(StringUtils.upperCase(md5), m);
        }

        //上报次数
        List<IN_Video> videos = info.getDD().getVideos();
        List<VideoRequest> list = new ArrayList<>();
        for(IN_Video v : videos){
            String md5 = StringUtils.upperCase(v.getNO());
            //获取匹配的物料信息
            Map materialMap = materialsMap.get(md5);
            //预定义播放次数
            Integer material_count = Integer.parseInt(materialMap.get("material_count")+"");
            String request_id = materialMap.get("request_id")+"";
            String material_id = materialMap.get("material_id")+"";
            Long id = Long.parseLong(materialMap.get("id")+"");
            Integer realTimes = Integer.parseInt(v.getTI()+"");

            if(StringUtils.isBlank(material_id) || StringUtils.isBlank(request_id) || material_count == 0 || realTimes == 0){
                log.info("参数不正确："+JSON.toJSONString(materialMap));
                continue;
            }

            VideoRequest req = new VideoRequest();
            req.setRequest_id(request_id);
            req.setMaterial_id(material_id);
            req.setReal_times(realTimes);
            //os_version 操作系统版本
            req.setOs_version(ve);
            //vendor 设备厂商
            req.setVendor("");
            //model 机型
            req.setModel(info.getDD().getDT());
            //screen_size 1280*800
            req.setScreen_size("10英寸");
            //ipv4
            req.setIpv4("127.0.0.0");
            //log.info("广告播放次数上报：广告对象{}", JSON.toJSONString(req));
            list.add(req);

            //更新已播放次数到数据库
            if(material_count == realTimes){
                equipMacDao.updateMaterialTotalCount(realTimes, id);
            }
        }

        if(list == null || list.isEmpty()){
            log.info("改设备无匹配的物料，不上报播放次数");
            result.put("success", true);
            result.put("message", "操作成功");
            return result;
        }

        log.info("广告播放次数上报请求参数："+JSON.toJSONString(list));
        //获取物料信息
        String jsonStr = HttpRequestUtil.post("http://60.208.131.125:8082/sspAdmin/task/testRecordCount", JSON.toJSONString(list));
        log.info("广告播放次数上报返回结果："+jsonStr);
        Map returnMap = JSON.parseObject(jsonStr);
        if(StringUtils.equals(returnMap.get("code")+"", "007")){
            //返回成功
            result.put("success", true);
            result.put("message", "操作成功");
        }else{
            result.put("success", false);
            result.put("message", "操作失败");
        }
        return result;
    }

    /*@Override
    public Map<String, Object> sendVideoMsg(Long id, String mi) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        if(id == null){
            //根据mi查询mac_id
            id = equipMacDao.selectMacByCode(mi);
        }
        //1 根据id查询物料信息
        List<Map> videos = equipMacDao.findMaterials(id);
        if(videos == null || videos.isEmpty()){
            log.info("未读取到物料信息，请重新获取物料");
            result.put("message", "未读取到物料信息，请重新获取物料");
            result.put("success", false);
            return result;
        }

        String uuid = videos.get(0).get("uuid")+"";
        String v = "";
        int index = 0;
        int ti = 0;
        for(Map vo : videos){
            index++;
            EqVideo video = new EqVideo();
            String md5 = StringUtils.lowerCase(vo.get("material_md5")+"");
            Integer material_count = Integer.parseInt(vo.get("material_count")+"");
            Integer total_count = Integer.parseInt(vo.get("total_count")+"");
            if(material_count <= total_count){
                continue;
            }

            Integer temp_ti = material_count - total_count;
            //播放次数处理
            if(ti==0 || ti > temp_ti){
               ti = temp_ti;
            }

            video.setMd5(md5);
            video.setUrl(vo.get("content")+"");
            v = v+"\"V"+(index)+"\":"+JSON.toJSONString(video)+",";
        }

        if(ti == 0){
            ti = 90000000;
        }
        //向设备发送指令
        String str = "{\"MI\":\""+mi+"\",\"AT\":\"ChangeVideos\","+v+"\"TI\":\""+ti+"\",\"TS \":\""+ DateUtil.Date2TimeStamp(new Date())+"\"}";
        pubMsgl.publish(str, UUID.randomUUID().toString(),"SERVER/EQUIPMENT/"+mi);

        //同时修改对应的uuid
        equipMacDao.updateMacUUID(uuid, id);

        result.put("message", "操作成功");
        result.put("success", true);
        return result;
    }*/

    /*@Override
    public Map<String, Object> sendEquipVideoTimes(IN_Info info) throws Exception {
        Map<String, Object> result = new HashMap<>();
        //通过sd获取mac地址
        String mac = equipMacDao.selectMacBySD(info.getSD());
        String mi = info.getMI();
        String ve = info.getDD().getVE();
        List<Map> materials = equipMacDao.selectMateialId(mac);

        if(materials == null || materials.isEmpty()){
            log.info("该设备无物料信息，请重新获取物料");
            result.put("success", true);
            result.put("message", "操作成功");
            return result;
        }

        //把物料信息存放在map中
        //查询最小预播放次数 判断是否需要更新已播放次数到数据库
        Integer minCount = null;
        Map<String, Map> materialsMap = new HashMap<>();
        for(Map m : materials){
            Integer material_count = Integer.parseInt(m.get("material_count")+"");
            Integer total_count = Integer.parseInt(m.get("total_count")+"");
            if(material_count <= total_count){
                continue;
            }

            String md5 = m.get("material_md5")+"";
            md5 = StringUtils.substring(md5, 0, 5);
            materialsMap.put(StringUtils.upperCase(md5), m);

            if(minCount == null ||  minCount > material_count){
                minCount = material_count;
            }
        }

        if(materialsMap == null || materialsMap.isEmpty()){
            log.info("该设备物料播放次数已用完，请重新获取物料");
            result.put("success", true);
            result.put("message", "操作成功");
            return result;
        }

        //判断是否需要更新已播放次数到数据库
        List<IN_Video> videos = info.getDD().getVideos();
        boolean updateFlag = false;
        for(IN_Video v : videos){
            String md5 = StringUtils.upperCase(v.getNO());
            Map materialMap = materialsMap.get(md5);
            Integer total_count = Integer.parseInt(materialMap.get("total_count")+"");
            String ti = v.getTI();
            if(Integer.parseInt(ti) + total_count >= minCount){
                updateFlag = true;
                break;
            }
        }
        log.info("updateFlag:"+updateFlag + "-----------minCount:"+minCount);

        //上报次数
        List<VideoRequest> list = new ArrayList<>();
        for(IN_Video v : videos){
            String md5 = StringUtils.upperCase(v.getNO());
            //获取匹配的物料信息
            Map materialMap = materialsMap.get(md5);
            Integer material_count = Integer.parseInt(materialMap.get("material_count")+"");
            String request_id = materialMap.get("request_id")+"";
            String material_id = materialMap.get("material_id")+"";
            Integer total_count = Integer.parseInt(materialMap.get("total_count")+"");
            Long id = Long.parseLong(materialMap.get("id")+"");
            Integer realTimes = Integer.parseInt(v.getTI()+"") + total_count;

            if(StringUtils.isBlank(material_id) || StringUtils.isBlank(request_id) || material_count == 0 || realTimes == 0){
                continue;
            }

            VideoRequest req = new VideoRequest();
            req.setRequest_id(request_id);
            req.setMaterial_id(material_id);
            req.setReal_times(realTimes);
            //os_version 操作系统版本
            req.setOs_version(ve);
            //vendor 设备厂商
            req.setVendor("");
            //model 机型
            req.setModel(info.getDD().getDT());
            //screen_size 1280*800
            req.setScreen_size("10英寸");
            //ipv4
            req.setIpv4("127.0.0.0");
            log.info("广告播放次数上报：广告对象{}", JSON.toJSONString(req));
            list.add(req);

            //更新已播放次数到数据库
            if(updateFlag){
                equipMacDao.updateMaterialTotalCount(Integer.parseInt(v.getTI()+""), id);
            }
        }

        if(list == null || list.isEmpty()){
            log.info("改设备无匹配的物料，不上报播放次数");
            result.put("success", true);
            result.put("message", "操作成功");
            return result;
        }

        //解绑后绑定
        if(updateFlag){
            sendVideoMsg(null, mi);
        }

        log.info("广告播放次数上报请求参数："+JSON.toJSONString(list));
        //获取物料信息
        String jsonStr = HttpRequestUtil.post("http://60.208.131.125:8082/sspAdmin/task/testRecordCount", JSON.toJSONString(list));
        log.info("广告播放次数上报返回结果："+jsonStr);
        Map returnMap = JSON.parseObject(jsonStr);
        if(StringUtils.equals(returnMap.get("code")+"", "007")){
            //返回成功
            result.put("success", true);
            result.put("message", "操作成功");
        }else{
            result.put("success", false);
            result.put("message", "操作失败");
        }
        return result;
    }*/

    @Override
    public PageInfo<Map> findEquipPage(int pageNum, int pageSize, String mi, Integer count) {
        PageHelper.startPage(pageNum, pageSize);
        List<Map> list = equipMacDao.findEquipPage(mi, count);
        return new PageInfo(list);
    }

    @Override
    public List<Map> findEquipAll(String mi, Integer count) {
        List<Map> list = equipMacDao.findEquipAll(mi, count);
        return list;
    }

}
