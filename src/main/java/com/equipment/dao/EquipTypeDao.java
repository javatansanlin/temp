package com.equipment.dao;

import com.equipment.entity.EquipType;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 设备类型 dao
 */
@Component
public interface EquipTypeDao {

    /** 查询所有的设备类型 */
    @Select("SELECT * FROM equiptype")
    List<EquipType> findAll();

    /** 根据版本,和电池卡口查询出对应的设备类型 **/
    @Select("SELECT * FROM equiptype WHERE VERSION = #{type} and CARD_NUM=#{num}")
    EquipType findTypeByVersion(@Param("type") int type ,@Param("num") int num);

}