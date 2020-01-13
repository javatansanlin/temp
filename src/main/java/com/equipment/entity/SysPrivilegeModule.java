package com.equipment.entity;

/**
 * 系统权限模块
 */
public class SysPrivilegeModule {

    /** 主键 **/
    private Long id;

    /** 编号 **/
    private String code;

    /** 名称 **/
    private String name;

    /** 标志 **/
    private String flag;

    /** 优先级 - 1-99 **/
    private Integer level;

    /** 是否有效 - ‘1’= 有；‘2’= 无效； **/
    private Integer isvalid;

    /** 上级模块 **/
    private Long parentModule;

    public SysPrivilegeModule(){ }

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

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag == null ? null : flag.trim();
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getIsvalid() {
        return isvalid;
    }

    public void setIsvalid(Integer isvalid) {
        this.isvalid = isvalid;
    }

    public Long getParentModule() {
        return parentModule;
    }

    public void setParentModule(Long parentModule) {
        this.parentModule = parentModule;
    }
}