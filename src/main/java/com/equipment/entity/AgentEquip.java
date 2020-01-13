package com.equipment.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Author: JavaTansanlin
 * @Description: 代理商设备
 * @Date: Created in 12:18 2018/8/28
 * @Modified By:
 */
@Data
public class AgentEquip {
    /** 主键id */
    private Long id;
    /** 代理商id **/
    private Long agentId;
    /** 注册时间 **/
    private Date registtime;
    /** 设备二维码 */
    private Long qrcodeStore;
    /** 操作员 */
    private Long operator;
}
