package com.equipment.service.impl;

import com.alibaba.fastjson.JSON;
import com.equipment.dao.*;
import com.equipment.entity.*;
import com.equipment.model.equipmanager.EquipManagePage;
import com.equipment.model.equipmanager.EquipPower;
import com.equipment.model.equipmanager.QueryBundedEquip;
import com.equipment.model.equipmanager.QueryNotBundEquip;
import com.equipment.model.old.Battery;
import com.equipment.model.old.Drivers;
import com.equipment.model.old.IN_Info;
import com.equipment.model.querymodel.FindAgentGradeResModel;
import com.equipment.mqtt.PubMsg;
import com.equipment.service.EquipManageService;
import com.equipment.util.Constants;
import com.equipment.util.DateUtil;
import com.equipment.util.ExcelCommon;
import com.equipment.util.PDFUtil;
import com.equipment.util.excel.ExcelException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @Author: JavaTansanlin
 * @Description: 设备管理相关的业务逻辑
 * @Date: Created in 15:19 2018/8/15
 * @Modified By:
 */
@Service
@Transactional
public class EquipManageServiceImpl implements EquipManageService {

    /** 日志 */
    private Logger log = LoggerFactory.getLogger(getClass());

    /** 设备dao **/
    @Autowired
    private EquipInfoDao equipInfoDao;

    /** 设备类型dao **/
    @Autowired
    private EquipTypeDao equipTypeDao;

    /** 设备心跳dao **/
    @Autowired
    private EquipHeartDetailDao heartDetailDao;

    /** 设备电池dao */
    @Autowired
    private EquipPowerDetailDao powerDetailDao;

    /** 设备与店铺的关系dao */
    @Autowired
    private ShopEquipDao shopEquipDao;

    /** 二维码仓库dao **/
    @Autowired
    private QrcodeStoreDao qrcodeStoreDao;

    /** 访问其它服务采用的模板 **/
    @Autowired
    private RestTemplate restTemplate;

    /** redis操作 */
    @Autowired
    private RedisTemplate redisTemplate;

    /** 发布 */
    @Autowired
    private PubMsg pubMsgl;

    /** 设备管理的分页，条件查询 */
    @Override
    public PageInfo<EquipManagePage> findEquipManagePageAndByOdition(Integer indexPage, Integer pageCount, String code , Integer equipType , Integer state, String shopName , String play , String openId , String agentName, String time){
        PageHelper.startPage(indexPage, pageCount);
        try {
            List<EquipManagePage> ae = new ArrayList<>();//设备记录
            //先判断openId和代理名是否为空
            ae = getEquipManagePages(code, equipType, state, shopName, play, openId, agentName, ae, time);
            //判断是否查询出了设备
            getEqPowerDetail(ae);
            return new PageInfo<EquipManagePage>(ae);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new PageInfo<EquipManagePage>();
    }

    /** 设备管理，条件查询所有，用于导出使用 */
    @Override
    public List<EquipManagePage> findEquipManageByOdition(String code, Integer equipType , Integer state,
                                                   String shopName , String play , String openId , String agentName, String time){
        List<EquipManagePage> ae = new ArrayList<>();//设备记录
        try {
            //先判断openId和代理名是否为空
            ae = getEquipManagePages(code, equipType, state, shopName, play, openId, agentName, ae, time);
            //判断是否查询出了设备
            getEqPowerDetail(ae);
        }catch (Exception e){
            e.printStackTrace();
        }

        return ae;
    }

    /** 查询所有的设备类型 **/
    @Override
    public List<EquipType> findAllEquipType() {
        return equipTypeDao.findAll();
    }

    /** 查看每台设备的电池 */
    @Override
    public Map<Integer,EquipPower> equipPowersDetail(String equipCode) {
        Map<Integer,EquipPower> result = new HashMap<>();
        //获取该设备的卡口数
        int cardNum = equipInfoDao.findCardNumByEquip(equipCode);
        if (cardNum>0) {
            //根据设备号获取redis缓存里面的信息
            String json = (String) redisTemplate.opsForValue().get(equipCode + "-Info");
            IN_Info info = JSON.parseObject(json, IN_Info.class);//获得缓存的心跳数据
            if (info != null) {//心跳数据不为空，说明在线
                for (int i = 0; i < cardNum; i++) {//遍历设备所有的卡口号
                    EquipPower power = new EquipPower();//新建页面的封装对象
                    for (Battery battery : info.getDD().getBL()) {//遍历设备中所有的电池对象
                        if (battery != null && i == Integer.parseInt(battery.getBO())) {//判断所在卡号是否一致
                            power.setBo(battery.getBO());
                            power.setBc(battery.getBC());
                            power.setPowerCode(battery.getBI());
                            power.setSt(battery.getST());
                            power.setWi(battery.getWI());
                        }
                        result.put(i, power);//封装进结果集
                    }
                }
            } else {//心跳数据为空，说明已经离线
                //查询心跳表中的最新一条记录
                EquipHeartDetail heartDetail = heartDetailDao.selectNewHeartByMi(equipCode);
                if (heartDetail!=null){
                    //在电池表中根据心跳id获取电池
                    List<EquipPowerDetail> powerList = powerDetailDao.findListPowerByHeart(heartDetail.getId());
                    if (powerList!=null && powerList.size()>0){//电池集合不为空并且有数据
                        for (int i = 0; i < cardNum; i++) {//遍历设备所有的卡口号
                            EquipPower power = new EquipPower();//新建页面的封装对象
                            for (EquipPowerDetail e:powerList) {//遍历设备中所有的电池对象
                                if (i == Integer.parseInt(e.getBo())){//判断所在卡号是否一致
                                    power.setBo(e.getBo());
                                    power.setBc(e.getBc());
                                    power.setPowerCode(e.getBi());
                                    power.setSt(e.getSt());
                                    power.setWi(e.getWi());
                                }
                            }
                            result.put(i, power);//封装进结果集
                        }
                    }
                }
            }
        }
        return result;
    }

    /** 查询店铺已绑定的设备 **/
    @Override
    public List<QueryBundedEquip> bundedByshopCode(String shopCode) {
        return equipInfoDao.findBundedByshopCode(shopCode);
    }

    /** 查询店铺已绑定的设备 **/
    @Override
    public List<QueryBundedEquip> bundedByshopCode(String shopCode,String code ) {
        return equipInfoDao.findBundedByshopCodeAndCode(shopCode,code);
    }

    /** 查询正常在库的未绑定的设备 **/
    @Override
    public Map<String , Object> findNotBundEquip(Integer indexPage, Integer pageCount ,String code) {
        Map<String , Object> result = new HashMap<>();//结果
        int equip = 0;
        if (code!=null && !"".equals(code)){//如果设备号的查询条件不为空，则先查寻该设备是否存在
            equip = equipInfoDao.findEquipByCode(code);
            if (equip==0){//所查询的设备号不存在
                result.put("code" , 1);
                return result;
            }
        }
        PageHelper.startPage(indexPage, pageCount);
        List<QueryNotBundEquip> notBundEquip = equipInfoDao.findNotBundEquip(code);
        if ( (notBundEquip==null || notBundEquip.size()==0) && equip>0 ){
            result.put("code" , 2);//该设备号已经被绑定或者已经出库，请先解绑或者入库
            return result;
        }
        if (notBundEquip==null || notBundEquip.size()<=0){
            result.put("code" , 1);
            return result;
        }
        result.put("code", 3);
        result.put("page", new PageInfo<QueryNotBundEquip>(notBundEquip));
        return result;
    }

    /** 绑定设备 **/
    @Override
    public Map<String, Object> bundEquip(String shopCode, Long equipId) {
        Map<String , Object> result = new HashMap<>();//结果
        //判断该设备是否在库
        EquipInfo equipInfo = equipInfoDao.findEquipEntityById(equipId);
        if (equipInfo==null || equipInfo.getIsstock()==2){
            result.put("code" ,2);//绑定失败，该设备不在库中
            return result;
        }
        //判断该设备是否已经被绑定
        int shopEquipByEquipId = shopEquipDao.findShopEquipByEquipId(equipId);
        if (shopEquipByEquipId>0){
            result.put("code" ,4);//绑定失败，该设备已经被绑定，请先解绑再绑定
            return result;
        }
        //根据店铺编号，判断该店铺是否存在
        int shopExistByShopCode = shopEquipDao.findShopExistByShopCode(shopCode);
        if (shopExistByShopCode<=0){
            result.put("code" ,5);//绑定失败，该店铺不存在
            return result;
        }
        //判断该设备的二维码是否已经绑定，如未绑定，则调用生成二维码服务接口
        //有则拿出该二维码的id（注意，调用服务的时候，返回的id是否为空）从而与店铺进行绑定
        QrcodeStore qrcode = qrcodeStoreDao.findCountQrcodeByEquipId(equipInfo.getId());
        if (qrcode==null){//未绑定，则调用生成二维码服务接口
            result.put("code" ,13);//该设备未绑定二维码，无法进行店铺绑定
            return result;
        }
        //执行店铺二维码绑定操作
        ShopEquip se = new ShopEquip();
        se.setCode(shopCode);//店铺
        se.setQrcodeStore(qrcode.getId());//二维码
        se.setRegisttime(new Date());//注册时间
        shopEquipDao.insertOne(se);//插入操作
        result.put("code" , 3);//成功
        return result;
    }

    /** 店铺通过扫二维码绑定设备 */
    @Override
    public Map<String, Object> bundEquip(String shopCode, String codeUrl) {
        Map<String ,Object> result = new HashMap<>();
        //判断参数
        if(shopCode==null || codeUrl==null || "".equals(codeUrl) || "".equals(shopCode)){
            result.put("code" ,1);
            return result;//参数不正确
        }
        //查找该二维码是否存在
        List<QrcodeStore> listCode = qrcodeStoreDao.fingCodeByUrl(codeUrl);
        if (listCode==null || listCode.size()!=1 ){
            result.put("code" ,2);
            return result;//查找该二维码错误
        }
        QrcodeStore qrcodeStore = listCode.get(0);//获取二维码
        //判断该二维码是否有已经绑定设备，没有绑定设备不给予操作
        if (qrcodeStore.getEquip()==null){
            result.put("code" ,4);
            return result;//该设备还未绑定二维码，无法进行绑定
        }
        //判断该设备是否已经被绑定
        int shopEquipByEquipId = shopEquipDao.findShopEquipByEquipId(qrcodeStore.getEquip());
        if (shopEquipByEquipId>0){
            result.put("code" ,5);//绑定失败，该设备已经被绑定，请先解绑再绑定
            return result;
        }
        //判断该店铺是否存在
        int shopExistByShopCode = shopEquipDao.findShopExistByShopCode(shopCode);
        if (shopExistByShopCode<=0){
            result.put("code" ,6);//绑定失败，该店铺不存在
            return result;
        }
        //执行绑定操作
        ShopEquip se = new ShopEquip();
        se.setCode(shopCode);//店铺
        se.setQrcodeStore(qrcodeStore.getId());//二维码
        se.setRegisttime(new Date());//注册时间
        shopEquipDao.insertOne(se);//插入操作
        result.put("code" , 3);//成功
        return result;
    }

    public static void main(String[] args) {
        String a = "9999999000000";
        Long b = Long.valueOf(a);
        b = b+ 1;
        System.out.println(b);

    }

    /** 批量绑定设备 **/
    @Override
    public List bundEquipBatch(String equipCode , Integer num , String shopCode) {

        List resultList = new ArrayList();
        for (int i = 0; i < num; i++) {
            Long eqCodeLong = Long.valueOf(equipCode);
            eqCodeLong = eqCodeLong + i;
            Map<String , Object> result = new HashMap<>();//结果
            result.put("equipCode",eqCodeLong.toString());
            //判断该设备是否在库
            EquipInfo equipInfo = equipInfoDao.findEquipEntityByCode(eqCodeLong.toString());
            if (equipInfo==null || equipInfo.getIsstock()==2){
                result.put("code" ,2);//绑定失败，该设备不在库中
                resultList.add(result);
                continue;
            }
            //判断该设备是否已经被绑定
            int shopEquipByEquipId = shopEquipDao.findShopEquipByEquipId(equipInfo.getId());
            if (shopEquipByEquipId>0){
                result.put("code" ,4);//绑定失败，该设备已经被绑定，请先解绑再绑定
                resultList.add(result);
                continue;
            }
            //根据店铺编号，判断该店铺是否存在
            int shopExistByShopCode = shopEquipDao.findShopExistByShopCode(shopCode);
            if (shopExistByShopCode<=0){
                result.put("code" ,5);//绑定失败，该店铺不存在
                resultList.add(result);
                continue;
            }
            //判断该设备的二维码是否已经绑定，如未绑定，则调用生成二维码服务接口
            //有则拿出该二维码的id（注意，调用服务的时候，返回的id是否为空）从而与店铺进行绑定
            QrcodeStore qrcode = qrcodeStoreDao.findCountQrcodeByEquipId(equipInfo.getId());
            if (qrcode==null){//未绑定，则调用生成二维码服务接口
                result.put("code" ,13);//该设备未绑定二维码，无法进行店铺绑定
                resultList.add(result);
                continue;
            }
            //执行店铺二维码绑定操作
            ShopEquip se = new ShopEquip();
            se.setCode(shopCode);//店铺
            se.setQrcodeStore(qrcode.getId());//二维码
            se.setRegisttime(new Date());//注册时间
            shopEquipDao.insertOne(se);//插入操作
            result.put("code" , 3);//成功
            resultList.add(result);
        }
        return resultList;
    }


    /** 批量绑定设备 **/
    @Override
    public List bundEquipBatchNew(String equipCode[] ,String shopCode) {

        List resultList = new ArrayList();
        for (int i = 0; i < equipCode.length; i++) {

            Map<String , Object> result = new HashMap<>();//结果
            result.put("equipCode",equipCode[i]);
            //判断该设备是否在库
            EquipInfo equipInfo = equipInfoDao.findEquipEntityByCode(equipCode[i]);
            if (equipInfo==null || equipInfo.getIsstock()==2){
                result.put("code" ,2);//绑定失败，该设备不在库中
                resultList.add(result);
                continue;
            }
            //判断该设备是否已经被绑定
            int shopEquipByEquipId = shopEquipDao.findShopEquipByEquipId(equipInfo.getId());
            if (shopEquipByEquipId>0){
                result.put("code" ,4);//绑定失败，该设备已经被绑定，请先解绑再绑定
                resultList.add(result);
                continue;
            }
            //根据店铺编号，判断该店铺是否存在
            int shopExistByShopCode = shopEquipDao.findShopExistByShopCode(shopCode);
            if (shopExistByShopCode<=0){
                result.put("code" ,5);//绑定失败，该店铺不存在
                resultList.add(result);
                continue;
            }
            //判断该设备的二维码是否已经绑定，如未绑定，则调用生成二维码服务接口
            //有则拿出该二维码的id（注意，调用服务的时候，返回的id是否为空）从而与店铺进行绑定
            QrcodeStore qrcode = qrcodeStoreDao.findCountQrcodeByEquipId(equipInfo.getId());
            if (qrcode==null){//未绑定，则调用生成二维码服务接口
                result.put("code" ,13);//该设备未绑定二维码，无法进行店铺绑定
                resultList.add(result);
                continue;
            }
            //执行店铺二维码绑定操作
            ShopEquip se = new ShopEquip();
            se.setCode(shopCode);//店铺
            se.setQrcodeStore(qrcode.getId());//二维码
            se.setRegisttime(new Date());//注册时间
            shopEquipDao.insertOne(se);//插入操作
            result.put("code" , 3);//成功
            resultList.add(result);
        }
        return resultList;
    }

    /** 解绑设备 **/
    @Override
    public Map<String, Object> untieEquip(Long equipId) {
        Map<String ,Object> result = new HashMap<>();
        //根据设备号查询二维码
        QrcodeStore qrcode = qrcodeStoreDao.findCountQrcodeByEquipId(equipId);
        if (qrcode==null){//该设备未绑定二维码，无法解绑
            result.put("code",2);
            return result;
        }
        shopEquipDao.deleteByEquip(qrcode.getId());
        result.put("code" ,3);//解绑成功
        return result;
    }


    /** 解绑设备 **/
    @Override
    public List untieEquipBatch(String equipCode , Integer num ){
        List resultList = new ArrayList();
        for (int i = 0; i < num; i++) {
            Map<String ,Object> result = new HashMap<>();
            Long eqCodeLong = Long.valueOf(equipCode);
            eqCodeLong = eqCodeLong + i;
            result.put("equipCode",eqCodeLong.toString());
            //判断该设备是否在库
            EquipInfo equipInfo = equipInfoDao.findEquipEntityByCode(eqCodeLong.toString());
            if (equipInfo==null || equipInfo.getIsstock()==2){
                result.put("code" ,2);//绑定失败，该设备不在库中
                resultList.add(result);
                continue;
            }

            //根据设备号查询二维码
            QrcodeStore qrcode = qrcodeStoreDao.findCountQrcodeByEquipId(equipInfo.getId());
            if (qrcode==null){//该设备未绑定二维码，无法解绑
                result.put("code",2);
                resultList.add(result);
                continue;
            }
            shopEquipDao.deleteByEquip(qrcode.getId());

            result.put("code" ,3);//解绑成功
            resultList.add(result);
        }
        return resultList;
    }


    /** 解绑设备 **/
    @Override
    public List untieEquipBatchNew(String equipCode[],String shopCode){
        List resultList = new ArrayList();
        for (int i = 0; i < equipCode.length; i++) {
            Map<String ,Object> result = new HashMap<>();
            result.put("equipCode",equipCode[i]);
            //判断该设备是否在库
            EquipInfo equipInfo = equipInfoDao.findEquipEntityByCode(equipCode[i]);
            if (equipInfo==null || equipInfo.getIsstock()==2){
                result.put("code" ,2);//绑定失败，该设备不在库中
                resultList.add(result);
                continue;
            }

            //判断该设备是否已经被绑定
            int shopEquipByEquipId = shopEquipDao.findShopEquipByEquipId(equipInfo.getId());
            if ( !(shopEquipByEquipId>0)){
                result.put("code" ,7);//设备未绑定店铺
                resultList.add(result);
                continue;
            }

            //根据设备号查询二维码
            String shopCodeDb = shopEquipDao.findShopByEquipId(equipInfo.getId());
            if (!shopCode.equals(shopCodeDb)){
                result.put("code",6);
                resultList.add(result);
                continue;
            }

            //根据设备号查询二维码
            QrcodeStore qrcode = qrcodeStoreDao.findCountQrcodeByEquipId(equipInfo.getId());
            if (qrcode==null){//该设备未绑定二维码，无法解绑
                result.put("code",2);
                resultList.add(result);
                continue;
            }
            shopEquipDao.deleteByEquip(qrcode.getId());

            result.put("code" ,3);//解绑成功
            resultList.add(result);
        }
        return resultList;
    }


    //代理商后台绑定设备（已经绑定了店铺）
    @Override
    @Transactional
    public Map bindEqForAgent(String[] equipCode ,String shopCode ){
        Map<String ,Object> result = new HashMap<>();
        result.put("code",1);
        List resultList = new ArrayList();
        if(equipCode.length > 0){
            for (int i = 0; i < equipCode.length; i++) {
                Map<String ,Object> map = new HashMap<>();
                map.put("equipCode" ,equipCode[i]);
                /** 解绑设备 **/
                //判断该设备是否在库
                EquipInfo equipInfo = equipInfoDao.findEquipEntityByCode(equipCode[i]);
                if (equipInfo==null || equipInfo.getIsstock()==2){
                    map.put("code" ,2);//绑定失败，该设备不在库中
                    resultList.add(map);
                    continue;
                }

                //根据设备号查询二维码
                String shopCodeDb = shopEquipDao.findShopByEquipId(equipInfo.getId());
                if (!shopCode.equals(shopCodeDb)){
                    map.put("code",6);//店铺为空
                    resultList.add(map);
                    continue;
                }
                //根据设备号查询二维码
                QrcodeStore qrcode = qrcodeStoreDao.findCountQrcodeByEquipId(equipInfo.getId());
                if (qrcode==null){//该设备未绑定二维码，无法解绑
                    map.put("code",2);
                    resultList.add(map);
                    continue;
                }
                shopEquipDao.deleteByEquip(qrcode.getId());
                /** 绑定设备 **/

                //执行店铺二维码绑定操作
                ShopEquip se = new ShopEquip();
                se.setCode(shopCode);//店铺
                se.setQrcodeStore(qrcode.getId());//二维码
                se.setRegisttime(new Date());//注册时间
                shopEquipDao.insertOne(se);//插入操作
                map.put("code" , 3);//成功
                resultList.add(map);
            }
            result.put("data",resultList);
            result.put("code",3);
        }
        return  result;
    }

    /** 把所有的设备生成excel文档，并且返回文件名 */
    @Override
    public String createEqEx(String path ,String code , Integer equipType , Integer state, String shopName , String play , String openId , String agentName, String time){
        String fileName = UUID.randomUUID().toString().replaceAll("-", "")+".xlsx";
        List<EquipManagePage> ae = new ArrayList<>();//设备记录
        // 先判断openId和代理名是否为空 
        ae = getEquipManagePages(code, equipType, state, shopName, play, openId, agentName, ae, time);
        //判断是否查询出了设备
        getEqPowerDetail(ae);
        String[] header = {"设备id","设备号","最后一次心跳", "店铺编号","手机号","设备版本","状态","电池总量","可借数量","可还数量 ","店铺名","所属店铺管理员","所属平台名 ","所属省级代理名","所属市级代理名","所属区域代理名","所属业务代理名"};
        List<Object[]> listO = new ArrayList<>();
        try {
            for (EquipManagePage e:ae) {
                Object[] o ={
                        e.getId() ,e.getCode()==null?"":e.getCode(),e.getScode()==null?"":e.getScode(),
                        e.getRtime()==null?"":e.getRtime(),
                        e.getSd()==null?"":e.getSd(),e.getName()==null?"":e.getName(),e.getState()==1?"在线":"离线",
                        e.getBc()==null?"":e.getBc(),e.getCb()==null?"":e.getCb(),e.getCr()==null?"":e.getCr(),
                        e.getRsname()==null?"":e.getRsname(),e.getManager()==null?"":e.getManager(),e.getApname()==null?"":e.getApname(),
                        e.getPaname()==null?"":e.getPaname(),e.getCaname()==null?"":e.getCaname(),e.getAaname()==null?"":e.getAaname(),e.getSaname()==null?"":e.getSaname()
                };
                listO.add(o);
            }
            ExcelCommon.createExcelModul(path ,fileName ,"设备详情","",header,listO);
        } catch (ExcelException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    /** 把所有的设备生成PDF文档，并且下载 */
    @Override
    public byte[] createEqPDF(String path, String code, Integer equipType, Integer state, String shopName, String play, String openId, String agentName, String time) {
        List<EquipManagePage> ae = new ArrayList<>();//设备记录
        // 先判断openId和代理名是否为空
        ae = getEquipManagePages(code, equipType, state, shopName, play, openId, agentName, ae, time);
        //判断是否查询出了设备
        getEqPowerDetail(ae);
        String[] header = {"设备id","设备号","最后一次心跳", "店铺编号","手机号","设备版本","状态","电池总量","可借数量","可还数量 ","店铺名","所属店铺管理员","所属平台名 ","所属省级代理名","所属市级代理名","所属区域代理名","所属业务代理名"};
        float[] cellsWidths = {30,40,50,50,30,30,30,30,30,45,45,45,45,45,45,45};
        List<String[]> listO = new ArrayList<>();
        for (EquipManagePage e:ae) {
            String[] o ={
                    e.getId()+"" ,e.getCode()==null?"":e.getCode(),e.getScode()==null?"":e.getScode(),
                    e.getRtime()==null?"":e.getRtime(),
                    e.getSd()==null?"":e.getSd(),e.getName()==null?"":e.getName(),e.getState()==1?"在线":"离线",
                    e.getBc()==null?"":e.getBc(),e.getCb()==null?"":e.getCb(),e.getCr()==null?"":e.getCr(),
                    e.getRsname()==null?"":e.getRsname(),e.getManager()==null?"":e.getManager(),e.getApname()==null?"":e.getApname(),
                    e.getPaname()==null?"":e.getPaname(),e.getCaname()==null?"":e.getCaname(),e.getAaname()==null?"":e.getAaname(),e.getSaname()==null?"":e.getSaname()
            };
            listO.add(o);
        }
        return PDFUtil.exportPDF("设备详情", header, listO, cellsWidths);
    }

    private void getEqPowerDetail(List<EquipManagePage> ae) {
        if (ae!=null && ae.size()>0){
            //循环所有的设备，获取心跳相关的数据
            for (EquipManagePage e : ae) {
                //为了兼容历史数据，这里先查询当前心跳表，无数据则查询历史数据表
                if(e.getEhdId() == null){
                    EquipHeartDetail h = heartDetailDao.selectSDBCCBCRByCode(e.getCode());//查询出最新的心跳记录
                    if (h!=null){//有心跳记录的情况下执行下面逻辑
                        e.setSd(h.getSd());//赋值手机号
                        e.setBc(h.getBc());//赋值卡口总数
                        e.setCb(h.getCb());//赋值可借数
                        e.setCr(h.getCr());//赋值可还数
                        e.setTs(h.getTs());//赋值心跳时间
                    }
                }
                //重置心跳时间格式为yyyy-MM-dd HH:mm:ss
                try {
                    if (StringUtils.isBlank(e.getTs())){
                        e.setRtime(Constants.DEFAULT_EQ_HEART_TIME_VALUE);
                    }else{
                        e.setRtime(DateUtil.TimeStamp2Date(e.getTs() ,"yyyy-MM-dd HH:mm:ss"));
                    }
                }catch (Exception e2){
                    e.setRtime("设备心跳时间数据异常");
                }

                //赋值sg信号强度信息，直接从redis中取
                //获取redis里面的数据
                String s= (String) redisTemplate.opsForValue().get(e.getCode()+"-Info");
                if (s!=null && !"".equals(s)){
                    IN_Info in_info = JSON.parseObject(s, IN_Info.class);
                    Drivers dd = in_info.getDD();
                    if (dd!=null){
                        e.setOw(in_info.getOW());//当前连接的wifi
                        e.setSg(dd.getSG()==null?"0":dd.getSG());
                    }
                }

            }
        }
    }

    private List<EquipManagePage> getEquipManagePages(String code, Integer equipType, Integer state, String shopName, String play, String openId, String agentName, List<EquipManagePage> ae,
                                                      String time) {
        //心跳时间
        String startTime = null;
        String endTime = null;
        if (time != null && !"".equals(time)) {
            //时间以 - 截取 -之前为前一部分，-之后为后一部分
            String[] splits = time.split(" - ");
            //设置开始时间 0为前一部分，1为后一部分
            try{
                Date startDate = DateUtil.getDateString(splits[0]);
                startTime = startDate.getTime()+"";
                Date endDate = DateUtil.getDateString(splits[1]);
                endTime = endDate.getTime()+"";
            }catch (Exception e){
                return ae;
            }
        }

        if ( (openId!=null && !"".equals(openId))){//调用查询代理id的方法
            FindAgentGradeResModel agentGrade = equipInfoDao.findAgentGrade(openId, null);
            if (agentGrade!=null && agentGrade.getId()!=null && agentGrade.getMGroup()!=null){
                if (agentGrade.getMGroup()==2){//店铺管理员
                    ae = equipInfoDao.findAllEquipManage2(code, equipType, state, play, shopName, agentGrade.getId(), null, null, null, null ,agentName, startTime, endTime);
                }else if (agentGrade.getMGroup()==3){//业务代
                    ae = equipInfoDao.findAllEquipManage2(code,equipType,state,play,shopName,null,null,null,null,agentGrade.getId() ,agentName, startTime, endTime);
                }else if (agentGrade.getMGroup()==4){//区域代
                    ae = equipInfoDao.findAllEquipManage2(code,equipType,state,play,shopName,null,null,null,agentGrade.getId(),null ,agentName, startTime, endTime);
                }else if (agentGrade.getMGroup()==5){//市级代
                    ae = equipInfoDao.findAllEquipManage2(code,equipType,state,play,shopName,null,null,agentGrade.getId(),null,null ,agentName, startTime, endTime);
                }else if (agentGrade.getMGroup()==6){//省级代
                    ae = equipInfoDao.findAllEquipManage2(code,equipType,state,play,shopName,null,agentGrade.getId(),null,null,null ,agentName, startTime, endTime);
                }
            }
        }else {
            ae = equipInfoDao.findAllEquipManage2(code,equipType,state,play,shopName,null,null,null,null,null ,agentName, startTime, endTime);
        }
        return ae;
    }

    /** 伪删除设备（更新设备的出库状态） */
    @Override
    public Map<String, Object> delete(Long id) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,3);
        //更新字段
        int i = equipInfoDao.updateEqTockState(id);
        //解绑改设备所在的店铺
        Long shopEqIdByEqid = equipInfoDao.findShopEqIdByEqid(id);
        if (shopEqIdByEqid!=null){
            equipInfoDao.deleteShopEq(shopEqIdByEqid);
        }
        result.put("msg" ,"删除成功，删除数量："+i);
        return result;
    }

    /** 查询设备周围的wifi */
    @Override
    public Map<String, Object> findRoundWIFI(String eqCode) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);

        if (eqCode==null || "".equals(eqCode)){
            result.put("msg" ,"设备号不正确！");
            return result;
        }

        //获取redis里面的数据
        String s= (String) redisTemplate.opsForValue().get(eqCode+"-Info");
        if (s==null){
            result.put("msg" ,"该设备不在线！");
            return result;
        }

        //判断设备类型
        IN_Info in_info = JSON.parseObject(s, IN_Info.class);
        if (!"MqBattery".equals(in_info.getDD().getDT()) && !"MqBattery-Video".equals(in_info.getDD().getDT())){
            result.put("msg" ,"该设备不支持wifi连接！");
            return result;
        }
        //判断wifi是否存在
        List<String> wd = in_info.getWD();
        if (wd==null || wd.size()<=0){
            result.put("msg" ,"该设备周围没有wifi信息！");
            return result;
        }

        result.put("code" ,3);
        result.put("msg" ,"操作成功！");
        result.put("wifi" ,wd);
        return result;
    }

    /** 根据账户密码连接wifi */
    @Override
    public Map<String, Object> connectWIFI(String eqCode, String name, String pwd) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);

        if (eqCode==null || "".equals(eqCode)){
            result.put("msg" ,"设备号不正确！");
            return result;
        }

        if (name==null || "".equals(name)){
            result.put("msg" ,"账户密码不正确！");
            return result;
        }

        //获取redis里面的数据
        String s= (String) redisTemplate.opsForValue().get(eqCode+"-Info");
        if (s==null){
            result.put("msg" ,"该设备不在线！");
            return result;
        }

        //判断设备类型
        IN_Info in_info = JSON.parseObject(s, IN_Info.class);
        if (!"MqBattery".equals(in_info.getDD().getDT()) && !"MqBattery-Video".equals(in_info.getDD().getDT())){
            result.put("msg" ,"该设备不支持wifi连接！");
            return result;
        }

        try {
            String str = "{\"MI\":\""+eqCode+"\",\"AT\":\"ConnectWifi\",\"ID\":\""+name+"\",\"PW\":\""+(pwd==null?"":pwd)+"\"}";
            pubMsgl.publish(str,UUID.randomUUID().toString(),"SERVER/EQUIPMENT/"+eqCode);
            result.put("code" ,3);
            result.put("msg" ,"已向设备发送链接wifi请求，请稍后注意设备的连接信息");
        }catch (Exception e){
            result.put("msg" ,"系统异常！");
        }
        return result;
    }

    //更新当前设备心跳表的心跳时间
    @Override
    public void updateCurrentHeartTs(String mi, String ts){
        heartDetailDao.updateCurrentTs(mi, ts);
    }

    @Override
    public String updateEquipHeartTs(Long id){
        EquipInfo e = equipInfoDao.findEqById(id);
        if(e == null){
            return "";
        }

        String tsStr = "";
        String code = e.getCode();//设备编号
        //根据设备编号查询redis中设备心跳信息
        String jsonObject = (String)redisTemplate.opsForValue().get(code+"-Info");
        if (jsonObject!=null && !"".equals(jsonObject)){
            IN_Info in_info = JSON.parseObject(jsonObject, IN_Info.class);
            String ts = in_info.getTS();
            if(StringUtils.isNotBlank(ts)){
                //转换为年月日显示方式
                tsStr = DateUtil.TimeStamp2Date(ts ,"yyyy-MM-dd HH:mm:ss");
            }

            //查询当前心跳数，做对比
            EquipHeartDetail current = heartDetailDao.selectCurrent(code);
            //设备第一次开机，心跳信息还没写入数据库，数据库数据为空
            if(StringUtils.isBlank(ts)){
                return "";
            }

            String cb = "";
            String cr = "";
            String bc = "";
            String dt = "";
            String ll = "";
            String sd = in_info.getSD();
            Drivers dd = in_info.getDD();
            if(dd != null){
                cb = dd.getCB();
                cr = dd.getCR();
                bc = dd.getBC();
                dt = dd.getDT();
                ll = dd.getLL();
            }

            if(current == null){
                //新增 #{te},#{mi},#{ti},#{at},#{sd},#{ts},#{di},#{dt},#{ll},#{bc},#{cb},#{cr},#{cd},#{sc}
                EquipHeartDetail heartDetail = new EquipHeartDetail();
                heartDetail.setTe(in_info.getTE());
                heartDetail.setMi(in_info.getMI());
                heartDetail.setAt(in_info.getAT());
                heartDetail.setSd(in_info.getSD());
                heartDetail.setTs(in_info.getTS());
                heartDetail.setDi(in_info.getMI());
                heartDetail.setDt(dt);
                heartDetail.setLl(ll);
                heartDetail.setBc(bc);
                heartDetail.setCb(cb);
                heartDetail.setCr(cr);
                heartDetailDao.insertCurrent(heartDetail);
            }else{
                //更新心跳当前表
                heartDetailDao.updateCurrentInfo(code, ts, cb, cr, bc, sd);
            }
        }

        return tsStr;
    }
}
