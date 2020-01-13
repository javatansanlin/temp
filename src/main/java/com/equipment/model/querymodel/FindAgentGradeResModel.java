package com.equipment.model.querymodel;

import lombok.Data;

/**
 * @Author: JavaTansanlin
 * @Description: 设备管理查询用到的，查询代理属于哪一级代理的返回值model
 * @Date: Created in 16:54 2018/8/18
 * @Modified By:
 */
@Data
public class FindAgentGradeResModel {

    /** 代理id */
    private Long id;

    /** 所属代理级别 **/
    private Integer mGroup;
}
