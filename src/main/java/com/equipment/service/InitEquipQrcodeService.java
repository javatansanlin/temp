package com.equipment.service;

import com.equipment.model.querymodel.InitEQ2EquipQueryModel;
import com.github.pagehelper.PageInfo;

import java.util.Map;

/**
 * @Author: JavaTansanlin
 * @Description: 设备与二维码绑定相关的服务  业务接口
 * @Date: Created in 18:12 2018/8/28
 * @Modified By:
 */
public interface InitEquipQrcodeService {

    /** 根据设备编号查询设备 */
    PageInfo<InitEQ2EquipQueryModel> findEquip(Integer indexPage, Integer pageCount,String code);

    /** 设备绑定二维码（以设备id和二维码链接为条件） */
    Map<String ,Object> bindEquipQrcode(Long equipId ,String codeUrl);

}
