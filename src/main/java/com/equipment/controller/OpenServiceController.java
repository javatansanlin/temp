package com.equipment.controller;

import com.equipment.service.OpenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: JavaTansanlin
 * @Description: 提供给其它服务的接口
 * @Date: Created in 16:56 2018/8/28
 * @Modified By:
 */
@RestController
@RequestMapping("/openEquip")
public class OpenServiceController {

    /** 对外的设备服务 */
    @Autowired
    private OpenService openService;

    /** 根据微信的ticket查询出设备号  **/
    @PostMapping("/findEquipCodeByTicket")
    public String findEquipCodeByTicket(String ticket){
        return openService.findEquipCodeByWXTicket(ticket);
    }

    /** 根据GXQrcode查询设备号 **/
    @PostMapping("/findEqCodeByGXQrcode")
    public String findEqCodeByGXQrcode(String gxCode) { return openService.findEqCodeByGXQrcode(gxCode);}

    /**
     * 根据GXQrcode查询在线的设备号(包括广告机的逻辑)
     * @param code 对应的是微信的ticket，支付宝的gxCode
     * @param type wx-微信；ali-支付宝
     * @return
     */
    @PostMapping("/findEqCodeByGXQrcodeAndOnline")
    public Map<String ,Object> findEqCodeByGXQrcodeAndOnline(String code ,String type) { return openService.findEqCodeByGXQrcodeAndOnline(code,type);}

    /**
     * 提供给netty使用，检测是否在netty中有参数，有则添加，无则修改
     */

    /**
     * 提供个app使用：根据二维码的链接查询出设备编号
     */
    @PostMapping("/findEqCodeByString")
    @ApiOperation(value = "提供个app使用：根据二维码的链接查询出设备编号")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name = "codeString", value = "二维码链接或者是二码合一码", dataType = "String"),
            @ApiImplicitParam(paramType="query", name = "sources", value = "请求源：XCX（小程序访问），APP（app访问）", dataType = "String"),
    })
    public Map<String ,Object> findEqCodeByString(String codeString ,String sources){
        return openService.findEqCodeByString(codeString ,sources);
    }

}
