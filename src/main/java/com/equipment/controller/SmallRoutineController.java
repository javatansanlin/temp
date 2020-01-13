package com.equipment.controller;

import com.equipment.service.SmallRoutineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: JavaTansanlin
 * @Description: 小程序相关的接口
 * @Date: Created in 13:14 2018/10/11
 * @Modified By:
 */
@RestController
@RequestMapping("/smallRou")
public class SmallRoutineController {

    /** 相关的业务流程service */
    @Autowired
    private SmallRoutineService smallRoutineService;

    /**
     * 根据坐标点查询附近点的设备
     */
    @PostMapping("/findRoundEq")
    public Map<String ,Object> findRoundEq(Double lo ,Double la){
        return smallRoutineService.findRoundEq(lo ,la);
    }

}
