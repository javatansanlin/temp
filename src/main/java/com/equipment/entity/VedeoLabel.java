package com.equipment.entity;

import lombok.Data;

/**
 * @Author: JavaTansanlin
 * @Description:视频标签实体类
 * @Date: Created in 20:13 2018/12/13
 * @Modified By:
 */
@Data
public class VedeoLabel {
    /** id */
    private Long id;
    /** 标签吗，名字 */
    private String labelName;
    /** 操作员 */
    private Long operator;
}
