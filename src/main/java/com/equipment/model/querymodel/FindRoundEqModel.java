package com.equipment.model.querymodel;

import lombok.Data;

/**
 * @Author: JavaTansanlin
 * @Description:
 * @Date: Created in 16:09 2018/10/11
 * @Modified By:
 */
@Data
public class FindRoundEqModel {
    /** 主键 */
    private Long id;
    /** 设备编号 */
    private String mi;
    /** 可借电池 */
    private String cb;
    /** 可还电池 */
    private String cr;
    /** 设备所在的经度 */
    private Double lo;
    /** 设备所在的纬度 */
    private Double la;
    /** 店铺名 */
    private String sName;
    /** 店铺logo */
    private String sLogo;
    /** 店铺租金 */
    private Double rentCost;
    /** 店铺地址 */
    private String address;
    /** 设备状态 1-在线 ，2-离线*/
    private Integer state;
    /** 营业时间*/
    private String businessHours;
}
