package com.equipment.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: JavaTansanlin
 * @Description:
 * @Date: Created in 14:03 2019/11/5
 * @Modified By:
 */
@Repository
public interface TestDao {

    @Select("SELECT CODE FROM equipinfo WHERE (TYPE=1 OR TYPE=2 OR TYPE=3)")
    List<String> findAllEq();

    @Update("UPDATE equipinfo SET STATE=#{state} WHERE CODE=#{code}")
    int updateState(@Param("state") Integer state ,@Param("code") String code);

}
