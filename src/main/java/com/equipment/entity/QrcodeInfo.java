package com.equipment.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Author: JavaTansanlin
 * @Description: 二维码信息
 * @Date: Created in 17:13 2018/9/17
 * @Modified By:
 */
@Data
public class QrcodeInfo {
    /** 主键 */
    private Long id;
    /** 二维码名称 */
    private String qrName;
    /** 二维码类型 */
    private Integer qrType;
    /** 支付宝商户id */
    private String aliId;
    /** 支付宝应用公钥 */
    private String aliAppPublicKey;
    /** 支付宝应用私钥 */
    private String aliAppPrivateKey;
    /** 支付宝公钥 */
    private String aliPublicKey;
    /** 微信支付商户号 */
    private String wecMchid;
    /** 微信支付apikey */
    private String wecApiKey;
    /** 注册时间 */
    private Date registime;
    /** 操作员id */
    private Integer operator;
    /** 公众号链接 */
    private String qrUrl;
    /** 服务名称 */
    private String payServiceName;
}
