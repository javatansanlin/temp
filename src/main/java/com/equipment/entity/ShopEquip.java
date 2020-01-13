package com.equipment.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Author: JavaTansanlin
 * @Description: 店铺与设备之间的关系类，（以二维码作为媒介相关联）
 * @Date: Created in 16:36 2018/8/22
 * @Modified By:
 */
@Data
public class ShopEquip {
    /** 主键 */
    private Long id;
    /** 店铺编号 **/
    private String code;
    /** 插入时间 */
    private Date registtime;
    /** 设备的二维码（外键：二维码仓库的主键） **/
    private Long qrcodeStore;
    /** 操作人 */
    private Long operator;
}
