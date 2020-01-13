package com.equipment.dao;

import com.equipment.entity.SysPrivilegeModule;

/**
 * 系统权限模块
 */
public interface SysPrivilegeModuleDao {

    int deleteByPrimaryKey(Long id);

    int insert(SysPrivilegeModule record);

    int insertSelective(SysPrivilegeModule record);

    SysPrivilegeModule selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysPrivilegeModule record);

    int updateByPrimaryKey(SysPrivilegeModule record);
}