package com.equipment.dao;

import com.equipment.entity.SysPrivilegeRoleAction;

/**
 * 系统权限角色功能
 */
public interface SysPrivilegeRoleActionDao {

    int deleteByPrimaryKey(Long id);

    int insert(SysPrivilegeRoleAction record);

    int insertSelective(SysPrivilegeRoleAction record);

    SysPrivilegeRoleAction selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysPrivilegeRoleAction record);

    int updateByPrimaryKey(SysPrivilegeRoleAction record);
}