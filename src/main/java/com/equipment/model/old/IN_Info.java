/**
 * Copyright (C), 2015-2018, 广州云电吧科技有限公司
 * FileName: IN_Info
 * Author:   javatansanlin
 * Date:     2018/7/11 12:21
 * Description: 心跳实体类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.equipment.model.old;

import java.util.List;

/**
 * 〈一句话功能简述〉
 * 〈心跳实体类〉
 *
 * @author javatansanlin
 * @create 2018/7/11
 * @since 1.0.0
 */

public class IN_Info {
    /**客户端的标识，默认值为：TCP*/
    private String TE;
    /**每台设备的唯一识别码，例如：A123456*/
    private String MI;
    /**默认值为空*/
    private String TI;
    /**心跳包，英文为：IN_Info*/
    private String AT;
    /**手机卡号：15920993968*/
    private String SD;
    /**实时时间戳：1511870734*/
    private String TS;
    /**设备*/
    private Drivers DD;
    /** 周围wifi信息（视频机） */
    private List<String> WD;
    /** 目前的网络连接方式(视频机) */
    private String NT;
    /** 目前连接的wifi名称（视频机） */
    private String OW;
    /** 目前的音量，音量值0~15, 0 为静音， 0 为静音， 15 为最大音量（视频机） */
    private String VO;

    private String MAC;

    /**默认构造*/
    public IN_Info() {
    }

    public IN_Info(String TE, String MI, String TI, String AT, String SD, String TS, Drivers DD) {
        this.TE = TE;
        this.MI = MI;
        this.TI = TI;
        this.AT = AT;
        this.SD = SD;
        this.TS = TS;
        this.DD = DD;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
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

    public void setAI(String AT) {
        this.AT = AT;
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

    public Drivers getDD() {
        return DD;
    }

    public void setDD(Drivers DD) {
        this.DD = DD;
    }

    public void setAT(String AT) {
        this.AT = AT;
    }

    public List<String> getWD() {
        return WD;
    }

    public void setWD(List<String> WD) {
        this.WD = WD;
    }

    public String getNT() {
        return NT;
    }

    public void setNT(String NT) {
        this.NT = NT;
    }

    public String getOW() {
        return OW;
    }

    public void setOW(String OW) {
        this.OW = OW;
    }

    public String getVO() {
        return VO;
    }

    public void setVO(String VO) {
        this.VO = VO;
    }
}
