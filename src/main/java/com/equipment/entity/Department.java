package com.equipment.entity;

import java.util.Date;

/**
 * 部门
 */
public class Department {

    /** 主键 **/
    private Long id;

    /** 操作员 - SysMember外键 **/
    private Long operator;

    /** 部门名称 **/
    private String name;

    /** 部门老大 **/
    private Long boss;

    /** 注册时间 **/
    private Date registime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOperator() {
        return operator;
    }

    public void setOperator(Long operator) {
        this.operator = operator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Long getBoss() {
        return boss;
    }

    public void setBoss(Long boss) {
        this.boss = boss;
    }

    public Date getRegistime() {
        return registime;
    }

    public void setRegistime(Date registime) {
        this.registime = registime;
    }
}