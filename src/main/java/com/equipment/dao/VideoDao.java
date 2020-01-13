package com.equipment.dao;

import com.equipment.entity.Vedeo;
import com.equipment.entity.VedeoLabel;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Author: JavaTansanlin
 * @Description:视频相关的dao
 * @Date: Created in 15:19 2018/12/10
 * @Modified By:
 */
@Component
public interface VideoDao {

    /** 查询视频设备的视频信息 **/
    @Select({
            "<script>",
            "SELECT",
            "eq.ID as id,",
            "eq.CODE AS eqCode,",
            "rs.S_NAME AS shopName,",
            "rs.CODE AS shopCode,",
            "COUNT(ev.ID) AS vnum,",
            "eq.STATE as state,",
            "ei.NAME AS typeName",
            "FROM",
            "equipinfo AS eq",
            "LEFT JOIN equiptype AS ei ON eq.TYPE = ei.ID",
            "LEFT JOIN qrcode_store AS qs ON eq.ID = qs.EQUIP",
            "LEFT JOIN shop_equip AS se ON se.QRCODE_STORE = qs.ID",
            "LEFT JOIN rank_shop AS rs ON rs.CODE = se.CODE",
            "LEFT JOIN agent_plat ap on rs.PLAT = ap.ID",
            "LEFT JOIN agent_relation mana on rs.MANAGER = mana.id",
            "LEFT JOIN agent_relation pa on rs.PROVINCE_AGENT=pa.id",
            "LEFT JOIN agent_relation ca on rs.CITY_AGENT = ca.ID",
            "LEFT JOIN agent_relation aa on rs.AREA_AGENT = aa.ID",
            "LEFT JOIN agent_relation sa on rs.SALES_AGENT = sa.ID",
            "LEFT JOIN equip_vedeo AS ev ON ev.EQ_CODE = eq.CODE",
            "LEFT JOIN vedeo AS v ON v.ID = ev.VEDEO",
            "WHERE ei.ID = 3",
            "<if test=\" eqCode != null and eqCode != ''\">AND eq.CODE = #{eqCode}</if>",
            "<if test=\" vCode != null and vCode != ''\">AND v.FILE_CODE = #{vCode}</if>",
            "<if test=\" vName != null and vName != ''\">AND v.FILE_NAME LIKE concat('%',#{vName},'%')</if>",
            "<if test=\" sCode != null and sCode != ''\">AND rs.CODE = #{sCode}</if>",
            "<if test=\" sName != null and sName != ''\">AND rs.S_NAME LIKE concat('%',#{sName},'%')</if>",
            "<if test=\" agentOid != null and agentOid != ''\">AND (pa.OPENID = #{agentOid} or ca.OPENID = #{agentOid} or aa.OPENID = #{agentOid} or sa.OPENID = #{agentOid} or mana.OPENID = #{agentOid})</if>",
            "GROUP BY eq.ID",
            "ORDER BY eq.STATE ASC, vnum DESC",
            "</script>"
    })
    List<Map> queryEqVideoByDition(@Param("eqCode") String eqCode, @Param("vCode") String vCode, @Param("vName") String vName,
                                   @Param("sCode") String sCode, @Param("sName") String sName, @Param("agentOid") String agentOid);

    /** 查询基础表中的可绑设备数 */
    @Select("SELECT EQUIPVIDEO_NUM FROM basic_setting WHERE ID=1")
    Integer findBaseSetVideoNum();

    /** 查询基础表中的默认视频 */
    @Select("SELECT DEFAULT_EQUIPVIDEO FROM basic_setting WHERE ID=1")
    String findBseSetDefaultVideo();

    /** 查询基础表中的默认视频 */
    @Select("SELECT DEFAULT_EQUIPVIDEO_MD5 FROM basic_setting WHERE ID=1")
    String findBseSetDefaultVideoMd5();

    /** 查询视频标签名字是否存在 */
    @Select("SELECT COUNT(ID) FROM vedeo_label WHERE LABEL_NAME=#{name}")
    Integer findLableIseixtByName(String name);

    /** 插入一条视频标签数据 */
    @Insert("INSERT INTO vedeo_label(label_name ,createtime ,operator) VALUES (#{labelName},NOW() ,#{operator});")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int addLabelOne(VedeoLabel vedeoLabel);

    /** 查询所有的视频标签 */
    @Select("SELECT * FROM vedeo_label")
    List<VedeoLabel> findAllVideoLabel();

    /** 查询视频名字是否存在 */
    @Select("SELECT COUNT(ID) FROM vedeo WHERE FILE_NAME = #{name}")
    Integer findVideoNameIsExit(String name);

    /** 插入一条视频数据 */
    @Insert("INSERT INTO vedeo(file_name,file_code,file_url,createtime,label,isdele,file_size,operator,file_type,file_md5) " +
            "VALUES (#{fileName},#{fileCode},#{fileUrl},NOW(),#{label},#{isdele},#{fileSize},#{operator},#{fileType},#{fileMd5});")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int addVideoOne(Vedeo vedeo);

    /** 查询视频列表 */
    @Select({
            "<script>",
            "SELECT",
            "ve.ID AS id,",
            "ve.FILE_CODE as fileCode,",
            "ve.FILE_NAME as fileName,",
            "ve.FILE_URL as fileUrl,",
            "ve.FILE_SIZE as fileSize,",
            "ve.FILE_TYPE as fileType,",
            "COUNT(ev.ID) AS evnum,",
            "ve.CREATETIME as createtime,",
            "sm.ACCOUNT_NAME as operator",
            "FROM",
            "vedeo AS ve",
            "LEFT JOIN sys_member AS sm ON sm.ID = ve.OPERATOR",
            "LEFT JOIN equip_vedeo AS ev ON ev.VEDEO = ve.ID",
            "LEFT JOIN equipinfo AS eq ON eq.CODE = ev.EQ_CODE",
            "LEFT JOIN qrcode_store AS qs ON qs.EQUIP = eq.ID",
            "LEFT JOIN shop_equip AS se ON se.QRCODE_STORE = qs.ID",
            "LEFT JOIN rank_shop AS rs ON rs.CODE = se.CODE",
            "LEFT JOIN agent_relation mana on rs.MANAGER = mana.id",
            "LEFT JOIN agent_relation pa on rs.PROVINCE_AGENT=pa.id",
            "LEFT JOIN agent_relation ca on rs.CITY_AGENT = ca.ID",
            "LEFT JOIN agent_relation aa on rs.AREA_AGENT = aa.ID",
            "LEFT JOIN agent_relation sa on rs.SALES_AGENT = sa.ID",
            "WHERE 1=1",
            "<if test=\" eqCode != null and eqCode != ''\">AND eq.CODE = #{eqCode}</if>",
            "<if test=\" vCode != null and vCode != ''\">AND ve.FILE_CODE = #{vCode}</if>",
            "<if test=\" vName != null and vName != ''\">AND ve.FILE_NAME LIKE concat('%',#{vName},'%')</if>",
            "<if test=\" shopCode != null and shopCode != ''\">AND rs.CODE = #{shopCode}</if>",
            "<if test=\" shopName != null and shopName != ''\">AND rs.S_NAME LIKE concat('%',#{shopName},'%')</if>",
            "<if test=\" agentOid != null and agentOid != ''\">AND (pa.OPENID = #{agentOid} or ca.OPENID = #{agentOid} or aa.OPENID = #{agentOid} or sa.OPENID = #{agentOid} or mana.OPENID = #{agentOid})</if>",
            "GROUP BY ve.ID",
            "ORDER BY ve.ID DESC",
            "</script>"
    })
    List<Map> findVideoDetail(@Param("eqCode") String eqCode ,@Param("vCode") String vCode ,
                              @Param("vName") String vName ,@Param("shopCode") String shopCode ,
                              @Param("shopName") String shopName ,@Param("agentOid") String agentOid);

    /** 根据视频id查询是否存在并且查询已绑定的设备数 */
    @Select("SELECT v.*,COUNT(ev.ID) AS CON FROM vedeo AS v,equip_vedeo AS ev WHERE ev.VEDEO=v.ID AND v.ID = #{id}")
    Map findVideoIsExitAndEqNumByVID(Long id);

    /** 删除指定的视频文件 */
    @Delete("DELETE FROM vedeo WHERE ID=#{id}")
    int deleVideo(Long id);

    /** 删除指定的视频文件 */
    @Delete("DELETE FROM equip_vedeo WHERE VEDEO=#{id} AND EQ_CODE = #{eqCode}")
    int delBindVideo(@Param("id") Long id,
                  @Param("eqCode") String eqCode);

    /** 设备绑定视频-->查找视频 */
    @Select({
            "<script>",
            "SELECT",
            "v.ID AS id,",
            "v.FILE_CODE AS fileCode,",
            "v.FILE_NAME AS fileName,",
            "v.FILE_URL AS fileUrl,",
            "vl.LABEL_NAME AS labelName,",
            "(SELECT COUNT(ID) FROM equip_vedeo AS ev WHERE ev.EQ_CODE=#{eqCode} AND ev.VEDEO = v.ID) AS isBan",
            "FROM",
            "vedeo AS v",
            "LEFT JOIN vedeo_label AS vl ON vl.ID = v.LABEL",
            "WHERE 1=1 ",
            "<if test=\" vName != null and vName != ''\">AND v.FILE_NAME LIKE concat('%',#{vName},'%')</if>",
            "<if test=\" vCode != null and vCode != ''\">AND v.FILE_CODE = #{vCode}</if>",
            "ORDER BY v.ID DESC",
            "</script>"
    })
    List<Map> findCanBanVideoByEqCodeAndCondition(@Param("eqCode") String eqCode ,
                                                  @Param("vName") String vName ,@Param("vCode") String vCode);

    /** 根据设备编号查询该设备是否存在，并且查询出该设备绑定视频数 */
    @Select("SELECT eq.ID,COUNT(ev.ID) AS CON FROM equipinfo AS eq LEFT JOIN equip_vedeo AS ev ON ev.EQ_CODE = eq.CODE WHERE eq.CODE=#{eqCode} GROUP BY eq.ID")
    Map findEqExistAndQueryBindNum(String eqCode);

    /** 插入设备视频关系记录 */
    @Insert("INSERT INTO equip_vedeo(EQ_CODE,VEDEO,PRIORITY,CREATETIME,OPERATOR) VALUES (#{eqCode},#{vid},#{priority},NOW(),#{op})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int addEqVideoOne(@Param("eqCode") String eqCode ,@Param("vid") Long vid ,@Param("priority") Integer priority ,@Param("op") Long op);

    @Select({
            "<script>",
            "SELECT v.ID AS id, v.FILE_URL AS fileUrl, v.FILE_MD5 AS fileMd5 FROM equip_vedeo e",
            "LEFT JOIN vedeo v ON e.VEDEO = v.ID",
            "WHERE e.EQ_CODE = #{eqCode}",
            "</script>"
    })
    List<Vedeo> findVediosByeq(@Param("eqCode") String eqCode);

    /** 根据视频id查询该视频信息 */
    @Select("SELECT * FROM vedeo WHERE ID=#{id}")
    Vedeo findVideoById(Long id);

    /** 根据id查询视频绑定设备记录 */
    @Select("SELECT * FROM equip_vedeo WHERE ID = #{id}")
    Map findEqVideoById(Long id);

    /** 根据id删除视频绑定设备记录 */
    @Delete("DELETE FROM equip_vedeo WHERE ID=#{id}")
    int deleEqVideoById(Long id);

    @Update("UPDATE vedeo SET FILE_MD5 = #{md5} WHERE ID = #{id}")
    int updateVideoMd5(String md5, Long id);

    /** 根据设备编号查询已经绑定的视频信息 */
    @Select({
            "<script>",
            "SELECT",
            "ev.ID AS id,",
            "v.ID AS vid,",
            "v.FILE_CODE AS fileCode,",
            "V.FILE_NAME AS fileName,",
            "v.FILE_URL AS fileUrl,",
            "v.FILE_MD5 AS fileMd5,",
            "v.FILE_SIZE AS fileSize,",
            "ev.CREATETIME AS createtime,",
            "sm.ACCOUNT_NAME AS operator",
            "FROM",
            "vedeo AS v",
            "LEFT JOIN equip_vedeo AS ev ON ev.VEDEO = v.ID",
            "LEFT JOIN sys_member AS sm ON sm.ID = ev.ID",
            "WHERE ev.EQ_CODE=#{eqCode}",
            "</script>"
    })
    List<Map> findEqBindVideoDetail(String eqCode);

    /**
     * 根据设备code删除视频设备的关系
     */
    @Delete("DELETE FROM equip_vedeo WHERE eq_code=#{eqCode}")
    int deleFromEqVideoByEqCode(String eqCode);

    /** 查询平台默认logo */
    @Select("SELECT BASIC_LOGO FROM basic_setting WHERE ID=1")
    String findBasicTaltLogo();

}
