package com.equipment.dao;

import com.equipment.entity.EquipLocation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

/**
 * @Author: JavaTansanlin
 * @Description:设备位置dao
 * @Date: Created in 14:52 2018/11/2
 * @Modified By:
 */
@Component
public interface EquipLocationDao {

    /** 增加设备位置记录 */
    @Insert("insert into EQUIP_LOCATION(EQUIP_CODE,LO,LA,UPDATE_TIME) values (#{equipCode},#{lo},#{la},NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertEqLocation(EquipLocation equipLocation);

    /** 根据设备编号查询最新设备位置记录 */
    @Select("SELECT * FROM equip_location WHERE equip_code=#{code} ORDER BY update_time LIMIT 1")
    EquipLocation findNewRecordByEqCode(String code);

    /** 根据id更新设备位置信息 */
    @Update("UPDATE equip_location SET lo=#{lo},la=#{la},update_time=NOW() WHERE ID=#{id}")
    int updateRecord(EquipLocation equipLocation);

}
