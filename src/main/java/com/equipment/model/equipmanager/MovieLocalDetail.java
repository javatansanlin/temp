package com.equipment.model.equipmanager;

import lombok.Data;

/**
 * @Author: JavaTansanlin
 * @Description: 视频机的公共模板消息
 * @Date: Created in 11:46 2019/4/2
 * @Modified By:
 */
@Data
public class MovieLocalDetail {
    /** 类型：1-上传图片，2-logo，3-微信二维码，4-支付宝二维码，5-二码合一二维码 */
    private Integer type;
    /** 类型名称 */
    private String name;
    /** 连接地址 */
    private String url;

    public MovieLocalDetail(Integer type, String name, String url) {
        this.type = type;
        this.name = name;
        this.url = url;
    }

    public MovieLocalDetail() {
    }
}
