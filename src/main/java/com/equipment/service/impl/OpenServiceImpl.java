package com.equipment.service.impl;

import com.equipment.dao.BasicsettingDao;
import com.equipment.dao.EquipInfoDao;
import com.equipment.dao.QrcodeStoreDao;
import com.equipment.entity.EquipInfo;
import com.equipment.entity.QrcodeStore;
import com.equipment.entity.RankShop;
import com.equipment.service.OpenService;
import com.equipment.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Author: JavaTansanlin
 * @Description: 提供给其它服务  业务层实现类
 * @Date: Created in 17:12 2018/8/28
 * @Modified By:
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OpenServiceImpl implements OpenService {

    /** 设备相关的dao */
    @Autowired
    private EquipInfoDao equipInfoDao;
    /** 二维码相关的dao */
    @Autowired
    private QrcodeStoreDao qrcodeStoreDao;
    /** redis操作 */
    @Autowired
    private RedisTemplate redisTemplate;
    /** 基础参数dao */
    @Autowired
    private BasicsettingDao basicsettingDao;

    /** 根据微信的ticket查询出设备号 */
    @Override
    public String findEquipCodeByWXTicket(String ticket) {
        if (ticket!=null || !"".equals(ticket)){
            EquipInfo equip = equipInfoDao.findEquipByQrcodeTicket(ticket);
            if (equip!=null){
                return equip.getCode();
            }
        }
        return null;
    }

    /** 根据GXQrcode查询设备号 **/
    @Override
    public String findEqCodeByGXQrcode(String gxCode) {
        if (gxCode!=null || !"".equals(gxCode)){
            EquipInfo equip = equipInfoDao.findEquipByGXcode(gxCode);
            if (equip!=null){
                return equip.getCode();
            }
        }
        return null;
    }

    /**
     * 根据GXQrcode查询在线的设备号(包括广告机的逻辑)
     * @param code 对应的是微信的ticket，支付宝的gxCode
     * @param type wx-微信；ali-支付宝；
     * @return
     */
    @Override
    public Map<String ,Object> findEqCodeByGXQrcodeAndOnline(String code ,String type) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);

        if (code==null || "".equals(code.trim()) || type==null || (!"wx".equals(type) && !"ali".equals(type)) ){
            result.put("msg" ,"参数异常，查找设备失败！！");
            return result;
        }

        //根据gxCode获取二维码信息
        QrcodeStore qrcodeStore = new QrcodeStore();
        if ("ali".equals(type)){
            //支付宝的二维码查询方式
            qrcodeStore = qrcodeStoreDao.findQrcodeByAliGxCode(code);
        }else {
            //微信的查询方式
            qrcodeStore = qrcodeStoreDao.findQrcodeByWeChatTicket(code);
        }
        if (qrcodeStore==null){
            result.put("msg" ,"查找二维码失败，请联系管理员，祝您生活愉快！");
            return result;
        }
        //判断二维码的oneCode是否为空，为空则添加oneCode进去
        if (qrcodeStore.getOneQrcode()==null || "".equals(qrcodeStore.getOneQrcode())){
            String uid = UUID.randomUUID().toString().replaceAll("-", "");
            qrcodeStoreDao.updateOneCodeByID(uid ,qrcodeStore.getId());
        }
        //设备编号
        String eqCode = null;
        //根据二维码编号判断是否是大型广告机，判断首字母是否为：G
        if (qrcodeStore.getCode().startsWith("G")){
            //判断该大型设备下的设备是否存在
            List<String> gCodeByCode = qrcodeStoreDao.findGCodeByCode(qrcodeStore.getCode());
            if (gCodeByCode==null || gCodeByCode.size()<=0){
                result.put("msg" ,"该广告机设备的二维码暂无绑定设备信息，或设备不在线，请联系管理员，祝您生活愉快！");
                return result;
            }
            //记录电池数
            Long battryNum = 0L;
            //遍历设备，获取心跳中设备信息，获取设备电池最多的设备号
            for (String c:gCodeByCode) {
                //获取心跳
                String lin = (String) redisTemplate.opsForValue().get(c + "-Info");
                if (lin != null) {
                    //获取电池链中的个数
                    Long size = redisTemplate.opsForList().size(c + "-Power");
                    if (size != null && size > battryNum) {
                        battryNum = size;
                        eqCode = c;
                    }
                }
            }
            if (eqCode!=null && battryNum==0){
                result.put("msg" ,"很抱歉，该设备没有可借的充电宝！！");
                return result;
            }else if (eqCode==null){
                result.put("msg" ,"该广告机设备不在线，请联系管理员，祝您生活愉快！");
                return result;
            }else {
                qrcodeStore = qrcodeStoreDao.findQrcodeByEquipCode(eqCode);
                if (qrcodeStore==null){
                    result.put("msg" ,"该广告机二维码查找失败，请联系管理员，祝您生活愉快！");
                    return result;
                }
            }
        }else {
            //普通的视频设备，根据二维码查询设备
            EquipInfo equipInfo = qrcodeStoreDao.findEqByQsCode(qrcodeStore.getId());
            if (equipInfo==null){
                result.put("msg" ,"该二维码未与任何设备绑定，请联系管理员，祝您生活愉快！");
                return result;
            }
            Integer state = equipInfo.getState();
            Integer isstock = equipInfo.getIsstock();
            if (state==null || state!=1){
                result.put("msg" ,"该设备不在线，请联系管理员，祝您生活愉快！");
                return result;
            }
            if (isstock==null || isstock!=1){
                result.put("msg" ,"该设备不在库，请联系管理员，祝您生活愉快！");
                return result;
            }
            //获取心跳,心跳存在才是在线
            String lin = (String) redisTemplate.opsForValue().get(equipInfo.getCode() + "-Info");
            if (lin != null) {
                eqCode = equipInfo.getCode();
            }
        }
        if (eqCode==null){
            result.put("msg" ,"该设备不在线，请联系管理员，祝您生活愉快！");
            return result;
        }
        result.put("code" ,3);
        result.put("eqCode" ,eqCode);//设备编号
        result.put("qrCode" ,qrcodeStore);//二维码信息
        result.put("msg" ,"成功");
        return result;
    }

    /**
     * 提供个app使用：根据二维码的链接查询出设备编号
     */
    @Override
    public Map<String, Object> findEqCodeByString(String codeString, String sources) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);

        try {
            //判断参数
            if (codeString==null || sources==null || "".equals(codeString.trim()) || (!"XCX".equals(sources) && !"APP".equals(sources)) ){
                result.put("msg" ,"所需要的参数错误");
                return result;
            }

            //处理url转义
            codeString = URLDecoder.decode(codeString, "UTF-8");

            //判断来源，走不同的业务流程
            EquipInfo eq = new EquipInfo();
            QrcodeStore qrcodeStore = null;

            if (codeString.indexOf("alipay.com") >=0){//支付宝链接
                qrcodeStore = qrcodeStoreDao.findQrcodeByAliCode(codeString.substring((codeString.lastIndexOf("/")+1)));
            }else if (codeString.indexOf("weixin.qq.com") >=0){//微信链接
                qrcodeStore = qrcodeStoreDao.findQrcodeByWeChatCode(codeString);
            }else {//二码合一链接
                //切割位置
                int i = codeString.lastIndexOf("oneCode=") == -1 ? 0 : codeString.lastIndexOf("oneCode=") + 8;
                qrcodeStore = qrcodeStoreDao.findQrcodeByOneCode(codeString.substring(i));
            }

            if (qrcodeStore==null){
                result.put("msg" ,"找不到该二维码");
                return result;
            }

            //根据二维码编号判断是否是大型广告机，判断首字母是否为：G
            if (qrcodeStore.getCode().startsWith("G")){
                //判断该大型设备下的设备是否存在
                List<String> gCodeByCode = qrcodeStoreDao.findGCodeByCode(qrcodeStore.getCode());
                if (gCodeByCode==null || gCodeByCode.size()<=0){
                    result.put("msg" ,"该广告机设备的二维码暂无绑定设备信息，或设备不在线，请联系管理员，祝您生活愉快！");
                    return result;
                }
                //记录电池数
                Long battryNum = 0L;
                //遍历设备，获取心跳中设备信息，获取设备电池最多的设备号
                for (String c:gCodeByCode) {
                    //获取心跳
                    String lin = (String) redisTemplate.opsForValue().get(c + "-Info");
                    if (lin != null) {
                        //获取电池链中的个数
                        Long size = redisTemplate.opsForList().size(c + "-Power");
                        if (size != null && size > battryNum) {
                            battryNum = size;
                            eq.setCode(c);
                        }
                    }
                }
                if ((eq.getCode()!=null && battryNum==0) || eq.getCode()==null){
                    result.put("msg" ,"很抱歉，该设备没有可借的充电宝！！");
                    return result;
                }
            }else {
                if (qrcodeStore.getEquip()==null){
                    result.put("msg" ,"该二维码未绑定设备");
                    return result;
                }
                //查找设备
                eq = equipInfoDao.findEqById(qrcodeStore.getEquip());
                if (eq==null || eq.getCode()==null){
                    result.put("msg" ,"查找设备错误");
                    return result;
                }
            }
            //根据设备编号查询店铺信息
            RankShop shop = equipInfoDao.findShopDetailByEqCode(eq.getCode());
            if (shop==null){
                result.put("msg" ,"该设备未绑定店铺");
                return result;
            }

            //处理店铺押金问题
            if (shop.getShopDeposit()==null){
                shop.setShopDeposit(basicsettingDao.findDePosit());
            }

            //处理收费时间标准显示
            String unitMinuteStr = DateUtil.getShopUnitMinute(shop.getRentCost(), shop.getUnitMinute());
            shop.setUnitMinuteStr(unitMinuteStr);
            result.put("code" ,3);
            result.put("eqCode" ,eq.getCode());
            result.put("eqType" ,eq.getType());
            result.put("shop" ,shop);
            result.put("msg" ,"成功");
        }catch (Exception e){
            e.printStackTrace();
            result.put("msg" ,"系统异常");
        }

        return result;
    }

    public static void main(String[] args) {
        System.out.println("https://qr.alipay.com/pvx02586xsininni86fsf72".substring(("https://qr.alipay.com/pvx02586xsininni86fsf72".lastIndexOf("/")+1)));
    }

}
