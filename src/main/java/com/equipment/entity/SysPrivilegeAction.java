package com.equipment.entity;

/**
 * 系统权限功能
 */
public class SysPrivilegeAction {

    /** 主键 */
    private Long id;

    /** 引用系统权限的主键为外键 - SysPrivilegeModule **/
    private Long module;

    /** 编号 **/
    private String code;

    /** 名称 **/
    private String name;

    /** 是否有效 - ‘1’= 有；‘2’= 无效； **/
    private Integer isvalid;

    public SysPrivilegeAction(){ }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getModule() {
        return module;
    }

    public void setModule(Long module) {
        this.module = module;
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

    public Integer getIsvalid() {
        return isvalid;
    }

    public void setIsvalid(Integer isvalid) {
        this.isvalid = isvalid;
    }
}