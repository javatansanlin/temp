package com.equipment.dao;

import com.equipment.entity.EquipHeartDetail;
import com.equipment.entity.EquipLocation;
import com.equipment.model.querymodel.FindRoundEqModel;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 设备心跳详情
 */
@Component
public interface EquipHeartDetailDao {

    /** 根据主键删除 **/
    @Delete("delete from equip_heart_detail where ID = #{id}")
    int deleteByPrimaryKey(Long id);

    /** 插入一条记录,利用了反射机制 */
    //@InsertProvider(type = SqlProvider.class,method = "insertEquipHeartDetail")@Param("equipHeartDetail")
    @Insert("insert into equip_heart_detail(te,mi,ti,at,sd,ts,di,dt,ll,bc,cb,cr,cd,sc) values (#{te},#{mi},#{ti},#{at},#{sd},#{ts},#{di},#{dt},#{ll},#{bc},#{cb},#{cr},#{cd},#{sc})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(EquipHeartDetail equipHeartDetail);

    //保存心跳数据到当前心跳表
    @Insert("insert into EQUIP_HEART_DETAIL_CURRENT(te,mi,ti,at,sd,ts,di,dt,ll,bc,cb,cr,cd,sc,videos) values (#{te},#{mi},#{ti},#{at},#{sd},#{ts},#{di},#{dt},#{ll},#{bc},#{cb},#{cr},#{cd},#{sc},#{videos})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertCurrent(EquipHeartDetail equipHeartDetail);

    //更新设备心跳时间
    @Update("update EQUIP_HEART_DETAIL_CURRENT set TS = #{ts} where MI =  #{mi}")
    int updateCurrentTs(@Param("mi") String mi, @Param("ts") String ts);

    //更新设备当前心跳信息
    @Update({
            "<script>",
            "update EQUIP_HEART_DETAIL_CURRENT set TS = #{ts} ",
            "<if test=\"cb != null and cb != ''\">, CB = #{cb} </if>" ,
            "<if test=\"cr != null and cr != ''\">, CR = #{cr} </if>" ,
            "<if test=\"bc != null and bc != ''\">, BC = #{bc} </if>" ,
            "<if test=\"sd != null and sd != ''\">, SD = #{sd} </if>" ,
            "where MI =  #{mi}",
            "</script>"
    })
    int updateCurrentInfo(@Param("mi") String mi, @Param("ts") String ts,
                          @Param("cb") String cb, @Param("cr") String cr,
                          @Param("bc") String bc, @Param("sd") String sd);

    //从当前心跳表中查询设备心跳信息，根据设备编号
    @Select("select * from EQUIP_HEART_DETAIL_CURRENT where MI = #{mi}")
    EquipHeartDetail selectCurrent(@Param("mi") String mi);

    @Delete("delete from EQUIP_HEART_DETAIL_CURRENT where id = #{id}")
    int deleteCurrent(@Param("id") Long id);

    /** 查询一条记录 **/
    @Select("select * from equip_heart_detail where ID = #{id}")
    EquipHeartDetail selectByPrimaryKey(Long id);

    /** 根据设备唯一标识查询出记录 */
    @Select("select * from equip_heart_detail where MI = #{mi}")
    List<EquipHeartDetail> selectByMi(String mi);

    /** 根据设备编号获取心跳记录,只获取到：卡口数，可借数，可还数，手机号码 **/
    @Select("select SD,BC,CB,CR,LL,TS from equip_heart_detail where MI = #{mi} ORDER BY ID DESC LIMIT 1")
    EquipHeartDetail selectSDBCCBCRByCode(String mi);

    /** 根据设备编号查询设备的最新心跳 **/
    @Select("SELECT * FROM equip_heart_detail WHERE MI=#{mi} ORDER BY ID DESC LIMIT 1;")
    EquipHeartDetail selectNewHeartByMi(String mi);

    /** 根据坐标点查询附近点的设备（查的是心跳表） */
    @Select("SELECT " +
            "SUBSTRING_INDEX(GROUP_CONCAT(eq.ID ORDER BY eq.ID DESC),',',1) AS ID,eq.MI,eq.CB,eq.LO,lA,rs.S_NAME,rs.S_LOGO,rs.RENT_COST,ei.STATE " +
            "FROM " +
            "equip_heart_detail AS eq " +
            "LEFT JOIN equipinfo AS ei ON ei.CODE=eq.MI " +
            "LEFT JOIN qrcode_store AS qs ON qs.equip=ei.ID " +
            "LEFT JOIN shop_equip AS se ON se.QRCODE_STORE=qs.ID " +
            "LEFT JOIN rank_shop AS rs ON rs.CODE=se.CODE " +
            "WHERE rs.CODE IS NOT NULL AND LO>=#{minLo} AND LO<=#{maxLo} AND LA>=#{minLa} AND LA<=#{maxLa} " +
            "GROUP BY MI;")
    List<FindRoundEqModel> findRoundEq(@Param("minLo") double minLo ,@Param("maxLo")double maxLo ,
                                       @Param("minLa") double minLa ,@Param("maxLa")double maxLa);


    /** 根据坐标点查询附近点的设备（查的是设备位置表） */
    @Select("SELECT " +
            "SUBSTRING_INDEX(GROUP_CONCAT(el.ID ORDER BY el.ID DESC),',',1) AS ID,el.EQUIP_CODE as mi,el.LO,el.lA,rs.S_NAME,rs.S_LOGO,rs.RENT_COST,ei.STATE,rs.S_ADDRESS as address " +
            "FROM " +
            "equip_location AS el " +
            "LEFT JOIN equipinfo AS ei ON ei.CODE=el.EQUIP_CODE " +
            "LEFT JOIN qrcode_store AS qs ON qs.equip=ei.ID " +
            "LEFT JOIN shop_equip AS se ON se.QRCODE_STORE=qs.ID " +
            "LEFT JOIN rank_shop AS rs ON rs.CODE=se.CODE " +
            "WHERE rs.CODE IS NOT NULL AND LO>=#{minLo} AND LO<=#{maxLo} AND LA>=#{minLa} AND LA<=#{maxLa} " +
            "GROUP BY el.EQUIP_CODE;")
    List<FindRoundEqModel> newFindRoundEq(@Param("minLo") double minLo ,@Param("maxLo")double maxLo ,
                                       @Param("minLa") double minLa ,@Param("maxLa")double maxLa);

    /** 根据坐标点查询附近点的设备（查的是店铺表） */
    @Select("SELECT  UNIT_MINUTE as unitMinute, BUSINESS_HOURS ,ID,S_NAME,S_LOGO,RENT_COST,S_ADDRESS as address,LO,LA  FROM rank_shop WHERE LO IS NOT NULL AND LA IS NOT NULL AND LO>=#{minLo} AND LO<=#{maxLo} AND LA>=#{minLa} AND LA<=#{maxLa} AND ISBLOCK=2")
    List<FindRoundEqModel> newFindRoundEqByShop(@Param("minLo") double minLo ,@Param("maxLo")double maxLo ,
                                          @Param("minLa") double minLa ,@Param("maxLa")double maxLa);

    /** 查询所有的设备心跳数据，去重复，并且拿到最新 */
    @Select("SELECT SUBSTRING_INDEX(GROUP_CONCAT(ID ORDER BY ID DESC),',',1) AS ID,MI,LO,lA,LL FROM equip_heart_detail WHERE id>3800000 GROUP BY MI")
    List<EquipHeartDetail> findAllGroupByCode();

    /** 增加设备位置记录 */
    @Insert("insert into equip_location(EQUIP_CODE,LO,LA,UPDATE_TIME) values (#{equipCode},#{lo},#{la},NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertEqLocation(EquipLocation equipLocation);

}