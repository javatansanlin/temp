package com.equipment.model.equipmanager;

import lombok.Data;

/**
 * @Author: JavaTansanlin
 * @Description: 设备管理页面的列表model显示
 * @Date: Created in 15:35 2018/8/15
 * @Modified By:
 */
@Data
public class EquipManagePage {
    /** 设备id **/
    private Long id;
    /** 设备号 **/
    private String code;
    /** 店铺编号 **/
    private String scode;
    /** 手机号 **/
    private String sd;
    /** 设备版本 */
    private String name;
    /** 状态 **/
    private Long state;
    /** 电池总量 **/
    private String bc;
    /** 可借数量 **/
    private String cb;
    /** 可还数量 **/
    private String cr;
    /** 实时时间戳 **/
    private String ts;
    /** 店铺名 */
    private String rsname;
    /** 所属店铺管理员 **/
    private String manager;
    /** 所属平台名 **/
    private String apname;
    /** 所属省级代理名 **/
    private String paname;
    /** 所属市级代理名 **/
    private String caname;
    /** 所属区域代理名 **/
    private String aaname;
    /** 所属业务代理名 **/
    private String saname;
    /** 最近的心跳时间 */
    private String rtime;
    /** 当前的wifi名称 */
    private String ow;
    /** 4G或者2G的信号强度 */
    private String sg;

    private Long ehdId;//当前心跳表的主键ID

    /** 房间数量 */
    private Long roomNum;

    /** 生产日期 */
    private String production;
}
