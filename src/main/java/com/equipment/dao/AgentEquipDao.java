package com.equipment.dao;

import com.equipment.entity.AgentEquip;
import com.equipment.model.querymodel.QueryAgentEquipModel;
import com.equipment.model.querymodel.QueryAgentShopEqModel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: JavaTansanlin
 * @Description: 代理商设备 dao
 * @Date: Created in 11:45 2018/8/28
 * @Modified By:
 */
@Component
public interface AgentEquipDao {

    /** 根据二维码id查询代理设备的记录 */
    @Select("SELECT * FROM agent_equip WHERE QRCODE_STORE = #{qrcodeId}")
    AgentEquip findEntityByQrcodeId(@Param("qrcodeId") Long qrcodeId);

    /** 插入一条记录 */
    @Insert("insert into agent_equip(AGENT_ID,REGISTTIME,QRCODE_STORE,OPERATOR) values (#{agentId},#{registtime},#{qrcodeStore},#{operator})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertOne(AgentEquip agentEquip);

    /** 根据代理id，设备编号，以及设备状态查询设备 */
    @Select({
            "<script>",
            "SELECT eq.ID,eq.CODE,et.CARD_NUM AS carnum,rs.S_NAME AS store ",
            "FROM equipinfo AS eq",
            "LEFT JOIN qrcode_store AS qs ON qs.EQUIP = eq.ID ",
            "LEFT JOIN agent_equip AS ae ON ae.QRCODE_STORE = qs.ID ",
            "LEFT JOIN shop_equip AS se ON qs.ID = se.QRCODE_STORE ",
            "LEFT JOIN rank_shop AS rs ON se.CODE = rs.CODE ",
            "LEFT JOIN equiptype AS et ON et.ID = eq.TYPE WHERE 1=1 ",
            "AND ae.AGENT_ID = #{agentId} AND eq.ISSTOCK=1",
            "<if test='eqCode != null'>AND eq.CODE = #{eqCode}</if>",
            "<if test='state != null'>AND eq.STATE = #{state}</if>",
            "</script>"
    })
    List<QueryAgentEquipModel> findEquipByAgentOrCodeOrState(@Param("agentId") Long agentId ,@Param("eqCode") String eqCode ,@Param("state") Integer state);

    /** 查询代理店铺的设备，根据传进来的代理分组查询 */
    @Select({
            "<script>",
                "SELECT eq.ID,eq.CODE,eq.STATE,rs.S_NAME",
                "FROM",
                "shop_equip AS se",
                "INNER JOIN rank_shop AS rs ON se.CODE = rs.CODE",
                "<if test='accunt == 2'>INNER JOIN agent_relation AS ar ON rs.MANAGER = ar.ID</if>",
                "<if test='accunt == 3'>INNER JOIN agent_relation AS ar ON rs.SALES_AGENT = ar.ID</if>",
                "<if test='accunt == 4'>INNER JOIN agent_relation AS ar ON rs.AREA_AGENT = ar.ID</if>",
                "<if test='accunt == 5'>INNER JOIN agent_relation AS ar ON rs.CITY_AGENT = ar.ID</if>",
                "<if test='accunt == 6'>INNER JOIN agent_relation AS ar ON rs.PROVINCE_AGENT = ar.ID</if>",
                "INNER JOIN qrcode_store AS qs ON qs.ID = se.QRCODE_STORE",
                "INNER JOIN equipinfo AS eq ON eq.ID = qs.EQUIP",
                "WHERE ar.OPENID = #{openid} AND eq.ISSTOCK=1",
                "<if test='eqCode != null'>AND eq.CODE like concat('%',#{eqCode},'%')</if>",
            "</script>"
    })
    List<QueryAgentShopEqModel> findAgentShopEq( @Param("openid") String openid ,@Param("accunt") Integer accunt ,@Param("eqCode") String eqCode);

}
