package com.equipment.dao;

import com.equipment.entity.ServerList;

/**
 * 服务器列表
 */
public interface ServerListDao {

    int deleteByPrimaryKey(Long id);

    int insert(ServerList record);

    int insertSelective(ServerList record);

    ServerList selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ServerList record);

    int updateByPrimaryKey(ServerList record);
}