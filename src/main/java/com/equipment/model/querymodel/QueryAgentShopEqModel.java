package com.equipment.model.querymodel;

import lombok.Data;

/**
 * @Author: JavaTansanlin
 * @Description: 查询代理设备的页面显示model
 * @Date: Created in 20:57 2018/9/6
 * @Modified By:
 */
@Data
public class QueryAgentShopEqModel {

    /** 设备id */
    private Long id;
    /** 设备编号 **/
    private String code;
    /** 可借数量 **/
    private String cb;
    /** 可还数量 **/
    private String cr;
    /** 在线状态 :1-在线，2-离线 */
    private Integer state;
    /** 所属店铺名 **/
    private String sName;

}
