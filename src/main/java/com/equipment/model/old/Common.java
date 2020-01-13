package com.equipment.model.old;

/**
 * @Author: JavaTansanlin
 * @Description: 请求的公共类
 * @Date: Created in 16:04 2018/7/16
 * @Modified By:
 */
public class Common {

    /** 客户端的标识，默认值为：TCP */
    private String TE;

    /** 每台设备的唯一识别码，例如：A123456 */
    private String MI;

    /** 默认值为空 */
    private String TI;

    /** 类型 */
    private String AT;

    /** 时间截 */
    private String TS;

    public Common(){ }

    public Common(String TE, String MI, String TI, String AT, String TS) {
        this.TE = TE;
        this.MI = MI;
        this.TI = TI;
        this.AT = AT;
        this.TS = TS;
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

    public String getTI() {
        return TI;
    }

    public void setTI(String TI) {
        this.TI = TI;
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
}
