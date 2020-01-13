package com.equipment.model.querymodel;

import lombok.Data;

import java.util.Date;

/**
 * @Author: JavaTansanlin
 * @Description:
 * @Date: Created in 14:04 2019/9/19
 * @Modified By:
 */
@Data
public class FindQrcodeStore {
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
    /** 公众号链接 */
    private String qrUrl;
    /** 服务名称 */
    private String payServiceName;
}
