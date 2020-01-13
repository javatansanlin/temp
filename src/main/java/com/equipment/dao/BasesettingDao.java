package com.equipment.dao;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @Author: JavaTansanlin
 * @Description:
 * @Date: Created in 10:02 2019/8/16
 * @Modified By:
 */
@Repository
public interface BasesettingDao {

    @Select("SELECT POWER_ERR_LIMIT FROM basic_setting WHERE ID=1")
    Integer findPowerErrorLimit();

}
