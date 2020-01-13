package com.equipment.service;

import com.equipment.model.equipmanager.EquipManagePage;
import com.equipment.model.querymodel.QueryAgentEquipModel;
import com.equipment.model.querymodel.QueryAgentShopEqModel;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * @Author: JavaTansanlin
 * @Description: 代理设备管理 业务层接口
 * @Date: Created in 12:03 2018/8/28
 * @Modified By:
 */
public interface AgentEquipManageService {

    /** 代理绑定设备（根据代理的id和扫描的出来的二维码id） */
    Map<String ,Object> agentBindEquip(Long agentId ,String codeUrl);

    /** 查询代理名下的设备，根据设备状态和设备编号为查询条件 */
    PageInfo<QueryAgentEquipModel> findAgentEquip(Integer indexPage, Integer pageCount,Long agentId ,String eqCode ,Integer state);

    /** 查询代理店铺的设备，根据传进来的代理分组查询 */
    PageInfo<QueryAgentShopEqModel> findAgentShopEq(Integer indexPage, Integer pageCount, String openid, Integer mgc ,String eqCode);

    /**
     * 【代理商系统】-->代理商下的设备查询分页（根据名下的店铺进行查询）
     */
    Map<String,Object> findMyAgentShopEq(Integer pageNum, Integer pageSize, String code, Integer equipType, Integer state, String shopName, Long agent, String time);

    /**
     * 【代理商系统】-->代理商下的设备查询导出（根据名下的店铺进行查询）
     */
    List<EquipManagePage> findEquipManageByOdition(String code, Integer equipType , Integer state,
                                                          String shopName , Long agent, String time);
    /**
     * 【代理商系统】-->代理商绑定设备
     */
    Map<String,Object> agentBanEq(Long agent, String eqCode ,String shopCode);

    /**
     * 查看代理商总设备数，和在线设备数
     */
    Map<String,Object> findEqNumAndAll(Long agent);
}
