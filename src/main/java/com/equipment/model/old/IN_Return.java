package com.equipment.model.old;

/**
 * @Author: JavaTansanlin
 * @Description: 店铺设备端发送归还请求  实体类
 * @Date: Created in 14:56 2018/7/18
 * @Modified By:
 */
public class IN_Return {
    /** 客户端的标识，默认值为：TCP */
    private String TE;

    /** 每台设备的唯一识别码，例如：A123456 */
    private String MI;

    /** 归还，应为标识为：Return */
    private String AT;

    /** 实时时间戳：1511870734 */
    private String TS;

    /** 租借电池返回的电池类 */
    private Battery GH;

    /**设备*/
    private Drivers DD;

    public IN_Return(){

    }

    public IN_Return(String TE, String MI, String AT, String TS, Battery GH, Drivers DD) {
        this.TE = TE;
        this.MI = MI;
        this.AT = AT;
        this.TS = TS;
        this.GH = GH;
        this.DD = DD;
    }

    public String getTE() {
        return TE;
    }

    public void setTE(String TE) {
        this.TE = TE;
    }

    public String getMI() {
        return MI;
    }

    public void setMI(String MI) {
        this.MI = MI;
    }

    public String getAT() {
        return AT;
    }

    public void setAT(String AT) {
        this.AT = AT;
    }

    public String getTS() {
        return TS;
    }

    public void setTS(String TS) {
        this.TS = TS;
    }

    public Battery getGH() {
        return GH;
    }

    public void setGH(Battery GH) {
        this.GH = GH;
    }

    public Drivers getDD() {
        return DD;
    }

    public void setDD(Drivers DD) {
        this.DD = DD;
    }
}
