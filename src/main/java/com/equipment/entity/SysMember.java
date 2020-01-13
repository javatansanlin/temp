package com.equipment.entity;

/**
 * 系统用户
 */
public class SysMember {

    /** 主键 **/
    private Long id;

    /** 账户名称 **/
    private String accountName;

    /** 外部标识ID号 **/
    private String openid;

    /** 管理员姓名 **/
    private String realName;

    /** 电话 **/
    private String phone;

    /** 邮箱 **/
    private String email;

    /** 账号状态 **/
    private Integer statue;

    /** 角色 - SysPrivilegeRole 的外键 **/
    private Long role;

    /** 操作员 - 引用自身主键 **/
    private Long operator;

    /** 所属部门 - Department 的外键 **/
    private Long department;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName == null ? null : accountName.trim();
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid == null ? null : openid.trim();
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName == null ? null : realName.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public Integer getStatue() {
        return statue;
    }

    public void setStatue(Integer statue) {
        this.statue = statue;
    }

    public Long getRole() {
        return role;
    }

    public void setRole(Long role) {
        this.role = role;
    }

    public Long getOperator() {
        return operator;
    }

    public void setOperator(Long operator) {
        this.operator = operator;
    }

    public Long getDepartment() {
        return department;
    }

    public void setDepartment(Long department) {
        this.department = department;
    }
}