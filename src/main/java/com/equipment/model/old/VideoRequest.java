package com.equipment.model.old;

import lombok.Data;

@Data
public class VideoRequest {

    //设备MAC地址
    private String request_id;

    //material_id
    private String material_id;

    //real_times
    private Integer real_times;

    //os_version
    private String os_version;

    //vendor
    private String vendor;

    //model
    private String model;

    //screen_size
    private String screen_size;

    //ipv4
    private String ipv4;

}
