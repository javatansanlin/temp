package com.equipment.model.querymodel;

import lombok.Data;

/**
 * @Author: JavaTansanlin
 * @Description: 设备与二维码绑定  查询设备的页面显示model
 * @Date: Created in 18:17 2018/8/28
 * @Modified By:
 */
@Data
public class InitEQ2EquipQueryModel {
    /** 设备id */
    private Long id;
    /** 设备编号 */
    private String code;
    /** 最后心跳时间 */
    private String time;
}
