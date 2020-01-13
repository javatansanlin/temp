package com.equipment.model.equipmanager;

import lombok.Data;

import java.util.Date;

/**
 * 电池信息对象
 */
@Data
public class PowerInfo {
    private Long id;//主键id

    private String powerCode;//电池编号

    private Date registTime;//注册时间

    private Integer borrowTimes;//租借次数

    private Integer errorTimes;//错误次数

    private Integer fixTimes;//维修次数

    private Integer powerNums;//电池电量

    private String onlineEquip;//当前设备

    private String beforeEquip;//上一次设备

    private Date updateTime;//更新时间

    private Integer borrowStatus;//租借状态

    private Integer powerStatus;//电池锁定状态

    private Long useMinutes;//使用时长

    private Integer totalErrorTimes;//总错误次数

}
