package com.equipment.controller;

import com.equipment.service.OtherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: JavaTansanlin
 * @Description:其它接口
 * @Date: Created in 11:16 2018/11/1
 * @Modified By:
 */
@RestController
@RequestMapping("/oth")
public class OtherController {

    /** 注入业务类 */
    @Autowired
    private OtherService otherService;

    /**
     * 获取临时密钥
     */
    @PostMapping("/getKey")
    public Map<String ,Object> getKey(Integer t){
        return otherService.getKey(t);
    }

    /**
     * 把设备心跳表的数据同步更新到设备位置表
     */
    @PostMapping("/synlyHealTolocation")
    public Map<String ,Object> synlyHealTolocation(){
        return otherService.synlyHealTolocation();
    }

}
