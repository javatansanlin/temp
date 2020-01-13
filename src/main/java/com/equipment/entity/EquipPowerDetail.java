package com.equipment.entity;

import lombok.Data;

/**
 * 设备电池详情
 */
@Data
public class EquipPowerDetail {

    /** 主键 **/
    private Long id;

    /** 心跳记录 - EquipInfo 的外键 **/
    private Long heart;

    /** 电池id号 **/
    private String bi;

    /** 电池所在卡口 **/
    private String bo;

    /** 电池电量 **/
    private String bc;

    /** 插线是否正常 **/
    private String wi;

    /** 设备是否正常 **/
    private String st;

    /** 电池租借情况 **/
    private Long borrowState;

    public EquipPowerDetail(){  }

}