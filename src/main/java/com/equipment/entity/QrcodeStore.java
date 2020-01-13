package com.equipment.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Author: JavaTansanlin
 * @Description: 二维码仓库表
 * @Date: Created in 15:29 2018/8/26
 * @Modified By:
 */
@Data
public class QrcodeStore {
    /** 主键 */
    private Long id;
    /** 二维码的信息（外键） */
    private Long qrcode;
    /** 绑定设备（外键） */
    private Long equip;
    /** 编号 **/
    private String code;
    /** 是否二码合一 **/
    private Integer isonecode;
    /** 是否绑定 */
    private Integer isbind;
    /** 微信的ticket */
    private String wechatTicket;
    /** 微信链接 */
    private String wechatQrcode;
    /** 支付宝链接 */
    private String aliQrcode;
    /** 二码合一的链接 */
    private String oneQrcode;
    /** 备注信息 */
    private String remark;
    /** 注册时间 */
    private Date registime;
    /** 操作员 */
    private Long operator;
}
