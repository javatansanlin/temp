package com.equipment.model.equipmanager;

import lombok.Data;

/**
 * @Author: JavaTansanlin
 * @Description: 设备电池，封装给页面显示用
 * @Date: Created in 17:19 2018/8/20
 * @Modified By:
 */
@Data
public class EquipPower {

    /** 电池编号 **/
    private String powerCode;
    /** 所在卡口 **/
    private String bo;
    /** 电池电量 **/
    private String bc;
    /** 插线是否正常 **/
    private String wi;
    /** 设备是否正常 **/
    private String st;

    public EquipPower() {
    }
    public EquipPower(String powerCode, String bo, String bc, String wi, String st) {
        this.powerCode = powerCode;
        this.bo = bo;
        this.bc = bc;
        this.wi = wi;
        this.st = st;
    }
}
