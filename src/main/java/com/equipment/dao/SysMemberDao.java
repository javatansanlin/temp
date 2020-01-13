package com.equipment.dao;

import com.equipment.entity.SysMember;

/**
 * 系统用户
 */
public interface SysMemberDao {

    int deleteByPrimaryKey(Long id);

    int insert(SysMember record);

    int insertSelective(SysMember record);

    SysMember selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysMember record);

    int updateByPrimaryKey(SysMember record);
}