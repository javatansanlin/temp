package com.equipment.service;

import java.util.Map;

/**
 * @Author: JavaTansanlin
 * @Description: 设备的超级管理  业务层接口
 * @Date: Created in 16:20 2018/9/1
 * @Modified By:
 */
public interface SuperEqManageService {

    /** 弹出需要检修的电池 */
    Map<String ,Object> overhaulToPower(String[] powers ,String eqCode);

    /** 重启或者关机操作 type:off-关机操作，rest-重启操作*/
    Map<String ,Object> offOrRest(String eqCode ,String type);

    /** 弹出设备的所有电池 */
    Map<String ,Object> ohAllPower(String eqCode);
}
