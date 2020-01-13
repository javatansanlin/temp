/**
 * Copyright (C), 2015-2018, 广州云电吧科技有限公司
 * FileName: Drivers
 * Author:   javatansanlin
 * Date:     2018/7/11 12:28
 * Description: 设备类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.equipment.model.old;

import java.util.List;

/**
 * 〈一句话功能简述〉
 * 〈设备类〉
 *
 * @author javatansanlin
 * @create 2018/7/11
 * @since 1.0.0
 */

public class Drivers {
    /**每台设备的唯一识别码，例如：A123456*/
    private String DI;
    /**设备标识，电池版：Battery*/
    private String DT;
    /**设备所在经纬度，例如：31.1594949,132.5984155*/
    private String LL;
    /**当前电池数量，例如：10*/
    private String BC;
    /**可借数量，例如：7*/
    private String CB;
    /**可还数量，例如3*/
    private String CR;
    /**电池*/
    private List<Battery> BL;
    /** 信号强度 */
    private String SG;
    /** 播放的视频 */
    private List<IN_Video> Videos;

    private String VE;

    /*默认构造*/
    public Drivers(){}

    public Drivers(String DI, String DT, String LL, String BC, String CB, String CR, List<Battery> BL) {
        this.DI = DI;
        this.DT = DT;
        this.LL = LL;
        this.BC = BC;
        this.CB = CB;
        this.CR = CR;
        this.BL = BL;
    }

    public String getVE() {
        return VE;
    }

    public void setVE(String VE) {
        this.VE = VE;
    }

    public String getDI() {
        return DI;
    }

    public void setDI(String DI) {
        this.DI = DI;
    }

    public String getDT() {
        return DT;
    }

    public void setDT(String DT) {
        this.DT = DT;
    }

    public String getLL() {
        return LL;
    }

    public void setLL(String LL) {
        this.LL = LL;
    }

    public String getBC() {
        return BC;
    }

    public void setBC(String BC) {
        this.BC = BC;
    }

    public String getCB() {
        return CB;
    }

    public void setCB(String CB) {
        this.CB = CB;
    }

    public String getCR() {
        return CR;
    }

    public void setCR(String CR) {
        this.CR = CR;
    }

    public List<Battery> getBL() {
        return BL;
    }

    public void setBL(List<Battery> BL) {
        this.BL = BL;
    }

    public String getSG() {
        return SG;
    }

    public void setSG(String SG) {
        this.SG = SG;
    }

    public List<IN_Video> getVideos() {
        return Videos;
    }

    public void setVideos(List<IN_Video> videos) {
        Videos = videos;
    }
}
