package com.equipment.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Author: JavaTansanlin
 * @Description:视频实体类
 * @Date: Created in 15:12 2018/12/14
 * @Modified By:
 */
@Data
public class Vedeo {
    /** id */
    private Long id;
    /** 文件名 */
    private String fileName;
    /** 文件id */
    private String fileCode;
    /** 视频url */
    private String fileUrl;
    /** 视频md5 */
    private String fileMd5;
    /** 上传时间 */
    private Date createtime;
    /** 所属标签id */
    private Long label;
    /** 是否可用 1-可用，2-不可用 */
    private Integer isdele;
    /** 文件大小 */
    private Long fileSize;
    /** 文件类型 **/
    private String fileType;
    /** 操作员 */
    private Long operator;
}
