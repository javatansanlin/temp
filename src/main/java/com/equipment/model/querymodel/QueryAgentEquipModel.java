package com.equipment.model.querymodel;

import lombok.Data;

/**
 * @Author: JavaTansanlin
 * @Description: 查询代理设备的页面显示的model
 * @Date: Created in 13:57 2018/8/28
 * @Modified By:
 */
@Data
public class QueryAgentEquipModel {
    /** 设备id */
    private Long id;
    /** 设备编号 **/
    private String code;
    /** 卡口数量 */
    private Integer carnum;
    /** 已借卡口数量 **/
    private String bonum;
    /** 在线时间 */
    private String time;
    /** 所属店铺 */
    private String store;
}
