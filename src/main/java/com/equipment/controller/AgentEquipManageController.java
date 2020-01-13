package com.equipment.controller;

import com.equipment.model.equipmanager.EquipManagePage;
import com.equipment.model.querymodel.QueryAgentEquipModel;
import com.equipment.model.querymodel.QueryAgentShopEqModel;
import com.equipment.service.AgentEquipManageService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Author: JavaTansanlin
 * @Description: 代理设备管理的相关接口
 * @Date: Created in 10:16 2018/8/28
 * @Modified By:
 */
@RestController
@RequestMapping("/aem")
public class AgentEquipManageController {

    /** 代理设备管理service */
    @Autowired
    private AgentEquipManageService manageService;

    /** 代理绑定设备（根据代理的id和扫描的出来的二维码id） */
    @PostMapping("/agentBindEquip")
    @ResponseBody
    public Map<String ,Object> agentBindEquip(Long agentId, String codeUrl){
        return manageService.agentBindEquip(agentId, codeUrl);
    }

    /** 查询代理名下的设备，根据设备状态和设备编号为查询条件 */
    @PostMapping("/findAgentEquip")
    public PageInfo<QueryAgentEquipModel> findAgentEquip(Integer indexPage, Integer pageCount,Long agentId ,String eqCode ,Integer state){
        if (indexPage==null || indexPage<=0){
            indexPage = 1;
        }
        if (pageCount==null || pageCount<=0){
            pageCount = 15;
        }
        return manageService.findAgentEquip(indexPage ,pageCount ,agentId ,eqCode ,state);
    }

    /** 查询代理店铺的设备，根据传进来的代理分组查询 */
    @PostMapping("/findAgentShopEq")
    public PageInfo<QueryAgentShopEqModel> findAgentShopEq(Integer indexPage, Integer pageCount ,String openid ,
                                                           Integer mgc ,String eqCode){
        if (indexPage==null || indexPage<=0){
            indexPage = 1;
        }
        if (pageCount==null || pageCount<=0){
            pageCount = 15;
        }
        return manageService.findAgentShopEq(indexPage ,pageCount ,openid ,mgc ,eqCode);
    }

    /**
     * 【代理商系统】-->代理商下的设备查询分页（根据名下的店铺进行查询）
     */
    @PostMapping("/findMyAgentShopEq")
    Map<String ,Object> findMyAgentShopEq(Integer pageNum ,Integer pageSize ,String code , Integer equipType ,Integer state
            ,String shopName,Long agent, String time){
        return manageService.findMyAgentShopEq(pageNum ,pageSize ,code ,equipType ,state ,shopName ,agent, time);
    }

    /** 设备管理根据查询条件查询所有用于导出使用 */
    @PostMapping("/findEquipManageByOdition")
    public List<EquipManagePage> findEquipManageByOdition(String code , Integer equipType , Integer state
            , String shopName,Long agent, String time){
        return manageService.findEquipManageByOdition(code,equipType,state,shopName,agent,time);
    }

    /**
     * 【代理商系统】-->代理商绑定设备
     */
    @PostMapping("/agentBanEq")
    Map<String ,Object> agentBanEq(Long agent ,String eqCode ,String shopCode){
        return manageService.agentBanEq(agent ,eqCode ,shopCode);
    }

    /**
     * 查看代理商总设备数，和在线设备数
     */
    @PostMapping("/findEqNumAndAll")
    Map<String ,Object> findEqNumAndAll(Long agent){
        return manageService.findEqNumAndAll(agent);
    }

}
