package com.equipment.service;

import com.equipment.entity.EquipType;
import com.equipment.model.equipmanager.EquipManagePage;
import com.equipment.model.equipmanager.EquipPower;
import com.equipment.model.equipmanager.QueryBundedEquip;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * @Author: JavaTansanlin
 * @Description:
 * @Date: Created in 15:19 2018/8/15
 * @Modified By:
 */
public interface EquipManageService {

    /** 查询所有的设备类型 **/
    List<EquipType> findAllEquipType();

    /** 设备管理的分页，条件查询 */
    PageInfo<EquipManagePage> findEquipManagePageAndByOdition(Integer indexPage, Integer pageCount, String code, Integer equipType , Integer state,
                                                              String shopName , String play , String openId , String agentName, String time);

    /** 设备管理，条件查询所有 */
    List<EquipManagePage> findEquipManageByOdition(String code, Integer equipType , Integer state,
                                                              String shopName , String play , String openId , String agentName, String time);

    /** 查看每台设备的电池 */
    Map<Integer,EquipPower> equipPowersDetail(String equipCode);

    /** 查询店铺已绑定的设备 **/
    List<QueryBundedEquip> bundedByshopCode(String shopCode);

    /** 查询店铺已绑定的设备 **/
    List<QueryBundedEquip> bundedByshopCode(String shopCode,String code);

    /** 查询正常在库的未绑定的设备 **/
    Map<String , Object> findNotBundEquip(Integer indexPage, Integer pageCount , String code);

    /** 店铺通过设备id和店铺编号绑定设备 **/
    Map<String , Object> bundEquip(String shopCode ,Long equipId);

    /** 批量绑定设备  线充 **/
    List bundEquipBatch(String equipCode , Integer num , String shopCode);

    /** 批量绑定设备  线充 **/
    List bundEquipBatchNew(String equipCode[], String shopCode);

    /** 店铺通过扫二维码绑定设备 */
    Map<String ,Object> bundEquip(String shopCode ,String codeUrl);

    /** 解绑设备 **/
    Map<String , Object> untieEquip(Long equipId);

    /** 批量 解绑设备  线充 **/
    List untieEquipBatch(String equipCode , Integer num );

    /** 批量绑定设备  线充 **/
    List untieEquipBatchNew(String equipCode[],String shopCode);

    /** 把所有的设备生成excel文档，并且返回文件名 */
    String createEqEx(String path ,String code , Integer equipType , Integer state, String shopName , String play , String openId , String agentName, String time);

    /** 把所有的设备生成PDF文档，并且下载 */
    byte[] createEqPDF(String path, String code, Integer equipType, Integer state, String shopName, String play, String openId, String agentName, String time);

    /** 伪删除设备（更新设备的出库状态） */
    Map<String,Object> delete(Long id);

    /** 查询设备周围的wifi */
    Map<String,Object> findRoundWIFI(String eqCode);

    /** 根据账户密码连接wifi */
    Map<String,Object> connectWIFI(String eqCode, String name, String pwd);

    //更新当前设备心跳表的心跳时间
    void updateCurrentHeartTs(String mi, String ts);

    //根据设备主键id更新设备心跳信息
    String updateEquipHeartTs(Long id);

    //代理商后台绑定设备（已经绑定了店铺）
    Map bindEqForAgent(String[] equipCode ,String shopCode);
}
