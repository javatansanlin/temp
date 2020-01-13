package com.equipment.controller;

import com.equipment.model.querymodel.InitEQ2EquipQueryModel;
import com.equipment.service.InitEquipQrcodeService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: JavaTansanlin
 * @Description: 设备与二维码绑定相关的接口
 * @Date: Created in 10:42 2018/8/28
 * @Modified By:
 */
@RestController
@RequestMapping("/initEQ")
public class InitEquipQrcodeController {

    /** 设备与二维码绑定相关service */
    @Autowired
    private InitEquipQrcodeService initEquipQrcodeService;

    /** 根据设备编号查询设备 */
    @PostMapping("/findEquip")
    public PageInfo<InitEQ2EquipQueryModel> findEquip(Integer indexPage, Integer pageCount, String code){
        if (indexPage==null || indexPage<=0){
            indexPage = 1;
        }
        if (pageCount==null || pageCount<=0){
            pageCount = 15;
        }
        return initEquipQrcodeService.findEquip(indexPage, pageCount, code);
    }

    /** 设备绑定二维码（以设备id和二维码链接为条件） */
    @PostMapping("/bindEquipQrcode")
    public Map<String ,Object> bindEquipQrcode(Long equipId ,String codeUrl){
        return initEquipQrcodeService.bindEquipQrcode(equipId,codeUrl);
    }

}