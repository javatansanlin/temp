package com.equipment.dao;

import com.equipment.model.equipmanager.EquipBanQueryByAgent;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Author: JavaTansanlin
 * @Description: 理商后台相关的底层sql
 * @Date: Created in 20:33 2018/10/16
 * @Modified By:
 */
@Component
public interface AgentManagerDao {

    /** 根据代理商id查询代理商等级 */
    @Select("SELECT M_GROUP FROM AGENT_RELATION WHERE ID = #{agent}")
    Integer findAgentGroupByAgentId(Long agent);

    /** 根据代理商id和数据id查询该记录是否存在 */
    @Select("SELECT COUNT(ID) FROM AGENT_RELATION WHERE AGENT_RELATION = #{agentId} and ID = #{id}")
    Integer findAgentCountByAgentIdAndID(@Param("agentId") Long agentId, @Param("id") Long id);

    /** 根据oid查询用户等级 */
    @Select("SELECT M_GROUP FROM MEMBER WHERE OPENID = #{oid}")
    Integer findMemberGroupByOID(String oid);

    /** 根据上级代理商id和代理名字查询该代理的等级-->代理商管理的设备查询用到 */
    @Select("SELECT ID,M_GROUP AS gr FROM agent_relation WHERE AGENT_RELATION = #{agent} AND A_NAME LIKE concat('%',#{name},'%');")
    List<Map> findAgentByReAndName(@Param("agent") Long agent ,@Param("name") String name);

    /** 根据代理商id和设备号查询设备信息 */
    @Select({
            "<script>",
            "SELECT eq.ID as eqId,se.ID as shopEqId,qs.ID as qrCodeId FROM equipinfo AS eq ",
            "LEFT JOIN qrcode_store AS qs ON qs.EQUIP = eq.ID ",
            "LEFT JOIN shop_equip AS se ON se.QRCODE_STORE = qs.ID ",
            "LEFT JOIN rank_shop AS rs ON rs.CODE = se.CODE ",
            "WHERE ",
            "eq.CODE = #{eqCode} AND eq.ISSTOCK=1",
            "<if test='loginGroup!=null and loginGroup==6' > and rs.PROVINCE_AGENT=#{agent} </if>",
            "<if test='loginGroup!=null and loginGroup==5' > and rs.CITY_AGENT=#{agent} </if>",
            "<if test='loginGroup!=null and loginGroup==4' > and rs.AREA_AGENT=#{agent} </if>",
            "<if test='loginGroup!=null and loginGroup==3' > and rs.SALES_AGENT=#{agent} </if>",
            "<if test='loginGroup!=null and loginGroup==2' > and rs.MANAGER=#{agent} </if>",
            "</script>"
    })
    EquipBanQueryByAgent findEqContByCodeAndAgentId(@Param("eqCode") String code , @Param("loginGroup") Integer loginGroup , @Param("agent") Long agent);

    /** 根据代理商设备的id删除记录 */
    @Delete("delete from SHOP_EQUIP where ID = #{id}")
    int deleShopEquip(Long id);
}
