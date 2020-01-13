package com.equipment.service.impl;

import com.alibaba.fastjson.JSON;
import com.equipment.dao.*;
import com.equipment.entity.AgentEquip;
import com.equipment.entity.EquipHeartDetail;
import com.equipment.entity.QrcodeStore;
import com.equipment.entity.ShopEquip;
import com.equipment.model.equipmanager.EquipBanQueryByAgent;
import com.equipment.model.equipmanager.EquipManagePage;
import com.equipment.model.old.Drivers;
import com.equipment.model.old.IN_Info;
import com.equipment.model.querymodel.QueryAgentEquipModel;
import com.equipment.model.querymodel.QueryAgentShopEqModel;
import com.equipment.service.AgentEquipManageService;
import com.equipment.util.Constants;
import com.equipment.util.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Author: JavaTansanlin
 * @Description: 代理设备管理 业务层实现类
 * @Date: Created in 12:06 2018/8/28
 * @Modified By:
 */
@Service
@Transactional
public class AgentEquipManageServiceImpl implements AgentEquipManageService {

    /** 代理设备的dao */
    @Autowired
    private AgentEquipDao agentEquipDao;

    /** 设备dao **/
    @Autowired
    private EquipInfoDao equipInfoDao;

    /** 设备心跳dao **/
    @Autowired
    private EquipHeartDetailDao heartDetailDao;

    /** 二维码dao */
    @Autowired
    private QrcodeStoreDao qrcodeStoreDao;

    /** redis操作 */
    @Autowired
    private RedisTemplate redisTemplate;

    /** 设备与店铺的关系dao */
    @Autowired
    private ShopEquipDao shopEquipDao;

    /** 代理商管理dao */
    @Autowired
    private AgentManagerDao agentManagerDao;

    public static void main(String[] args) {
        String a = "http://wx.yundianba.club/one/ttOne?oneCode=6339730966647366765535182";
        System.out.println(a.substring(a.indexOf("one/ttOne?oneCode=")+18));
    }

    /** 代理绑定设备（根据代理的id和扫描的出来的二维码id） */
    @Override
    public Map<String, Object> agentBindEquip(Long agentId, String codeUrl) {
        Map<String ,Object> result = new HashMap<>();
        // 判断参数
        if (agentId==null || codeUrl==null || agentId<=0 || "".equals(codeUrl)){
            result.put("code" , 1);//参数不正确
            return  result;
        }
        //处理链接
        if (codeUrl.indexOf("one/ttOne?oneCode=")>0){
            codeUrl = codeUrl.substring(codeUrl.indexOf("one/ttOne?oneCode=")+18);
        }
        //查询该二维码是否存在
        List<QrcodeStore> listCode = qrcodeStoreDao.fingCodeByUrl(codeUrl);
        if (listCode==null || listCode.size()!=1 ){
            result.put("code" ,2);
            return result;//查找该二维码错误
        }
        QrcodeStore qrcodeStore = listCode.get(0);
        if (qrcodeStore.getEquip()==null){
            result.put("code" , 4);//该设备还未绑定二维码，无法进行绑定
            return  result;
        }
        // 查询该二维码是否已经绑定过代理
        AgentEquip entity = agentEquipDao.findEntityByQrcodeId(qrcodeStore.getId());
        if (entity!=null){
            result.put("code" , 5);//该设备已经绑定，无法再次绑定
            return  result;
        }
        // 执行插入数据
        AgentEquip newAE = new AgentEquip();
        newAE.setAgentId(agentId);
        newAE.setQrcodeStore(qrcodeStore.getId());
        newAE.setRegisttime(new Date());
        agentEquipDao.insertOne(newAE);
        result.put("code" , 3);//绑定成功
        return  result;
    }

    /** 查询代理名下的设备，根据设备状态和设备编号为查询条件 */
    @Override
    public PageInfo<QueryAgentEquipModel> findAgentEquip(Integer indexPage, Integer pageCount,Long agentId, String eqCode, Integer state) {
        PageHelper.startPage(indexPage, pageCount);
        List<QueryAgentEquipModel> ae = agentEquipDao.findEquipByAgentOrCodeOrState(agentId ,eqCode ,state);//设备记录
        //查询出最新的心跳记录
        if (ae!=null && ae.size()>0){
            //循环所有的设备，获取心跳相关的数据
            for (int i = 0; i < ae.size(); i++) {
                QueryAgentEquipModel e = ae.get(i);
                //获取redis里面的数据
                String s= (String) redisTemplate.opsForValue().get(ae.get(i).getCode()+"-Info");
                if (s!=null && !"".equals(s)){
                    IN_Info in_info = JSON.parseObject(s, IN_Info.class);
                    Drivers dd = in_info.getDD();
                    if (dd!=null){
                        e.setBonum(dd.getCB());//赋值可借数
                        String ts = in_info.getTS();
                        if (ts!=null && !"".equals(ts))
                            e.setTime(DateUtil.TimeStamp2Date(ts,"yyyy-MM-dd HH:mm:ss"));
                        ae.set(i,e);//替换原来的元素
                    }
                }else {//离线便查询最新的心跳记录
                    EquipHeartDetail h = heartDetailDao.selectSDBCCBCRByCode(ae.get(i).getCode());//查询出最新的心跳记录
                    if (h!=null){//有心跳记录的情况下执行下面逻辑
                        e.setBonum(h.getCb());//赋值可借数量
                        String ts = h.getTs();//获取心跳的时间戳
                        if (ts!=null && !"".equals(ts))
                            e.setTime(DateUtil.TimeStamp2Date(ts,"yyyy-MM-dd HH:mm:ss"));
                        ae.set(i,e);//替换原来的元素
                    }
                }

            }
        }
        return new PageInfo<QueryAgentEquipModel>(ae);
    }

    /** 查询代理店铺的设备，根据传进来的代理分组查询 */
    @Override
    public PageInfo<QueryAgentShopEqModel> findAgentShopEq(Integer indexPage, Integer pageCount, String openid, Integer mgc ,String eqCode) {
        if (mgc==null || mgc!=2 && mgc!=3 && mgc!=4 && mgc!=5 && mgc!=6){//判断参数
             return new PageInfo<QueryAgentShopEqModel>();
        }
        PageHelper.startPage(indexPage, pageCount);
        List<QueryAgentShopEqModel> ae = agentEquipDao.findAgentShopEq(openid, mgc ,eqCode);//执行查询的dao
        //查询出最新的心跳记录
        if (ae!=null && ae.size()>0){
            //循环所有的设备，获取心跳相关的数据
            for (int i = 0; i < ae.size(); i++) {
                QueryAgentShopEqModel e = ae.get(i);
                //获取redis里面的数据
                String s= (String) redisTemplate.opsForValue().get(ae.get(i).getCode()+"-Info");
                if (s!=null && !"".equals(s)){
                    IN_Info in_info = JSON.parseObject(s, IN_Info.class);
                    Drivers dd = in_info.getDD();
                    if (dd!=null){
                        e.setCb(dd.getCB());//赋值可借数
                        e.setCr(dd.getCR());//赋值可还数
                        ae.set(i,e);//替换原来的元素
                    }
                }else {//离线便查询最新的心跳记录
                    EquipHeartDetail h = heartDetailDao.selectSDBCCBCRByCode(ae.get(i).getCode());//查询出最新的心跳记录
                    if (h!=null){//有心跳记录的情况下执行下面逻辑
                        e.setCb(h.getCb());//赋值可借数量
                        e.setCr(h.getCr());//赋值可还数
                        ae.set(i,e);//替换原来的元素
                    }
                }

            }
        }
        return new PageInfo<QueryAgentShopEqModel>(ae);
    }

    /**
     * 【代理商系统】-->代理商下的设备查询分页（根据名下的店铺进行查询）
     */
    @Override
    public Map<String, Object> findMyAgentShopEq(Integer pageNum, Integer pageSize, String code, Integer equipType, Integer state, String shopName, Long agent, String time) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);
        if (pageNum==null || pageNum<=0){
            pageNum = 1;
        }
        if (pageSize==null || pageSize<=0){
            pageSize = 15;
        }
        //查询代理等级
        Integer group = agentManagerDao.findAgentGroupByAgentId(agent);
        if (group==null){
            result.put("msg" ,"代理商查找错误！！");
            return result;
        }

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
                result.put("msg" ,"心跳时间不正确！！");
                return result;
            }
        }

        PageHelper.startPage(pageNum, pageSize);
        List<EquipManagePage> ae = new ArrayList<>();//设备记录

        //查询列表数据
        ae = getEquipManagePages(code, equipType, state, shopName, agent, group, startTime, endTime, ae);

        //封装页面显示
        getEqPowerDetail(ae);


        result.put("code" ,3);
        result.put("data" ,new PageInfo<EquipManagePage>(ae));
        return result;
    }

    /** 设备管理，条件查询所有，用于导出使用 */
    @Override
    public List<EquipManagePage> findEquipManageByOdition(String code, Integer equipType , Integer state,
                                                          String shopName , Long agent, String time){
        List<EquipManagePage> ae = new ArrayList<>();//设备记录
        //查询代理等级
        Integer group = agentManagerDao.findAgentGroupByAgentId(agent);
        if (group==null){
            return ae;
        }

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
                return  ae;
            }
        }

        try {
            //查询列表数据
            ae = getEquipManagePages(code, equipType, state, shopName, agent, group, startTime, endTime, ae);

            //封装页面显示
            getEqPowerDetail(ae);
        }catch (Exception e){
            e.printStackTrace();
        }

        return ae;
    }

    //根据参数查询设备列表
    private List<EquipManagePage> getEquipManagePages(String code, Integer equipType, Integer state, String shopName, Long agent, Integer group, String startTime, String endTime,
                                                      List<EquipManagePage> ae){
        if (group==2){//店铺管理员
            ae = equipInfoDao.findAllEquipManage(code, equipType, state, null, shopName, agent, null, null, null, null, startTime, endTime);
        }else if (group==3){//业务代
            ae = equipInfoDao.findAllEquipManage(code,equipType,state,null,shopName,null,null,null,null,agent, startTime, endTime);
        }else if (group==4){//区域代
            ae = equipInfoDao.findAllEquipManage(code,equipType,state,null,shopName,null,null,null,agent,null, startTime, endTime);
        }else if (group==5){//市级代
            ae = equipInfoDao.findAllEquipManage(code,equipType,state,null,shopName,null,null,agent,null,null, startTime, endTime);
        }else if (group==6){//省级代
            ae = equipInfoDao.findAllEquipManage(code,equipType,state,null,shopName,null,agent,null,null,null, startTime, endTime);
        }
        return ae;
    }

    //封装页面显示
    private void getEqPowerDetail(List<EquipManagePage> ae) {
        if (ae!=null && ae.size()>0){
            //循环所有的设备，获取心跳相关的数据
            for (EquipManagePage e : ae) {
                //为了兼容历史数据，这里先查询当前心跳表，无数据则查询历史数据表
                if(StringUtils.isBlank(e.getSd()) || StringUtils.isBlank(e.getBc()) || StringUtils.isBlank(e.getCr())
                        || StringUtils.isBlank(e.getCb())){
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
                if (StringUtils.isBlank(e.getTs())){
                    e.setRtime(Constants.DEFAULT_EQ_HEART_TIME_VALUE);
                }else{
                    e.setRtime(DateUtil.TimeStamp2Date(e.getTs() ,"yyyy-MM-dd HH:mm:ss"));
                }
            }
        }
    }

    /**
     * 【代理商系统】-->代理商绑定设备
     */
    @Override
    public Map<String, Object> agentBanEq(Long agent, String eqCode ,String shopCode) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);
        //查询代理等级
        Integer group = agentManagerDao.findAgentGroupByAgentId(agent);
        if (group==null){
            result.put("msg" ,"代理商查找错误！！");
            return result;
        }
        //查询该设备号（包括代理条件）
        EquipBanQueryByAgent codeAndAgentId = agentManagerDao.findEqContByCodeAndAgentId(eqCode, group, agent);
        if (codeAndAgentId==null || codeAndAgentId.getShopEqId()==null){
            result.put("msg" ,"找不到该设备！！");
            return result;
        }
        //判断是否绑定二维码
        if (codeAndAgentId.getQrCodeId()==null){
            result.put("msg" ,"到该设备没有绑定二维码，请先绑定二维码！！");
            return result;
        }
        //删除该设备原先的代理商设备信息
        agentManagerDao.deleShopEquip(codeAndAgentId.getShopEqId());
        //执行绑定操作
        ShopEquip se = new ShopEquip();
        se.setCode(shopCode);//店铺
        se.setQrcodeStore(codeAndAgentId.getQrCodeId());//二维码
        se.setRegisttime(new Date());//注册时间
        shopEquipDao.insertOne(se);//插入操作
        result.put("code" ,3);
        result.put("msg" ,"绑定成功！！");
        return result;
    }

    /**
     * 查看代理商总设备数，和在线设备数
     */
    @Override
    public Map<String, Object> findEqNumAndAll(Long agent) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);
        //查询代理等级
        Integer group = agentManagerDao.findAgentGroupByAgentId(agent);
        if (group==null){
            result.put("msg" ,"代理商查找错误！！");
            return result;
        }
        //查询
        Integer onlinNum = 0;
        Integer allNum = 0;
        if (group==2){//店铺管理员
            allNum = equipInfoDao.findAgentEqNum(null, agent, null, null, null, null);
            onlinNum = equipInfoDao.findAgentEqNum(1, agent, null, null, null, null);
        }else if (group==3){//业务代
            allNum = equipInfoDao.findAgentEqNum(null,null,null,null,null,agent);
            onlinNum = equipInfoDao.findAgentEqNum(1, null, null, null, null, agent);
        }else if (group==4){//区域代
            allNum = equipInfoDao.findAgentEqNum(null,null,null,null,agent,null);
            onlinNum = equipInfoDao.findAgentEqNum(1, null, null, null, agent, null);
        }else if (group==5){//市级代
            allNum = equipInfoDao.findAgentEqNum(null,null,null,agent,null,null);
            onlinNum = equipInfoDao.findAgentEqNum(1, null, null, agent, null, null);
        }else if (group==6){//省级代
            allNum = equipInfoDao.findAgentEqNum(null,null,agent,null,null,null);
            onlinNum = equipInfoDao.findAgentEqNum(1, null, agent, null, null, null);
        }
        result.put("code" ,3);
        result.put("onlinNum" ,onlinNum);
        result.put("allNum" ,allNum);
        result.put("msg" ,"成功!!");
        return result;
    }
}
