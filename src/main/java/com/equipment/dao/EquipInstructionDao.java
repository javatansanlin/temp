package com.equipment.dao;

import com.equipment.entity.EquipInstruction;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

/**
 * @Author: JavaTansanlin
 * @Description: 指令表dao
 * @Date: Created in 15:04 2018/8/16
 * @Modified By:
 */
@Component
public interface EquipInstructionDao {

    /** 设备端租借指令结果的插入 **/
    @Update("update EQUIP_INSTRUCTION set EQUIP_JSON = #{json} , EQUIP_JSON_TIME = NOW() where id = #{id}")
    int insertByBorrowEquip(@Param("id") Long id , @Param("json") String json);

    /** 插入设备和服务端的归还指令 **/
    @Insert("insert into EQUIP_INSTRUCTION(equip,type,SERVER_JSON,EQUIP_JSON,SERVER_JSON_TIME,EQUIP_JSON_TIME) values ((select id from EQUIPINFO where code = #{code}),#{e.type},#{e.serverJson},#{e.equipJson},#{e.serverJsonTime},#{e.equipJsonTime})")
    @Options(useGeneratedKeys = true, keyProperty = "e.id", keyColumn = "ID")
    int insert(@Param("e") EquipInstruction equipInstruction , @Param("code") String code);

}
