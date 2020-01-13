package com.equipment.entity;

/**
 * 系统权限角色
 */
public class SysPrivilegeRole {

    /** 主键 **/
    private Long id;

    /** 编号 **/
    private String code;

    /** 名称 **/
    private String name;

    public SysPrivilegeRole(){ }

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
}