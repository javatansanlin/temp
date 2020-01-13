package com.equipment.dao;

import com.equipment.entity.SysPrivilegeRole;

/**
 * 系统权限角色
 */
public interface SysPrivilegeRoleDao {

    int deleteByPrimaryKey(Long id);

    int insert(SysPrivilegeRole record);

    int insertSelective(SysPrivilegeRole record);

    SysPrivilegeRole selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysPrivilegeRole record);

    int updateByPrimaryKey(SysPrivilegeRole record);
}