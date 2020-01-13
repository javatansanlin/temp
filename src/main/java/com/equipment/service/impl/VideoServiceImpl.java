package com.equipment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.aliyun.oss.OSSClient;
import com.equipment.dao.BasicsettingDao;
import com.equipment.dao.VideoDao;
import com.equipment.entity.Vedeo;
import com.equipment.entity.VedeoLabel;
import com.equipment.model.equipmanager.MovieLocal;
import com.equipment.model.equipmanager.MovieLocalDetail;
import com.equipment.model.old.EqVideo;
import com.equipment.model.old.IN_Info;
import com.equipment.mqtt.PubMsg;
import com.equipment.service.VideoService;
import com.equipment.util.DateUtil;
import com.equipment.util.Md5Util;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @Author:JavaTansanlin
 * @Description:视频相关的业务逻辑
 * @Date: Created in 15:17 2018/12/10
 * @Modified By:
 */
@Service
@Transactional
public class VideoServiceImpl implements VideoService {

    private Logger logger = LoggerFactory.getLogger(VideoServiceImpl.class);

    /** 注入dao **/
    @Autowired
    private VideoDao videoDao;

    /** oss上传key */
    @Value("${oss.key}")
    private String osskey;

    /** oss文件上传密钥 */
    @Value("${oss.secret}")
    private String osssecret;

    /** oss的broker */
    @Value("${oss.broker}")
    private String ossBroker;

    /** oss地域节点r */
    @Value("${oss.endPoint}")
    private String ossEndPoint;

    /** 发布 */
    @Autowired
    private PubMsg pubMsgl;

    /** redis操作 */
    @Autowired
    private RedisTemplate redisTemplate;

    /** 项目域名 */
    @Value("${ser.yuming}")
    private String yuming;

    /** 基础参数dao */
    @Autowired
    private BasicsettingDao basicsettingDao;

    /**
     * 查询设备视频列表
     * @param eqCode 设备编号
     * @param vCode 视频编号
     * @param vName 视频名称
     * @param sCode 店铺编号
     * @param sName 店铺名称
     * @param agentOid 代理oid
     * @param pageSize 显示每页数据数量
     * @param pageNum 当前页
     * @return
     */
    @Override
    public Map<String, Object> eqList(String eqCode, String vCode, String vName, String sCode, String sName,
                                      String agentOid, Integer pageSize, Integer pageNum) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,3);

        //处理分页相关数据
        if (pageNum==null || pageNum<=0){
            pageNum = 1;
        }
        if (pageSize==null || pageSize<=0){
            pageSize = 15;
        }

        //查询数据
        //查询
        PageHelper.startPage(pageNum, pageSize);
        List<Map> sales = videoDao.queryEqVideoByDition(eqCode ,vCode ,vName,sCode ,sName ,agentOid);
        PageInfo pageInfo = new PageInfo(sales);
        result.put("data" ,pageInfo);
        //查询基础参数种的可绑设备数
        result.put("bang" ,videoDao.findBaseSetVideoNum());
        return result;
    }

    /**
     * 添加视频标签
     * @param name 标签名字
     * @param operator 操作员
     * @return
     */
    @Override
    public Map<String, Object> addLable(String name, Long operator) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);

        //判断参数
        if (name==null || "".equals(name.replace(" " ,""))){
            result.put("msg" ,"标签名字不能为空");
            return result;
        }

        //判断标签名字是否存在
        String newName = name.replace(" ", "");
        Integer iseixtByName = videoDao.findLableIseixtByName(newName);
        if (iseixtByName!=null && iseixtByName>0){
            result.put("msg" ,"该标签名字已经存在");
            return result;
        }

        //执行插入操作
        VedeoLabel vedeoLabel = new VedeoLabel();
        vedeoLabel.setLabelName(newName);
        vedeoLabel.setOperator(operator);
        int i = videoDao.addLabelOne(vedeoLabel);

        if (i>0){
            result.put("code" ,3);
            result.put("msg" ,"添加成功");
            result.put("lableName" ,newName);
            result.put("id" ,vedeoLabel.getId());
        }else {
            result.put("msg" ,"添加失败");
        }
        return result;
    }

    /** 查看所有的视频标签 */
    @Override
    public Map<String, Object> findAllVideoLable() {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,3);
        result.put("data" ,videoDao.findAllVideoLabel());
        return result;
    }

    /**
     * 添加视频
     * @param name 名字
     * @param labelId 标签id
     * @param filePath 视频地址
     * @return
     */
    @Override
    public Map<String, Object> addVideo(String name, Long labelId, String filePath ,Long operator) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);

        //判断参数
        if (name==null || "".equals(name.replace(" " ,""))){
            result.put("msg" ,"视频名字不能为空");
            return result;
        }
        if (filePath==null || "".equals(filePath.replace(" " ,""))){
            result.put("msg" ,"视频文件不能为空");
            return result;
        }
        if (operator==null){
            result.put("msg" ,"操作员不能为空");
            return result;
        }

        //判断文件名是否存在
        String newName = name.replace(" ", "");
        Integer nameIsExit = videoDao.findVideoNameIsExit(newName);
        if (nameIsExit!=null && nameIsExit>0){
            result.put("msg" ,"该视频名字已经存在");
            return result;
        }
        //判断该文件是否存在
        File file = new File(filePath);
        if (!file.exists()){
            result.put("msg" ,"该视频文件不存在");
            return result;
        }
        //获取视频的md5值
        String md5 = Md5Util.fileMd5(file);
        if (md5==null){
            result.put("msg" ,"该视频MD5校验失败");
            return result;
        }

        try {
            //获取文件名
            String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
            //获取文件后缀名
            String fileType = Files.probeContentType(Paths.get(filePath));

            //上传文件到oss
            String endpoint = "http://"+ossEndPoint;
            // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
            String accessKeyId = osskey;
            String accessKeySecret = osssecret;
            // 创建OSSClient实例。
            OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
            // 上传文件。<yourLocalFile>由本地文件路径加文件名包括后缀组成，例如/users/local/myfile.txt。
            ossClient.putObject(ossBroker, fileName, file);
            // 关闭OSSClient。
            ossClient.shutdown();

            //添加到数据库
            Vedeo vedeo = new Vedeo();
            vedeo.setFileName(newName);
            vedeo.setFileCode(fileName);
            vedeo.setFileUrl("http://"+ossBroker+"."+ossEndPoint+"/"+fileName);
            vedeo.setLabel(labelId);
            vedeo.setIsdele(1);
            vedeo.setFileMd5(md5);
            vedeo.setFileSize(file.length());
            vedeo.setOperator(operator);
            vedeo.setFileType(fileType);
            videoDao.addVideoOne(vedeo);
            result.put("code" ,3);
            result.put("msg" ,"保存成功!!");
        }catch (Exception e){
            //System.out.println(e.getMessage());
            result.put("msg" ,e.getMessage());
        }

        return result;
    }

    /** 查询该视频名字是否存在 */
    @Override
    public Map<String, Object> videoNameIsExit(String name) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);
        if (name==null || "".equals(name)){
            result.put("msg" ,"名字不能为空");
            return result;
        }
        //判断文件名是否存在
        String newName = name.replace(" ", "");
        Integer nameIsExit = videoDao.findVideoNameIsExit(newName);
        if (nameIsExit!=null && nameIsExit>0){
            result.put("msg" ,"该视频名字已经存在");
            return result;
        }
        result.put("code" ,3);
        result.put("msg" ,"该名字可以使用");
        return result;
    }

    /**
     * 查询视频列表
     * @param eqCode 设备编号
     * @param vCode 视频编号
     * @param vName 视频名称
     * @param shopCode 店铺编号
     * @param shopName 店铺名称
     * @param agentOid 代理oid
     * @param pageSize 显示每页数据数量
     * @param pageNum 当前页
     * @return
     */
    @Override
    public Map<String, Object> videoList(String eqCode, String vCode, String vName, String shopCode, String shopName, String agentOid, Integer pageSize, Integer pageNum) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,3);

        //处理分页相关数据
        if (pageNum==null || pageNum<=0){
            pageNum = 1;
        }
        if (pageSize==null || pageSize<=0){
            pageSize = 15;
        }

        //查询数据
        PageHelper.startPage(pageNum, pageSize);
        List<Map> sales = videoDao.findVideoDetail(eqCode ,vCode ,vName ,shopCode ,shopName ,agentOid);
        PageInfo pageInfo = new PageInfo(sales);
        result.put("data" ,pageInfo);
        return result;
    }

    /** 删除视频 */
    @Override
    public Map<String, Object> deleVideo(Long vId) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);
        //判断参数
        if (vId==null || vId<=0){
            result.put("msg" ,"请选择需要删除的视频！");
            return result;
        }
        //查询该设备是否存在，并且查询绑定的设备数
        Map video = videoDao.findVideoIsExitAndEqNumByVID(vId);
        if (video==null || video.get("ID")==null){
            result.put("msg" ,"该视频不存在！");
            return result;
        }
        //判断是否又绑定的视频数
        if (Integer.parseInt(video.get("CON").toString())>0){
            result.put("msg" ,"该视频有绑定的设备，请先解绑！");
            return result;
        }
        //删除oss上的文件
        try {
            String endpoint = "http://"+ossEndPoint;
            // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
            String accessKeyId = osskey;
            String accessKeySecret = osssecret;
            // 创建OSSClient实例。
            OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
            // 删除文件。
            ossClient.deleteObject(ossBroker, (String) video.get("FILE_CODE"));
            // 关闭OSSClient。
            ossClient.shutdown();

        }catch (Exception e){
            System.out.println("删除oss文件报错："+e.getMessage());
        }
        //删除数据库记录
        int i = videoDao.deleVideo(vId);
        result.put("code" ,3);
        result.put("msg" ,"删除成功！删除数为："+i);
        return result;
    }

    /** 设备绑定视频-->查找视频 */
    @Override
    public Map<String, Object> eqBindFindVideo(String eqCode, String vName, String vCode, Integer pageSize, Integer pageNum) {
        Map<String ,Object> result = new HashMap<>();
        //处理分页相关数据
        if (pageNum==null || pageNum<=0){
            pageNum = 1;
        }
        if (pageSize==null || pageSize<=0){
            pageSize = 15;
        }

        //查询数据
        PageHelper.startPage(pageNum, pageSize);
        List<Map> data = videoDao.findCanBanVideoByEqCodeAndCondition(eqCode ,vName ,vCode);
        PageInfo pageInfo = new PageInfo(data);
        result.put("data" ,pageInfo);
        return result;
    }

    /** 设备绑定视频 */
    @Override
    public Map<String, Object> eqBindVideo(String eqCode, String vid ,Long operator) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);
        //判断参数
        if (eqCode==null || "".equals(eqCode)){
            result.put("msg" ,"设备参数错误！");
            return result;
        }
        if (vid==null || "".equals(vid)){
            result.put("msg" ,"绑定视频参数错误！");
            return result;
        }
        try {
            JSONArray jsonArray = JSON.parseArray(vid);
            List<Long> vids = jsonArray.toJavaList(Long.class);
            if (vids==null || vids.size()<=0){
                result.put("msg" ,"请选择你需要绑定的视频！");
                return result;
            }
            //查找该设备是否存在并且查询已经绑定的设备数
            Map bindNum = videoDao.findEqExistAndQueryBindNum(eqCode);
            if (bindNum==null || bindNum.get("ID")==null){
                result.put("msg" ,"该设备不存在！");
                return result;
            }
            //判断该设备是否在线并且是否是视频机
            String lin = (String) redisTemplate.opsForValue().get(eqCode + "-Info");
            if (lin==null || "".equals(lin.trim())){
                result.put("msg" ,"该设备不在线");
                return result;
            }
            IN_Info inInfo = JSON.parseObject(lin, IN_Info.class);//获取心跳钟的数据
            if (!"MqBattery-Video".equals(inInfo.getDD().getDT())){
                result.put("msg" ,"该设备不支持视频播放！");
                return result;
            }
            //判断绑定的视频数是否超过
            int con = Integer.parseInt(bindNum.get("CON").toString());
            Integer baseSetVideoNum = videoDao.findBaseSetVideoNum();//查询系统最大视频数
            if ( baseSetVideoNum==null || (con+vids.size())>baseSetVideoNum ){
                result.put("msg" ,"绑定视频数大于系统设定！系统最大视频数："+baseSetVideoNum);
                return result;
            }
            //插入视频绑定数据
            for (int i = 0; i < vids.size(); i++) {
                Long vi = vids.get(i);
                //查找该视频
                Vedeo video = videoDao.findVideoById(vi);
                if (video!=null){
                    //插入记录
                    videoDao.addEqVideoOne(eqCode ,vi ,(i+1) ,operator);
                    EqVideo eqVideo = new EqVideo();
                    eqVideo.setUrl(video.getFileUrl());
                    eqVideo.setMd5(video.getFileMd5());
                    //向设备发送指令
                    String str = "{\"MI\":\""+eqCode+"\",\"AT\":\"ChangeVideo\",\"SI\":"+JSON.toJSONString(eqVideo)+",\"TS\":\""+DateUtil.Date2TimeStamp(new Date())+"\"}";
                    pubMsgl.publish(str,UUID.randomUUID().toString(),"SERVER/EQUIPMENT/"+eqCode);
                }
            }
            result.put("code" ,3);
            result.put("msg" ,"绑定成功，已为设备更新视频");
        }catch (Exception e){
            result.put("msg" ,"系统错误！"+e.getMessage());
        }
        return result;
    }

    /** 设备解绑视频 evId-视频设备关系记录id */
    @Override
    public Map<String, Object> untying(Long evId) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);

        //判断参数
        if (evId==null){
            result.put("msg" ,"请选择需要解绑的视频！");
            return result;
        }

        //查找该记录
        Map eqVideoById = videoDao.findEqVideoById(evId);
        if (eqVideoById==null || eqVideoById.get("ID")==null){
            result.put("msg" ,"该视频绑定记录不存在！");
            return result;
        }

        //查询默认视频
        String bseSetDefaultVideo = videoDao.findBseSetDefaultVideo();
        if (bseSetDefaultVideo==null){
            result.put("msg" ,"请设置好系统的默认视频再进行删除！");
            return result;
        }

        //查询默认视频MD5
        String bseSetDefaultVideoMd5 = videoDao.findBseSetDefaultVideoMd5();
        if (bseSetDefaultVideoMd5==null){
            result.put("msg" ,"请设置好系统的默认视频的md5再进行删除！");
            return result;
        }

        try {
            //删除该记录
            int i = videoDao.deleEqVideoById(evId);
            String eqCode = (String) eqVideoById.get("EQ_CODE");//设备编号
            //查询该设备其它绑定的视频
            List<Map> videoDetail = videoDao.findEqBindVideoDetail(eqCode);
            //如果没有已经绑定的视频，则播放默认视频
            if (videoDetail==null || videoDetail.size()<=0){
                //向设备发送指令
                EqVideo eqVideo = new EqVideo();
                eqVideo.setUrl(bseSetDefaultVideo);
                eqVideo.setMd5(bseSetDefaultVideoMd5);
                String str = "{\"MI\":\""+eqCode+"\",\"AT\":\"ChangeVideo\",\"SI\":"+JSON.toJSONString(eqVideo)+",\"TS\":\""+DateUtil.Date2TimeStamp(new Date())+"\"}";
                pubMsgl.publish(str,UUID.randomUUID().toString(),"SERVER/EQUIPMENT/"+eqCode);
                result.put("msg" ,"操作成功，该设备没有绑定视频，已播放系统默认视频");
            }else {
                for (int j = 0; j < videoDetail.size(); j++) {
                    Map data = videoDetail.get(j);
                    //向设备发送指令
                    EqVideo eqVideo = new EqVideo();
                    eqVideo.setUrl(data.get("fileUrl").toString());
                    eqVideo.setMd5(data.get("fileMd5").toString());
                    String str = "{\"MI\":\""+eqCode+"\",\"AT\":\"ChangeVideo\",\"SI\":"+JSON.toJSONString(eqVideo)+",\"TS\":\""+DateUtil.Date2TimeStamp(new Date())+"\"}";
                    pubMsgl.publish(str,UUID.randomUUID().toString(),"SERVER/EQUIPMENT/"+eqCode);
                }
                result.put("msg" ,"操作成功!!");
            }
            result.put("code" ,3);
        }catch (Exception e){
            result.put("msg" ,"系统错误！"+e.getMessage());
        }

        return result;
    }

    /** 向设备发送更新视频指令 */
    @Override
    public Map<String, Object> updateEqVideo(String eqCode) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);
        //判断参数
        if (eqCode==null || "".equals(eqCode)){
            result.put("msg" ,"请选择需要更新的设备！");
            return result;
        }
        try {
            //判断该设备是否在线并且是否是视频机
            String lin = (String) redisTemplate.opsForValue().get(eqCode + "-Info");
            if (lin==null || "".equals(lin.trim())){
                result.put("msg" ,"该设备不在线");
                return result;
            }
            IN_Info inInfo = JSON.parseObject(lin, IN_Info.class);//获取心跳钟的数据
            if (!"MqBattery-Video".equals(inInfo.getDD().getDT())){
                result.put("msg" ,"该设备不支持视频播放！");
                return result;
            }
            //查询该设备绑定的视频
            List<Map> videoDetail = videoDao.findEqBindVideoDetail(eqCode);
            if (videoDetail==null || videoDetail.size()<=0){
                //查询默认视频
                String bseSetDefaultVideo = videoDao.findBseSetDefaultVideo();
                if (bseSetDefaultVideo==null){
                    result.put("msg" ,"请设置好系统的默认视频再进行删除！");
                    return result;
                }
                //向设备发送指令
                String str = "{\"MI\":\""+eqCode+"\",\"AT\":\"ChangeVideo\",\"SI\":\""+bseSetDefaultVideo+"\",\"TS\":\""+DateUtil.Date2TimeStamp(new Date())+"\"}";
                pubMsgl.publish(str,UUID.randomUUID().toString(),"SERVER/EQUIPMENT/"+eqCode);
                result.put("msg" ,"操作成功，该设备没有绑定视频，已播放系统默认视频");
            }else {
                for (int j = 0; j < videoDetail.size(); j++) {
                    Map data = videoDetail.get(j);
                    //向设备发送指令
                    String str = "{\"MI\":\""+eqCode+"\",\"AT\":\"ChangeVideo\",\"SI\":\""+data.get("fileUrl").toString()+"\",\"TS\":\""+DateUtil.Date2TimeStamp(new Date())+"\"}";
                    pubMsgl.publish(str,UUID.randomUUID().toString(),"SERVER/EQUIPMENT/"+eqCode);
                }
                result.put("msg" ,"操作成功!!");
            }
            result.put("code" ,3);
        }catch (Exception e){
            result.put("msg" ,"系统错误！"+e.getMessage());
        }
        return result;
    }

    /** 查询已经绑定的视频列表 */
    @Override
    public Map<String, Object> findEqBindedVideo(String eqCode) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);
        //判断参数
        if (eqCode==null || "".equals(eqCode)){
            result.put("msg" ,"请选择需要查看的设备！");
            return result;
        }
        //查询该设备绑定的视频
        result.put("data" ,videoDao.findEqBindVideoDetail(eqCode));
        result.put("msg" ,"操作成功！");
        result.put("code" ,3);
        return result;
    }

    /** 根据设备编号查询设备的当前音量 */
    @Override
    public Map<String, Object> findEqVolume(String eqCode) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);

        if (eqCode==null || "".equals(eqCode)){
            result.put("msg" ,"设备编号不正确");
            return result;
        }

        //判断该设备是否在线
        //根据设备号查询该设备的心跳是否存在
        String lin = (String) redisTemplate.opsForValue().get(eqCode + "-Info");
        if (lin==null || "".equals(lin.trim())){
            result.put("msg" ,"该设备不在线");
            return result;
        }

        IN_Info inInfo = JSON.parseObject(lin, IN_Info.class);
        String vo = inInfo.getVO() == null ? "0" : inInfo.getVO();

        result.put("code" ,3);
        result.put("vo" ,vo);
        result.put("msg" ,"操作成功");
        return result;
    }

    /** 调节设备音量 */
    @Override
    public Map<String, Object> adjustEqVolume(String eqCode, Integer volume) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);

        if (eqCode==null || "".equals(eqCode)){
            result.put("msg" ,"设备编号不正确");
            return result;
        }
        if (volume==null || volume<0 || volume>15){
            result.put("msg" ,"音量数值不正确");
            return result;
        }

        //判断该设备是否在线
        //根据设备号查询该设备的心跳是否存在
        String lin = (String) redisTemplate.opsForValue().get(eqCode + "-Info");
        if (lin==null || "".equals(lin.trim())){
            result.put("msg" ,"该设备不在线");
            return result;
        }

        try {
            IN_Info inInfo = JSON.parseObject(lin, IN_Info.class);
            String dt = inInfo.getDD().getDT();
            if ("MqBattery".equals(dt) || "MqBattery-Video".equals(dt)){//新mq协议
                DecimalFormat df=new DecimalFormat("0.0");//设置保留位数
                String format = df.format((float) volume / 10);
                String str = "{\"MI\":\""+inInfo.getMI()+"\",\"AT\":\"ChangeVoice\",\"VO\":\""+format+"\",\"TS \":\""+DateUtil.Date2TimeStamp(new Date())+"\"}";
                pubMsgl.publish(str,UUID.randomUUID().toString(),"SERVER/EQUIPMENT/"+inInfo.getMI());
                result.put("code" ,3);
                result.put("msg" ,"操作成功！");
            }else {
                result.put("msg" ,"该设备不支持该协议！");
            }
        }catch (Exception e){
            result.put("msg" ,"系统异常："+e.getMessage());
        }

        return result;
    }

    /** 批量操作-设备绑定视频 */
    @Override
    public Map<String, Object> batchEqBindVideo(String batchVid, String batchEq ,Long operator ,Integer ti) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);

        if (batchVid==null || batchEq==null || "".equals(batchVid) || "".equals(batchEq)){
            result.put("msg" ,"请选择需要绑定的视频和设备");
            return result;
        }

        try {
            //转换视频id
            JSONArray jsonArray = JSON.parseArray(batchVid);
            List<String> vids = jsonArray.toJavaList(String.class);

            //转换设备id
            JSONArray jsonArray2 = JSON.parseArray(batchEq);
            List<String> eqs = jsonArray2.toJavaList(String.class);

            if (vids==null || vids.size()<=0){
                result.put("msg" ,"请选择你需要绑定的视频！");
                return result;
            }

            if (eqs==null || eqs.size()<=0){
                result.put("msg" ,"请选择你需要绑定的设备！");
                return result;
            }

            //判断所绑定的视频数是否超过后台的设置
            Integer baseSetVideoNum = videoDao.findBaseSetVideoNum();//查询系统最大视频数
            if (baseSetVideoNum==null){
                result.put("msg" ,"请设置后台的视频绑定最大限制！");
                return result;
            }
            if (vids.size()>baseSetVideoNum){
                result.put("msg" ,"绑定的视频数超过后台设置的最大限制！");
                return result;
            }

            for (int i = 0; i < eqs.size(); i++) {
                String eq = eqs.get(i);
                //查找该设备是否存
                Map bindNum = videoDao.findEqExistAndQueryBindNum(eq);
                //删除已经绑定的记录
                //videoDao.deleFromEqVideoByEqCode(eq);
                if (bindNum!=null && bindNum.get("ID")!=null){
                    //判断该设备是否在线并且是否是视频机
                    String lin = (String) redisTemplate.opsForValue().get(eq + "-Info");
                    if (lin!=null && !"".equals(lin.trim())){
                        IN_Info inInfo = JSON.parseObject(lin, IN_Info.class);//获取心跳钟的数据
                        if ("MqBattery-Video".equals(inInfo.getDD().getDT())){//视频机才可以操作
                            List<Vedeo> oldVedeos = videoDao.findVediosByeq(eq);
                            String v = "";
                            //插入视频绑定数据
                            for (int j= 0; j < vids.size(); j++) {
                                Long vi = Long.parseLong(vids.get(j));
                                //查找该视频
                                Vedeo video = videoDao.findVideoById(vi);
                                if (video!=null){
                                    //插入记录
                                    videoDao.addEqVideoOne(eq ,vi ,(i+1) ,operator);
                                    EqVideo eqVideo = new EqVideo();
                                    eqVideo.setUrl(video.getFileUrl());
                                    eqVideo.setMd5(video.getFileMd5());
                                    //如果没有md5的重新生成MD5
                                    if(StringUtils.isBlank(eqVideo.getMd5())){
                                        String vedioMd5 = Md5Util.fileUrlMd5(video.getFileUrl());
                                        videoDao.updateVideoMd5(vedioMd5, video.getId());
                                        eqVideo.setMd5(vedioMd5);
                                    }
                                    v = v+"\"V"+(j+1)+"\":"+JSON.toJSONString(eqVideo)+",";
                                }
                            }

                            int jj = vids.size();
                            //查询之前数据库中的vidios fileurl md5
                            for(int z=0; z<oldVedeos.size(); z++){
                                Vedeo video = oldVedeos.get(z);
                                if(video != null){
                                    EqVideo eqVideo = new EqVideo();
                                    eqVideo.setUrl(video.getFileUrl());
                                    eqVideo.setMd5(video.getFileMd5());
                                    //如果没有md5的重新生成MD5
                                    if(StringUtils.isBlank(eqVideo.getMd5())){
                                        String vedioMd5 = Md5Util.fileUrlMd5(video.getFileUrl());
                                        videoDao.updateVideoMd5(vedioMd5, video.getId());
                                        eqVideo.setMd5(vedioMd5);
                                    }
                                    v = v+"\"V"+(jj+z+1)+"\":"+JSON.toJSONString(eqVideo)+",";
                                }
                            }

                            //上传默认视频
                            if(StringUtils.isBlank(v)){
                                //上传默认视频 base_setting
                                EqVideo eqVideo = new EqVideo();
                                Map setting = basicsettingDao.findDeVedio();
                                if(setting == null || setting.get("vedio") == null){
                                    result.put("msg" ,"系统默认视频不存在，不能全部删除");
                                    return result;
                                }
                                eqVideo.setUrl(setting.get("vedio")+"");
                                //如果没有md5的重新生成MD5
                                eqVideo.setMd5(setting.get("vedioMd5") == null ? null : setting.get("vedioMd5") + "");
                                if(StringUtils.isBlank(eqVideo.getMd5())){
                                    String vedioMd5 = Md5Util.fileUrlMd5(setting.get("vedio")+"");
                                    basicsettingDao.updateDeVedioMd5(vedioMd5);
                                    eqVideo.setMd5(vedioMd5);
                                }
                                v = v+"\"V1\":"+JSON.toJSONString(eqVideo)+",";
                            }

                            if (!"".equals(v)){
                                ti = ti==null?90000000:ti;
                                //向设备发送指令
                                String str = "{\"MI\":\""+eq+"\",\"AT\":\"ChangeVideos\","+v+"\"TI\":\""+ti+"\",\"TS \":\""+DateUtil.Date2TimeStamp(new Date())+"\"}";
                                logger.info("batchEqBindVideo请求命令："+str);
                                pubMsgl.publish(str,UUID.randomUUID().toString(),"SERVER/EQUIPMENT/"+eq);
                            }
                        }
                    }
                }
            }

            result.put("code" ,3);
            result.put("msg" ,"绑定成功，已为在线设备更新视频");
        }catch (Exception e){
            e.printStackTrace();
            result.put("msg" ,"系统异常！"+e.getMessage());
        }
        return result;
    }

    /** 批量操作-设备绑定视频 */
    @Override
    public Map<String, Object> delEqBindVideo(String batchVid, String eq) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);

        if (batchVid==null || eq==null || "".equals(batchVid) || "".equals(eq)){
            result.put("msg" ,"请选择需要删除的视频和设备");
            return result;
        }

        try {
            //转换视频id
            JSONArray jsonArray = JSON.parseArray(batchVid);
            List<String> vids = jsonArray.toJavaList(String.class);

            if (vids==null || vids.size()<=0){
                result.put("msg" ,"请选择你需要删除的视频！");
                return result;
            }

            Map bindNum = videoDao.findEqExistAndQueryBindNum(eq);
            if(bindNum==null || bindNum.get("ID")==null || bindNum.get("CON")==null || new Integer(bindNum.get("CON")+"") == 0){
                result.put("msg" ,"该设备未绑定视频！");
                return result;
            }

            //查找该设备是否存
            String lin = (String) redisTemplate.opsForValue().get(eq + "-Info");
            if (lin!=null && !"".equals(lin.trim())){
                IN_Info inInfo = JSON.parseObject(lin, IN_Info.class);//获取心跳钟的数据
                if ("MqBattery-Video".equals(inInfo.getDD().getDT())){//视频机才可以操作
                    //插入视频绑定数据
                    for (int j= 0; j < vids.size(); j++) {
                        Long vi = Long.parseLong(vids.get(j));
                        videoDao.delBindVideo(vi, eq);
                    }

                    String v = "";
                    //查询删除后的视频，重新下发
                    List<Vedeo> oldVedeos = videoDao.findVediosByeq(eq);
                    for(int z=0; z<oldVedeos.size(); z++){
                        Vedeo video = oldVedeos.get(z);
                        if(video != null){
                            EqVideo eqVideo = new EqVideo();
                            eqVideo.setUrl(video.getFileUrl());
                            eqVideo.setMd5(video.getFileMd5());
                            //如果没有md5的重新生成MD5
                            if(StringUtils.isBlank(eqVideo.getMd5())){
                                String vedioMd5 = Md5Util.fileUrlMd5(video.getFileUrl());
                                videoDao.updateVideoMd5(vedioMd5, video.getId());
                                eqVideo.setMd5(vedioMd5);
                            }

                            v = v+"\"V"+(z+1)+"\":"+JSON.toJSONString(eqVideo)+",";
                        }
                    }

                    //上传默认视频
                    if(StringUtils.isBlank(v)){
                        //上传默认视频 base_setting
                        EqVideo eqVideo = new EqVideo();
                        Map setting = basicsettingDao.findDeVedio();
                        if(setting == null || setting.get("vedio") == null){
                            result.put("msg" ,"系统默认视频不存在，不能全部删除");
                            return result;
                        }
                        eqVideo.setUrl(setting.get("vedio")+"");
                        //如果没有md5的重新生成MD5
                        eqVideo.setMd5(setting.get("vedioMd5") == null ? null : setting.get("vedioMd5") + "");
                        if(StringUtils.isBlank(eqVideo.getMd5())){
                            String vedioMd5 = Md5Util.fileUrlMd5(setting.get("vedio")+"");
                            basicsettingDao.updateDeVedioMd5(vedioMd5);
                            eqVideo.setMd5(vedioMd5);
                        }
                        v = v+"\"V1\":"+JSON.toJSONString(eqVideo)+",";
                    }

                    if (!"".equals(v)){
                        //向设备发送指令
                        String str = "{\"MI\":\""+eq+"\",\"AT\":\"ChangeVideos\","+v+"\"TI\":\""+90000000+"\",\"TS \":\""+DateUtil.Date2TimeStamp(new Date())+"\"}";
                        logger.info("delEqBindVideo请求命令："+str);
                        pubMsgl.publish(str,UUID.randomUUID().toString(),"SERVER/EQUIPMENT/"+eq);
                    }
                }
            }

            result.put("code" ,3);
            result.put("msg" ,"删除成功，已为在线设备更新视频");
        }catch (Exception e){
            e.printStackTrace();
            result.put("msg" ,"系统异常！"+e.getMessage());
        }
        return result;
    }

    /** 设备绑定默认logo */
    @Override
    public Map<String, Object> binDefalutLogo(String eqCode) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);

        if (eqCode==null || "".equals(eqCode)){
            result.put("msg" ,"设备编号不正确");
            return result;
        }

        //判断该设备是否在线
        //根据设备号查询该设备的心跳是否存在
        String lin = (String) redisTemplate.opsForValue().get(eqCode + "-Info");
        if (lin==null || "".equals(lin.trim())){
            result.put("msg" ,"该设备不在线");
            return result;
        }

        try {
            IN_Info inInfo = JSON.parseObject(lin, IN_Info.class);
            String dt = inInfo.getDD().getDT();
            if ("MqBattery".equals(dt) || "MqBattery-Video".equals(dt)){//新mq协议
                //查找平台默认logo
                String logo = videoDao.findBasicTaltLogo();
                if (logo==null || "".equals(logo.trim())){
                    result.put("msg" ,"请设置平台的默认logo");
                    return result;
                }
                String logoUrl = yuming+":8075/"+logo;
                String str = "{\"MI\":\""+inInfo.getMI()+"\",\"AT\":\"defaulData\",\"DF\":\""+logoUrl+"\"}";
                pubMsgl.publish(str,UUID.randomUUID().toString(),"SERVER/EQUIPMENT/"+inInfo.getMI());
                result.put("code" ,3);
                result.put("msg" ,"操作成功！");
            }else {
                result.put("msg" ,"该设备不支持该协议！");
            }
        }catch (Exception e){
            result.put("msg" ,"系统异常："+e.getMessage());
        }
        return result;
    }

    /** 设置视频机的公共模板 */
    @Override
    public Map<String, Object> settingMovieLocal(Integer upType, String upUrl, Integer centerType, String centerUrl,
                                                 Integer downType, String downUrl) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);
        //判断参数
        if (upType!=null && upType==1 && upUrl==null){
            result.put("msg" ,"第一张图片的文件名为空");
            return result;
        }
        if (centerType!=null && centerType==1 && centerUrl==null){
            result.put("msg" ,"第二张图片的文件名为空");
            return result;
        }
        if (downType!=null && downType==1 && downUrl==null){
            result.put("msg" ,"第三张图片的文件名为空");
            return result;
        }
        MovieLocal local = new MovieLocal();
        //处理第一张
        if (upType!=null){
            String upame = null;
            upame = getStringName(upType, upame);
            MovieLocalDetail up = new MovieLocalDetail(upType ,upame ,upUrl);
            local.setUp(up);
        }
        //处理第二张
        if (centerType!=null){
            String centerName = null;
            centerName = getStringName(centerType, centerName);
            MovieLocalDetail center = new MovieLocalDetail(centerType ,centerName ,centerUrl);
            local.setCenter(center);
        }
        //处理第三张
        if (downType!=null){
            String downName= null;
            downName = getStringName(downType, downName);
            MovieLocalDetail down = new MovieLocalDetail(downType ,downName ,downUrl);
            local.setDown(down);
        }

        if (local!=null && (local.getUp()!=null || local.getCenter()!=null || local.getDown()!=null)){
            basicsettingDao.updateSettingToMovieLocal(JSON.toJSONString(local));
        }

        result.put("code" ,3);
        result.put("msg" ,"操作成功！");
        return result;
    }

    private String getStringName(Integer type, String name) {
        if (type==1){
            name="图片显示";
        }else if (type==2){
            name="logo";
        }else if (type==3){
            name="微信二维码";
        }else if (type==4){
            name="支付宝二维码";
        }else if (type==5){
            name="二码合一";
        }
        return name;
    }

    /** 回显视频机的公共模板 */
    @Override
    public Map<String, Object> getMovieLocal() {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,3);
        String movieLocal = basicsettingDao.findMovieLocal();
        MovieLocal local = new MovieLocal();
        try {
            if (movieLocal!=null && !"".equals(movieLocal)){
                local = JSON.parseObject(movieLocal ,MovieLocal.class);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        result.put("data" ,local);
        return result;
    }
}
