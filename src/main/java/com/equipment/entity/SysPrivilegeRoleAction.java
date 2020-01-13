package com.equipment.entity;

/**
 * 系统权限角色功能
 */
public class SysPrivilegeRoleAction {

    /** 主键 **/
    private Long id;

    /** 编号 **/
    private String code;

    /** 所属角色 - 引用 SysPrivilegeRole 的主键为外键 **/
    private Long role;

    /** 所属功能 - 引用 SysPrivileAction 的主键为外键 **/
    private Long action;

    public SysPrivilegeRoleAction(){ }

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

    public Long getRole() {
        return role;
    }

    public void setRole(Long role) {
        this.role = role;
    }

    public Long getAction() {
        return action;
    }

    public void setAction(Long action) {
        this.action = action;
    }
}