package com.equipment.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Author: JavaTansanlin
 * @Description: 设备指令
 * @Date: Created in 18:26 2018/8/14
 * @Modified By:
 */
@Data
public class EquipInstruction {

    /** 主键 **/
    private Long id;

    /** 设备号外键 **/
    private Long equip;

    /** 指令类型 **/
    private String type;

    /** 服务端发送的指令 **/
    private String serverJson;

    /** 设备端发送的指令 **/
    private String equipJson;

    /** 服务端发送指令的时间 **/
    private Date serverJsonTime;

    /** 设备端发送指令的时间 **/
    private Date equipJsonTime;

}
