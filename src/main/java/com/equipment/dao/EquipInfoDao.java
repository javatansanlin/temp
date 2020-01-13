package com.equipment.dao;

import com.equipment.entity.EquipInfo;
import com.equipment.entity.RankShop;
import com.equipment.model.equipmanager.EquipManagePage;
import com.equipment.model.equipmanager.QueryBundedEquip;
import com.equipment.model.equipmanager.QueryNotBundEquip;
import com.equipment.model.querymodel.FindAgentGradeResModel;
import com.equipment.model.querymodel.InitEQ2EquipQueryModel;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 设备信息
 */
@Component
public interface EquipInfoDao {

    /** 根据设备id查询设备记录 */
    @Select("SELECT * FROM equipinfo WHERE ID=#{id}")
    EquipInfo findEqById(Long id);

    /** 更新设备的状态信息,state=1-在线，2-离线 **/
    @Update("UPDATE equipinfo SET state=#{state} WHERE code=#{code} and state=2")
    int updateEquipState(@Param("state") int state , @Param("code") String code);

    /** 更新设备的状态信息,state=1-在线，2-离线 **/
    @Update("UPDATE equipinfo SET state=#{state} WHERE code=#{code}")
    int updateEquipStateDown(@Param("state") int state , @Param("code") String code);

    /** 查询指定的设备号是否存在 **/
    @Select("SELECT count(ID) FROM equipinfo WHERE code=#{code}")
    int findEquipByCode(String code);

    /** 根据设备编号查询在库未绑定二维码的设备 */
    @Select({
            "<script>",
            "SELECT eq.ID,eq.CODE ",
            "FROM equipinfo AS eq ",
            "WHERE ",
            "NOT EXISTS (SELECT EQUIP FROM qrcode_store WHERE eq.ID = EQUIP) ",
            "AND eq.ISSTOCK = 1 ",
            "<if test='code != null'>AND eq.CODE = #{code}</if>",
            "</script>"
    })
    List<InitEQ2EquipQueryModel> findNotBindQREquipByCode(@Param("code") String code);

    /** 设备管理查询相关信息 */
    @Select({
            "<script>",
            "SELECT eq.ID,eq.CODE,se.CODE AS SCODE,et.NAME,eq.STATE,rs.S_NAME as rsname,ap.A_NAME as apname,pa.A_NAME as paname,ca.A_NAME AS caname,aa.A_NAME AS aaname,sa.A_NAME AS saname, mana.A_NAME AS manager, ",
            "ehd.sd sd, ehd.bc bc, ehd.cb cb, ehd.cr cr, ehd.ts ts",
            "FROM equipinfo AS eq ",
            "LEFT JOIN QRCODE_STORE AS qs ON eq.ID = qs.EQUIP",
            "LEFT JOIN shop_equip AS se ON qs.ID = se.QRCODE_STORE ",
            "LEFT JOIN rank_shop as rs ON rs.CODE = se.CODE ",
            "LEFT JOIN agent_plat ap on rs.PLAT = ap.ID ",
            "LEFT JOIN agent_relation mana on rs.MANAGER = mana.id ",
            "LEFT JOIN agent_relation pa on rs.PROVINCE_AGENT=pa.id ",
            "LEFT JOIN agent_relation ca on rs.CITY_AGENT = ca.ID ",
            "LEFT JOIN agent_relation aa on rs.AREA_AGENT = aa.ID ",
            "LEFT JOIN agent_relation sa on rs.SALES_AGENT = sa.ID ",
            "LEFT JOIN equiptype as et ON eq.TYPE = et.ID ",
            "LEFT JOIN equip_heart_detail_current ehd on ehd.MI = eq.CODE ",
            "WHERE 1=1 AND eq.ISSTOCK=1 ",
            "<if test='startTime != null and endTime!=null'>AND ehd.TS * 1000 BETWEEN #{startTime} AND #{endTime}</if>",
            "<if test=\"code != null and code!=''\">AND eq.CODE = #{code}</if>",//设备编号条件
            "<if test=\"state != null and state!=''\">AND eq.STATE = #{state}</if>",//状态条件
            "<if test=\"type != null and type!=''\">AND et.ID = #{type}</if>",//设备类型条件
            "<if test=\"shopName != null and shopName != ''\">AND rs.S_NAME like concat('%',#{shopName},'%')</if>",//店铺名条件
            "<if test=\"playName != null and playName != ''\">AND ap.A_NAME like concat('%',#{playName},'%')</if>",//平台名条件
            "<if test='manager != null'>AND mana.ID = #{manager}</if>",//店铺管理员id条件
            "<if test='pa != null'>AND pa.ID = #{pa}</if>",//省代id条件
            "<if test='ca != null'>AND ca.ID = #{ca}</if>",//市代id条件
            "<if test='aa != null'>AND aa.ID = #{aa}</if>",//区代id条件
            "<if test='sa != null'>AND sa.ID = #{sa}</if>",//业务代id条件
            "ORDER BY eq.STATE ASC, ehd.TS DESC",
            "</script>"
    })
    List<EquipManagePage> findAllEquipManage(@Param("code") String code, @Param("type") Integer equipType ,@Param("state") Integer state, @Param("playName") String playName,
                                             @Param("shopName") String shopName, @Param("manager") Long manager, @Param("pa") Long pa, @Param("ca") Long ca,
                                             @Param("aa") Long aa ,@Param("sa") Long sa,
                                             @Param("startTime") String startTime,  @Param("endTime") String endTime);

    /**
     * 后台系统-->设备管理查询相关信息
     */
    @Select({
            "<script>",
            "SELECT eq.ID,eq.CODE,se.CODE AS SCODE,et.NAME,eq.STATE,rs.S_NAME as rsname,ap.A_NAME as apname,pa.A_NAME as paname,ca.A_NAME AS caname,aa.A_NAME AS aaname,sa.A_NAME AS saname, mana.A_NAME AS manager, ",
            "ehd.ID ehdId, ehd.sd sd, ehd.bc bc, ehd.cb cb, ehd.cr cr, ehd.ts ts",
            ",qs.REGISTIME as production   ,  (select sum(1) from  shop_room  where  rs.CODE = SHOP_CODE ) as  roomNum ",
            "FROM equipinfo AS eq ",
            "LEFT JOIN QRCODE_STORE AS qs ON eq.ID = qs.EQUIP",
            "LEFT JOIN shop_equip AS se ON qs.ID = se.QRCODE_STORE ",
            "LEFT JOIN rank_shop as rs ON rs.CODE = se.CODE ",
            "LEFT JOIN agent_plat ap on rs.PLAT = ap.ID ",
            "LEFT JOIN agent_relation mana on rs.MANAGER = mana.id ",
            "LEFT JOIN agent_relation pa on rs.PROVINCE_AGENT=pa.id ",
            "LEFT JOIN agent_relation ca on rs.CITY_AGENT = ca.ID ",
            "LEFT JOIN agent_relation aa on rs.AREA_AGENT = aa.ID ",
            "LEFT JOIN agent_relation sa on rs.SALES_AGENT = sa.ID ",
            "LEFT JOIN equiptype as et ON eq.TYPE = et.ID ",
            "LEFT JOIN equip_heart_detail_current ehd on ehd.MI = eq.CODE ",
            "WHERE 1=1 AND eq.ISSTOCK=1 ",
            "<if test='startTime != null and endTime!=null'>AND ehd.TS * 1000 BETWEEN #{startTime} AND #{endTime}</if>",
            "<if test=\"code != null and code!=''\">AND eq.CODE = #{code}</if>",//设备编号条件
            "<if test=\"state != null and state!=''\">AND eq.STATE = #{state}</if>",//状态条件
            "<if test=\"type != null and type!=''\">AND et.ID = #{type}</if>",//设备类型条件
            "<if test=\"shopName != null and shopName != ''\">AND rs.S_NAME like concat('%',#{shopName},'%')</if>",//店铺名条件
            "<if test=\"playName != null and playName != ''\">AND ap.A_NAME like concat('%',#{playName},'%')</if>",//平台名条件
            "<if test='manager != null'>AND mana.ID = #{manager}</if>",//店铺管理员id条件
            "<if test='pa != null'>AND pa.ID = #{pa}</if>",//省代id条件
            "<if test='ca != null'>AND ca.ID = #{ca}</if>",//市代id条件
            "<if test='aa != null'>AND aa.ID = #{aa}</if>",//区代id条件
            "<if test='sa != null'>AND sa.ID = #{sa}</if>",//业务代id条件
            "<if test=\"agentName != null and agentName!=''\">AND CONCAT(IFNULL(aa.A_NAME,''),IFNULL(ca.A_NAME,''),IFNULL(mana.A_NAME,''),IFNULL(pa.A_NAME,''),IFNULL(sa.A_NAME,'')) LIKE concat('%',#{agentName},'%')</if>",//设备类型条件
            //"GROUP BY(eq.CODE)",
            "ORDER BY eq.STATE ASC, ehd.TS DESC",
            "</script>"
    })
    List<EquipManagePage> findAllEquipManage2(@Param("code") String code, @Param("type") Integer equipType ,@Param("state") Integer state, @Param("playName") String playName,
                                              @Param("shopName") String shopName, @Param("manager") Long manager, @Param("pa") Long pa, @Param("ca") Long ca,
                                              @Param("aa") Long aa ,@Param("sa") Long sa ,@Param("agentName") String agentName,
                                              @Param("startTime") String startTime,  @Param("endTime") String endTime);

    /**
     * 查看代理商设备的数据，在线条件
     */
    @Select({
            "<script>",
            "SELECT count(eq.ID) ",
            "FROM equipinfo AS eq ",
            "LEFT JOIN QRCODE_STORE AS qs ON eq.ID = qs.EQUIP",
            "LEFT JOIN shop_equip AS se ON qs.ID = se.QRCODE_STORE ",
            "LEFT JOIN rank_shop as rs ON rs.CODE = se.CODE ",
            "LEFT JOIN agent_plat ap on rs.PLAT = ap.ID ",
            "LEFT JOIN agent_relation mana on rs.MANAGER = mana.id ",
            "LEFT JOIN agent_relation pa on rs.PROVINCE_AGENT=pa.id ",
            "LEFT JOIN agent_relation ca on rs.CITY_AGENT = ca.ID ",
            "LEFT JOIN agent_relation aa on rs.AREA_AGENT = aa.ID ",
            "LEFT JOIN agent_relation sa on rs.SALES_AGENT = sa.ID ",
            "LEFT JOIN equiptype as et ON eq.TYPE = et.ID WHERE 1=1 AND eq.ISSTOCK=1 ",
            "<if test=\"state != null and state!=''\">AND eq.STATE = #{state}</if>",//状态条件
            "<if test='manager != null'>AND mana.ID = #{manager}</if>",//店铺管理员id条件
            "<if test='pa != null'>AND pa.ID = #{pa}</if>",//省代id条件
            "<if test='ca != null'>AND ca.ID = #{ca}</if>",//市代id条件
            "<if test='aa != null'>AND aa.ID = #{aa}</if>",//区代id条件
            "<if test='sa != null'>AND sa.ID = #{sa}</if>",//业务代id条件
            "</script>"
    })
    Integer findAgentEqNum(@Param("state") Integer state,@Param("manager") Long manager, @Param("pa") Long pa, @Param("ca") Long ca,
                           @Param("aa") Long aa ,@Param("sa") Long sa);

    /** 设备管理查询用到的，查询代理属于哪一级代理，2-店铺管理员，3-业务代，4-区域代，5-市级代，6-省级代 */
    @Select({
            "<script>",
            "SELECT M_GROUP,ID FROM agent_relation WHERE 1=1 ",
            "<if test='openId != null and openId != &apos;&apos;'>AND OPENID = #{openId}</if>",
            "<if test='agentName != null and agentName != &apos;&apos;'>AND A_NAME =#{agentName}</if>",
            "</script>"
    })
    FindAgentGradeResModel findAgentGrade(@Param("openId") String openId , @Param("agentName") String agentName);

    /** 根据设备编号查询设备的卡口数 */
    @Select("SELECT et.CARD_NUM FROM equipinfo AS eq LEFT JOIN equiptype AS et ON eq.TYPE = et.ID WHERE eq.CODE=#{code}")
    int findCardNumByEquip(@Param("code") String code);


    /** 插入一条设备记录 **/
    @Insert("insert into equipinfo(CODE,TYPE,SERVER,STATE,ISSTOCK) values (#{code},#{type},#{server},#{state},#{isstock})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(EquipInfo equipInfo);

    /** 根据店铺编号查询已绑定的设备 **/
    @Select("SELECT " +
            "eq.CODE," +
            "eq.ID," +
            "et.NAME AS type," +
            "et.CARD_NUM AS carnum," +
            "se.REGISTTIME," +
            "sm.ACCOUNT_NAME AS smname," +
            "se.QRCODE_STORE AS qrcodeId " +
            "FROM " +
            "shop_equip AS se " +
            "LEFT JOIN QRCODE_STORE AS qs ON qs.ID = se.QRCODE_STORE " +
            "LEFT JOIN sys_member AS sm ON se.OPERATOR = sm.ID " +
            "LEFT JOIN equipinfo AS eq ON qs.EQUIP = eq.ID " +
            "LEFT JOIN equiptype AS et ON eq.TYPE = et.ID " +
            "WHERE " +
            "se.CODE = #{code} AND eq.ISSTOCK = 1")
    List<QueryBundedEquip> findBundedByshopCode(@Param("code") String code);


    /** 根据店铺编号查询已绑定的设备 **/
    @Select({
            "<script>",
            "SELECT  eq.CODE, eq.ID,",
            "et.NAME AS type," +
            "et.CARD_NUM AS carnum," +
            "se.REGISTTIME," +
            "sm.ACCOUNT_NAME AS smname," +
            "se.QRCODE_STORE AS qrcodeId " +
            "FROM " +
            "shop_equip AS se " +
            "LEFT JOIN QRCODE_STORE AS qs ON qs.ID = se.QRCODE_STORE " +
            "LEFT JOIN sys_member AS sm ON se.OPERATOR = sm.ID " +
            "LEFT JOIN equipinfo AS eq ON qs.EQUIP = eq.ID " +
            "LEFT JOIN equiptype AS et ON eq.TYPE = et.ID " +
            "WHERE " +
            "   eq.ISSTOCK = 1 AND se.code = #{shopcode}  ",
            "<if test='code != null and code != &apos;&apos;'>AND eq.CODE = #{code}</if>",
            "</script>"
    })
    List<QueryBundedEquip> findBundedByshopCodeAndCode(@Param("shopcode") String shopcode, @Param("code") String code);


    /** 查询未绑定的设备 **/
    @Select({
            "<script>",
            "SELECT eq.ID,eq.CODE,et.NAME AS type,et.CARD_NUM AS carnum FROM equipinfo AS eq ",
            "LEFT JOIN equiptype AS et ON eq.TYPE = et.ID ",
            "LEFT JOIN qrcode_store AS qs ON qs.EQUIP = eq.ID",
            "WHERE ",
            "(SELECT COUNT(se.ID) FROM shop_equip AS se WHERE qs.ID = se.QRCODE_STORE ) = 0 ",
            "AND eq.ISSTOCK = 1 ",
            "<if test='code != null and code != &apos;&apos;'>AND eq.CODE = #{code}</if>",
            "</script>"
    })
    List<QueryNotBundEquip> findNotBundEquip(@Param("code") String code);

    /** 根据设备id查询设备 */
    @Select("SELECT * FROM equipinfo WHERE id=#{id}")
    EquipInfo findEquipEntityById(@Param("id") Long id);


    /** 根据设备Code查询设备 */
    @Select("SELECT * FROM equipinfo WHERE code=#{code}")
    EquipInfo findEquipEntityByCode(@Param("code") String code);

    /** 根据二维码的微信的ticket查询设备 */
    @Select("SELECT * FROM equipinfo AS eq LEFT JOIN qrcode_store AS qs ON qs.EQUIP=eq.ID WHERE qs.WECHAT_TICKET=#{ticket} AND eq.ISSTOCK=1")
    EquipInfo findEquipByQrcodeTicket(@Param("ticket") String ticket);

    /** 根据GXQrcode查询设备编号 */
    @Select("SELECT * FROM equipinfo AS eq LEFT JOIN qrcode_store AS qs ON qs.EQUIP=eq.ID WHERE qs.GX_QRCODE=#{gxCode} AND eq.ISSTOCK=1")
    EquipInfo findEquipByGXcode(@Param("gxCode") String gxCode);

    /** 测试：查询全部设备 */
    @Select("select CODE from equipinfo")
    List<EquipInfo> findAll();

    /** 测试：插入心跳设备 **/
    @Insert("INSERT INTO `equip_heart_detail` (`TE`, `MI`, `TI`, `AT`, `SD`, `TS`, `DI`, `DT`, `LL`, `BC`, `CB`, `CR`, `CD`, `SC`) VALUES ('TCP', #{code}, NULL, 'Info', '15920993999', '1511870734', #{code}, 'Battery', '31.1594949,132.5984155', '10', '7', '3', NULL, NULL)")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertHeart(@Param("code") String code);

    /** 测试：查询全部设备 */
    @Select("select * from equipinfo limit 10")
    List<Map<String,Object>> findAll2();

    /** 根据GXQrcode查询在线的设备号 **/
    @Select("SELECT * FROM equipinfo AS eq LEFT JOIN qrcode_store AS qs ON qs.EQUIP=eq.ID WHERE qs.GX_QRCODE=#{gxCode} AND eq.STATE=1 AND eq.ISSTOCK=1")
    EquipInfo findEquipByGXcodeAndOnline(String gxCode);

    /** 根据代理商id查询在线设备编号 */
    @Select("SELECT ei.CODE FROM equipinfo AS ei LEFT JOIN qrcode_store AS qs ON qs.EQUIP = ei.ID LEFT JOIN shop_equip AS se ON se.QRCODE_STORE = qs.ID LEFT JOIN rank_shop AS rs ON rs.CODE = se.CODE WHERE rs.ID=#{id} AND ei.STATE=1")
    List<String> findEqCodeByShopId(Long id);

    /** 更新设备出库状态（目的达到删除效果） */
    @Update("UPDATE equipinfo SET ISSTOCK=2 WHERE ID=#{id}")
    int updateEqTockState(Long id);

    /** 根据设备id查询店铺设备关系id */
    @Select("SELECT se.ID FROM shop_equip AS se LEFT JOIN qrcode_store AS qs ON qs.ID=se.QRCODE_STORE WHERE qs.EQUIP=#{id}")
    Long findShopEqIdByEqid(Long id);

    /** 删除设备所在的店铺关系 */
    @Delete("DELETE FROM shop_equip WHERE ID=#{id}")
    int deleteShopEq(Long id);

    /** 根据设备编号查询店铺信息 */
    @Select({
            "<script>",
            "SELECT",
            "rs.*",
            "FROM",
            "equipinfo AS ei",
            "LEFT JOIN qrcode_store AS qs ON qs.EQUIP = ei.ID",
            "LEFT JOIN shop_equip AS se ON se.QRCODE_STORE = qs.ID",
            "LEFT JOIN rank_shop AS rs ON rs. CODE = se. CODE",
            "WHERE",
            "ei. CODE = #{eqCode}",
            "</script>"
    })
    RankShop findShopDetailByEqCode(String eqCode);

    /**查询线充 最大值 */
    @Select("select  * from equipinfo  where code like \"999%\"   ORDER BY id DESC  limit 1")
    EquipInfo findLineChargerCode();

    /**查询烘衣架 最大值 */
    @Select("select  * from equipinfo  where code like \"888%\"   ORDER BY id DESC  limit 1")
    EquipInfo findHyjCode();

}