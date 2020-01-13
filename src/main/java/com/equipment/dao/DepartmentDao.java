package com.equipment.dao;

import com.equipment.entity.Department;
import org.apache.ibatis.annotations.Select;

/**
 * 部门
 */
public interface DepartmentDao {

    @Select("select * from DEPARTMENT where ID = #{id}")
    Department selectByPrimaryKey(Long id);

}