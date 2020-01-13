package com.equipment.dao;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface EquipMacDao {
    //保存MAC地址
    @Insert("INSERT INTO equip_mac(MI, MAC, VE, UUID, SD) VALUES (#{mi}, #{mac}, #{ve}, #{uuid}, #{sd})")
    int insert(@Param("mi")String mi, @Param("mac")String mac, @Param("ve")String ve, @Param("uuid")String uuid, @Param("sd")String sd);

    @Select("SELECT ID from equip_mac WHERE MI = #{mi}")
    Long selectMacByCode(String mi);

    @Update("UPDATE equip_mac SET uuid = #{uuid} WHERE ID = #{id}")
    int updateMacUUID(@Param("uuid")String uuid, @Param("id")Long id);

    //保存物料信息
    @Insert({
            "<script>",
            "INSERT INTO equip_material(material_type, video_duration, third_monitor_url, expiration_time, material_height, ",
            "material_size, material_id, request_id, material_md5, content, material_width, mac_id, material_count, uuid, newFlag)",
            "VALUES(#{param.material_type},#{param.video_duration},#{param.third_monitor_url},#{param.expiration_time},#{param.material_height},",
            "#{param.material_size},#{param.material_id},#{param.request_id},#{param.material_md5},#{param.content},#{param.material_width},#{param.mac_id}, #{param.material_count}, #{param.uuid}, #{param.newFlag})",
            "</script>"
    })
    int insertMaterial(@Param("param") Map param);

    @Update("UPDATE equip_material SET newFlag = #{newFlag} WHERE UUID = #{uuid}")
    int updateMaterialNewFlag(@Param("newFlag")Integer newFlag, @Param("uuid")String uuid);

    @Update("UPDATE equip_material SET total_count = #{totalCount} WHERE ID = #{id}")
    int updateMaterialTotalCount(@Param("totalCount")Integer totalCount, @Param("id")Long id);

    //删除之前的物料信息
    @Delete("DELETE FROM equip_material WHERE mac_id = #{id}")
    int deleteMaterial(Long id);

    @Select({
            "<script>",
            "SELECT em.MI mi, em.MAC mac, em.ID id, em.VE ve,",
            "(SELECT COUNT(1) FROM equip_material ema WHERE ema.mac_id = em.ID AND ema.newFlag = 1) count",
            "FROM equip_mac em WHERE 1=1 ",
            "<if test=\"mi!=null and mi != ''\" >",
            "AND (SELECT COUNT(1) FROM equip_material ema WHERE ema.mac_id = em.ID AND ema.newFlag = 1) > #{count}",
            "</if>",
            "<if test=\"mi!=null and mi != ''\" > and em.MI=#{mi} </if>",
            "</script>"
    })
    List<Map> findEquipPage(@Param("mi") String mi, @Param("count") Integer count);

    @Select({
            "<script>",
            "SELECT em.MI mi, em.MAC mac, em.ID id, em.VE ve, area.CN_NAME areaName, em.MAC mac, rs.S_ADDRESS address, em.VE ve,",
            "city.CN_NAME cityName, pro.CN_NAME proName",
            "from equip_mac em",
            "LEFT JOIN equipinfo eq ON em.MI = eq.CODE",
            "LEFT JOIN qrcode_store qs ON eq.ID = qs.EQUIP",
            "LEFT JOIN shop_equip se ON qs.ID = se.qrcode_store",
            "LEFT JOIN rank_shop rs ON rs.CODE = se.CODE",
            "LEFT JOIN area area ON rs.AREA = area.ID",
            "LEFT JOIN city city ON area.CITY = city.ID",
            "LEFT JOIN province pro ON pro.ID = city.PROVINCE",
            "WHERE 1=1 ",
            "<if test=\"mi!=null and mi != ''\" >",
            "AND (SELECT COUNT(1) FROM equip_material ema WHERE ema.mac_id = em.ID AND ema.newFlag = 1) > #{count}",
            "</if>",
            "<if test=\"mi!=null and mi != ''\" > and em.MI=#{mi} </if>",
            "</script>"
    })
    List<Map> findEquipAll(@Param("mi") String mi, @Param("count") Integer count);

    @Select("SELECT ema.material_md5 material_md5, ema.content content, ema.material_id material_id, ema.material_count material_count, ema.total_count total_count, ema.uuid uuid, em.MI mi FROM equip_mac em LEFT JOIN equip_material ema ON em.ID = ema.mac_id WHERE em.ID = #{id} AND ema.newFlag = 1")
    List<Map> findMaterials(Long id);

    @Select("SELECT mac, uuid FROM equip_mac WHERE ID = #{id}")
    Map selectMac(Long id);

    @Select({
            "<script>",
            "SELECT ema.ID id, ema.material_id material_id, ema.material_md5 material_md5, ema.request_id request_id, ema.material_count material_count, ema.total_count total_count",
            "FROM equip_material ema ",
            "LEFT JOIN equip_mac em ON em.ID = ema.mac_id ",
            "WHERE em.MAC = #{mac} AND em.UUID = ema.UUID",
            "ORDER BY ema.material_count",
            "</script>"
    })
    List<Map> selectMateialId(String mac);

    @Select("SELECT MAC FROM equip_mac WHERE SD = #{sd}")
    String selectMacBySD(String sd);


}
