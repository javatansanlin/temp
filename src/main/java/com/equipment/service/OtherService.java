package com.equipment.service;

import java.util.Map;

/**
 * @Author: JavaTansanlin
 * @Description:其它业务接口
 * @Date: Created in 11:35 2018/11/1
 * @Modified By:
 */
public interface OtherService {

    /** 获取临时密钥 */
    Map<String,Object> getKey(Integer t);

    /** 把设备心跳表的数据同步更新到设备位置表 */
    Map<String,Object> synlyHealTolocation();
}
