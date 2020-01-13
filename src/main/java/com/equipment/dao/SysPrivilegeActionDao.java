package com.equipment.dao;

import com.equipment.entity.SysPrivilegeAction;

/**
 * 系统权限功能
 */
public interface SysPrivilegeActionDao {
    int deleteByPrimaryKey(Long id);

    int insert(SysPrivilegeAction record);

    int insertSelective(SysPrivilegeAction record);

    SysPrivilegeAction selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysPrivilegeAction record);

    int updateByPrimaryKey(SysPrivilegeAction record);
}