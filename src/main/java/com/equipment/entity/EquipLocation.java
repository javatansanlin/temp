package com.equipment.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Author: JavaTansanlin
 * @Description:设备位置类
 * @Date: Created in 17:36 2018/11/1
 * @Modified By:
 */
@Data
public class EquipLocation {
    /** id主键 */
    private Long id;
    /** 设备号 */
    private String equipCode;
    /** 经度 */
    private Double lo;
    /** 纬度 */
    private Double la;
    /** 时间 */
    private Date updateTime;

    public EquipLocation() {
    }

    public EquipLocation(String equipCode, Double lo, Double la) {
        this.equipCode = equipCode;
        this.lo = lo;
        this.la = la;
    }

    public EquipLocation(Long id, String equipCode, Double lo, Double la, Date updateTime) {
        this.id = id;
        this.equipCode = equipCode;
        this.lo = lo;
        this.la = la;
        this.updateTime = updateTime;
    }
}
