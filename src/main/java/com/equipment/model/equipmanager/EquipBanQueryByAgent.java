package com.equipment.model.equipmanager;

import lombok.Data;

/**
 * @Author: JavaTansanlin
 * @Description: AgentManagerDao中的findEqContByCodeAndAgentId方法所用到的结果返回
 * @Date: Created in 23:43 2018/10/19
 * @Modified By:
 */
@Data
public class EquipBanQueryByAgent {
    /** 设备id */
    private Long eqId;
    /** 店铺设备id */
    private Long shopEqId;
    /** 二维码id */
    private Long qrCodeId;
}
