package com.equipment.model.old;

/**
 * @Author: JavaTansanlin
 * @Description: 设备端返回租借Json (请求成功或者失败) 实体类
 * @Date: Created in 18:41 2018/7/16
 * @Modified By:
 */
public class IN_Borrow {

    /** 客户端的标识，默认值为：TCP */
    private String TE;

    /** 每台设备的唯一识别码，例如：A123456 */
    private String MI;

    /** 返回给服务器请求的类型，租借的标识为：Borrow */
    private String AT;

    /** 动作码，原样返回 */
    private String AC;

    /** 手机卡号：15920993968 */
    private String SD;

    /** 实时时间戳：1511870734 */
    private String TS;

    /** 返回值表示：ok-成功，err */
    private String SC;

    /** 错误时返回 - 返回服务器指令弹出的电池的ID，例如：DC000001 */
    private String BI;

    /** 如果租借出错，则载入错误说明方便后台技术人员查找问题，例如：err Borrow */
    private String CD;

    /** 租借的电池 */
    private Battery ZJ;

    /** 租借的设备 */
    private Drivers DD;

    public IN_Borrow(){ }

    public IN_Borrow(String TE, String MI, String AT, String AC, String SD, String TS, String SC, String BI, String CD, Battery ZJ, Drivers DD) {
        this.TE = TE;
        this.MI = MI;
        this.AT = AT;
        this.AC = AC;
        this.SD = SD;
        this.TS = TS;
        this.SC = SC;
        this.BI = BI;
        this.CD = CD;
        this.ZJ = ZJ;
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

    public String getAC() {
        return AC;
    }

    public void setAC(String AC) {
        this.AC = AC;
    }

    public String getSD() {
        return SD;
    }

    public void setSD(String SD) {
        this.SD = SD;
    }

    public String getTS() {
        return TS;
    }

    public void setTS(String TS) {
        this.TS = TS;
    }

    public String getSC() {
        return SC;
    }

    public void setSC(String SC) {
        this.SC = SC;
    }

    public String getBI() {
        return BI;
    }

    public void setBI(String BI) {
        this.BI = BI;
    }

    public String getCD() {
        return CD;
    }

    public void setCD(String CD) {
        this.CD = CD;
    }

    public Battery getZJ() {
        return ZJ;
    }

    public void setZJ(Battery ZJ) {
        this.ZJ = ZJ;
    }

    public Drivers getDD() {
        return DD;
    }

    public void setDD(Drivers DD) {
        this.DD = DD;
    }
}
