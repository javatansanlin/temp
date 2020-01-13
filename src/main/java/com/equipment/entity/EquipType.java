package com.equipment.entity;

/**
 * 设备类型
 */
public class EquipType {

    /** 主键 */
    private Long id;

    /** 类型编号 */
    private String code;

    /** 类型名称 */
    private String name;

    /** 卡口数目 */
    private Integer cardNum;

    /** 版本 ‘1’= 电池版；‘2’= 单机版； */
    private Integer version;

    public EquipType(){  }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getCardNum() {
        return cardNum;
    }

    public void setCardNum(Integer cardNum) {
        this.cardNum = cardNum;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}