package com.equipment.service;

import com.equipment.entity.QrcodeInfo;
import com.equipment.model.equipmanager.QRManger;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * @Author: JavaTansanlin
 * @Description: 二维码相关的业务接口
 * @Date: Created in 20:31 2018/9/6
 * @Modified By:
 */
public interface QrcodeService {

    /** 查询出未绑定的二维码,并且生成二维码图片，打包成zip文件，并且返回路径 */
    Map<String ,Object> qrCodeDown(String id ,Integer typeId);

    /** 根据店铺名称，设备号，添加时间，是否绑定设备。查询二维码信息 */
    PageInfo<QRManger> findQr(String shopName ,String eqCode ,String time ,Integer isBan,Integer pageIndex ,Integer pageCount,Integer infoId);

    /** 删除选择的二维码 */
    Map<String ,Object> deleSelect(String id);

    /** 删除全二维码 */
    Map<String ,Object> deleAll();

    /** 查询所有的二维码信息 */
    List<QrcodeInfo> findAllInfo();

    /** 根据设备编号查询二维码资料 */
    Map<String,Object> findCodeByEqCode(String code);

    /** 【后台系统】根据设备号查询可以解绑的设备 */
    Map<String,Object> queryCanUntie(String data ,String key);

    /** 验证key */
    boolean keyUp(String key);

    /** 【后台系统】根据设备编号解绑设备 */
    Map<String,Object> untieEq(String data ,String key);

    /** 【后台系统】针对mqtt的视频机更新设备服务器的二维码 */
    Map<String,Object> updateMqEqQrCode(String eqCode);

    /** 针对只生成二码合一的二维码 */
    Map<String,Object> createQr(Integer num, Long qrInfoId);

    /** 批量绑定线充设备 */
    Map<String,Object>  genLineChargerCode(Long num,Integer type);
}
