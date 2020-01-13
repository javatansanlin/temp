package com.equipment.model.equipmanager;

import lombok.Data;

import java.util.Date;

/**
 * @Author: JavaTansanlin
 * @Description: 根据店铺查询已绑定的设备的页面显示模板
 * @Date: Created in 12:04 2018/8/22
 * @Modified By:
 */
@Data
public class QueryBundedEquip {

    /** 二维码 */
    private Long qrcodeId;
    /** 设备id */
    private Long id;
    /** 设备编号 */
    private String code;
    /** 类型 */
    private String type;
    /** 卡口数 */
    private Integer carnum;
    /** 绑定时间 */
    private Date registtime;
    /** 绑定人 */
    private String smname;

}
