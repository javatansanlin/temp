package com.equipment.dao;


import com.equipment.entity.EquipPowerDetail;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 设备电池详情
 */
@Component
public interface EquipPowerDetailDao {

    /** 根据电池编号查询电池信息 */
    @Select("SELECT * FROM equip_power_detail WHERE BI=#{bi}")
    EquipPowerDetail findPowerByBi(String bi);

    /** 更新记录 */
    @Update("UPDATE EQUIP_POWER_DETAIL SET HEART=#{heart},BO=#{bo},BC=#{bc},WI=#{wi},ST=#{st},BORROW_STATE=2 WHERE BI=#{bi}")
    int updateOnePower(EquipPowerDetail equipPowerDetail);

    /** 插入一条记录 */
    @Insert("insert into EQUIP_POWER_DETAIL(HEART,BI,BO,BC,WI,ST,BORROW_STATE) values (#{HEART},#{BI},#{BO},#{BC},#{WI},#{ST},#{BORROW_STATE})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertOnePower(EquipPowerDetail equipPowerDetail);

    /** 更新电池的心跳 **/
    @Update({
            "<script>",
            "update EQUIP_POWER_DETAIL set HEART = #{hearId} ",
            "<where>",
            "BI in",
            "<foreach item='item' index='index' collection='listBi' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</where>",
            "</script>"
    })
    int batchUpdateThePowerInHearBybi(@Param("listBi") List<String> listBi , @Param("hearId") Long hearId);

    /** 根据心跳id查询所有的电池 */
    @Select("SELECT * FROM equip_power_detail WHERE HEART = #{heartId}")
    List<EquipPowerDetail> findListPowerByHeart(@Param("heartId") Long heartId);

}