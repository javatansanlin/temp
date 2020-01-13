package com.equipment.entity;

import lombok.Data;

/**
 * 设备心跳详情
 */
@Data
public class EquipHeartDetail {

    /** 主键 **/
    private Long id;

    /** 客户端需识别此参数，默认为：WEB **/
    private String te;

    /** 设备的唯一识别码 **/
    private String mi;

    /** 默认值为空 **/
    private String ti;

    /** 服务器请求的类型 ：BORROW = ‘借’；RETURN = ‘归还’；SERVICE = ‘维修’；**/
    private String at;

    /** 手机卡号 **/
    private String sd;

    /** 实时时间戳 **/
    private String ts;

    /** 设备的唯一识别码 **/
    private String di;

    /** 设备标识 - 电池版：Battery **/
    private String dt;

    /** 设备所在经纬度 **/
    private String ll;

    /** 当前电池数量 **/
    private String bc;

    /** 可借数量 **/
    private String cb;

    /** 可还数量 **/
    private String cr;

    /** 测试  - 如果租借出错，则载入错误说明方便后台技术人员查找问题，例如：err Borrow**/
    private String cd;

    /** 测试 - 错误：err； 通过： ok； **/
    private String sc;

    /** 经度 */
    private Double lo;

    /** 纬度 */
    private Double la;

    /** 当前视频播放 */
    private String videos;

    /** 根据设备号查询手机号码，卡口总数，可借数，可还数的构造 */
    public EquipHeartDetail(String sd, String bc, String cb, String cr) {
        this.sd = sd;
        this.bc = bc;
        this.cb = cb;
        this.cr = cr;
    }

    /** 默认构造 */
    public EquipHeartDetail() {
    }

    /** 全部构造 */
    public EquipHeartDetail(Long id, String te, String mi, String ti, String at, String sd, String ts, String di, String dt, String ll, String bc, String cb, String cr, String cd, String sc) {
        this.id = id;
        this.te = te;
        this.mi = mi;
        this.ti = ti;
        this.at = at;
        this.sd = sd;
        this.ts = ts;
        this.di = di;
        this.dt = dt;
        this.ll = ll;
        this.bc = bc;
        this.cb = cb;
        this.cr = cr;
        this.cd = cd;
        this.sc = sc;
    }
}