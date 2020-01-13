package com.equipment.service;

import java.util.Map;

/**
 * @Author: JavaTansanlin
 * @Description: 提供给其它服务  业务层接口
 * @Date: Created in 17:10 2018/8/28
 * @Modified By:
 */
public interface OpenService {

    /** 根据微信的ticket查询出设备号 */
    String findEquipCodeByWXTicket(String ticket);

    /** 根据GXQrcode查询设备号 **/
    String findEqCodeByGXQrcode(String gxCode);

    /**
     * 根据GXQrcode查询在线的设备号(包括广告机的逻辑)
     * @param code 对应的是微信的ticket，支付宝的gxCode
     * @param type wx-微信；ali-支付宝
     * @return
     */
    Map<String ,Object> findEqCodeByGXQrcodeAndOnline(String code ,String type);

    /**
     * 提供个app使用：根据二维码的链接查询出设备编号
     */
    Map<String,Object> findEqCodeByString(String codeString, String sources);
}
