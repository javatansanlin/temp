package com.equipment.service;

import java.util.Map;

/**
 * @Author: JavaTansanlin
 * @Description: 小程序相关的接口
 * @Date: Created in 15:22 2018/10/11
 * @Modified By:
 */
public interface SmallRoutineService {

    /**
     * 根据坐标点查询附近点的设备
     */
    Map<String ,Object> findRoundEq(Double lo , Double la);

}
