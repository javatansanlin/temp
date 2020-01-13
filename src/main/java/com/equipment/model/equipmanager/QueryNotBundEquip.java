package com.equipment.model.equipmanager;

import lombok.Data;

/**
 * @Author: JavaTansanlin
 * @Description: 设备绑定查询未绑定设备的封装
 * @Date: Created in 14:27 2018/8/22
 * @Modified By:
 */
@Data
public class QueryNotBundEquip {
    /** 设备id **/
    private Long id;
    /** 设备编号 **/
    private String code;
    /** 设备类型 **/
    private String type;
    /** 卡口数 **/
    private Integer carnum;
}
