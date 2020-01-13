package com.equipment.dao;

import com.equipment.model.equipmanager.PowerInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface PowerInfoDao {

    //根据查询条件查询电池列表
    @Select({
            "<script>",
            "SELECT ID id, POWER_CODE powerCode, BORROW_STATUS borrowStatus, POWER_STATUS powerStatus, ",
            "ONLINE_EQUIP onlineEquip, BEFORE_EQUIP beforeEquip, POWER_NUMS powerNums, BORROW_TIMES borrowTimes, ",
            "ERROR_TIMES errorTimes, USE_MINUTES useMinutes, POWER_PROFIT powerProfit, TOTAL_ERROR_TIMES totalErrorTimes, DATE_FORMAT(UPDATETIME, '%Y-%m-%d %H:%i:%s') updateTime ",
            "FROM equip_powerinfo ",
            "WHERE 1=1 ",
            "<if test='powerCode != null'> AND POWER_CODE = #{powerCode} </if>",
            "<if test='equipCode != null'> AND (ONLINE_EQUIP like #{equipCode} AND BORROW_STATUS = 1)  </if>",
            "<if test='errorTimes != null'> AND ERROR_TIMES = #{errorTimes} </if>",
            "<if test='powerStatus != null'> AND POWER_STATUS = #{powerStatus} </if>",
            "<if test='borrowStatus != null'> AND BORROW_STATUS = #{borrowStatus} </if>",
            "</script>"
    })
    List<Map> findPowerList(@Param("powerCode") String powerCode,
                            @Param("equipCode")String equipCode,
                            @Param("errorTimes")Integer errorTimes,
                            @Param("powerStatus")Integer powerStatus,
                            @Param("borrowStatus")Integer borrowStatus);


    //更新电池信息
    @Update({
            "<script>",
            "UPDATE equip_powerinfo " ,
            "<set> " ,
            "<if test='param.borrowStatus != null'> BORROW_STATUS = #{param.borrowStatus} ,</if>" ,//电池租借状态
            "<if test='param.powerStatus != null'> POWER_STATUS = #{param.powerStatus}, </if>" ,//电池锁定状态
            "<if test='param.onlineEquip != null'> ONLINE_EQUIP = #{param.onlineEquip},</if>" ,//当前设备
            "<if test='param.beforeEquip != null'> BEFORE_EQUIP = #{param.beforeEquip},</if>" ,//上一个设备
            "<if test='param.powerNums != null'> POWER_NUMS = #{param.powerNums},</if>" ,//电池电量
            "<if test='param.borrowTimes != null'> BORROW_TIMES = #{param.borrowTimes},</if>" ,//租借次数
            "<if test='param.errorTimes != null'> ERROR_TIMES = #{param.errorTimes} ,</if> " ,//错误次数
            "<if test='param.useMinutes != null'> USE_MINUTES = #{param.useMinutes} ,</if> " ,//使用时长
            "<if test='param.updateTime != null'> UPDATETIME = #{param.updateTime} ,</if> " ,//更新时间
            "<if test='param.powerProfit != null'> POWER_PROFIT = #{param.powerProfit} ,</if> " ,//电池收益
            "</set> ",
            "WHERE ID = #{param.id}",
            "</script>"
    })
    int updatePowerById(@Param("param") Map<String, Object> param);

    //重置错误次数（设置错误次数为0,并且设置锁定状态为未锁定）
    @Update("UPDATE equip_powerinfo set TOTAL_ERROR_TIMES = TOTAL_ERROR_TIMES + #{errorTimes}, ERROR_TIMES = 0, POWER_STATUS = 1, FIX_TIMES = FIX_TIMES + 1, TOTAL_ERROR_TIMES = TOTAL_ERROR_TIMES + ERROR_TIMES WHERE ID = #{id}")
    int updatePowerDefault(@Param("id") Integer id, @Param("errorTimes") Integer errorTimes);

    @Select({
            "<script>",
            "SELECT ID id, POWER_CODE powerCode, BORROW_TIMES borrowTimes, POWER_NUMS powerNums, POWER_STATUS powerStatus, ERROR_TIMES errorTimes, TOTAL_ERROR_TIMES totalErrorTimes ",
            "FROM equip_powerinfo WHERE ID = #{id}",
            "</script>"
    })
    PowerInfo findPowerById(@Param("id") Integer id);

    //根据电池编号查询租借中的订单
    @Select("SELECT ID from orderinfo WHERE POWER_BI = #{powerCode} AND ORDER_STATUS = 1")
    Integer findOrderByCode(@Param("powerCode") String powerCode);
}
