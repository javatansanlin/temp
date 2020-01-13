package com.equipment.controller;

import com.equipment.service.SuperEqManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: JavaTansanlin
 * @Description: 设备的超级管理接口
 * @Date: Created in 16:18 2018/9/1
 * @Modified By:
 */
@RestController
@RequestMapping("superEqMa")
public class SuperEqManageController {

    @Autowired
    SuperEqManageService superEqManageService;

    /** 设备弹出电池操作 */
    @RequestMapping("overhaulToPower")
    Map<String ,Object> overhaulToPower(String[] powers ,String eqCode){
        return superEqManageService.overhaulToPower(powers ,eqCode);
    }

    /** 重启或者关机操作 type:off-关机操作，rest-重启操作*/
    @RequestMapping("offOrRest")
    Map<String ,Object> offOrRest(String eqCode ,String type){
        return superEqManageService.offOrRest(eqCode ,type);
    }

    /** 弹出设备的所有电池 */
    @PostMapping("ohAllPower")
    Map<String ,Object> ohAllPower(String eqCode){
        return superEqManageService.ohAllPower(eqCode);
    }
}
