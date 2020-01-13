package com.equipment.entity;

import lombok.Data;

/**
 * 设备信息
 */
@Data
public class EquipInfo {

    /** 主键 **/
    private Long id;

    /** 设备编号 **/
    private String code;

    /** 类型 - EquipType外键 **/
    private Long type;

    /** 属于服务器 **/
    private Long server;

    /** 在线状态 **/
    private Integer state;

    /** 是否在库 1= 在库；2= 出库 **/
    private Integer isstock;
}