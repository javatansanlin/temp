/**
 * Copyright (C), 2015-2018, 广州云电吧科技有限公司
 * FileName: Battery
 * Author:   javatansanlin
 * Date:     2018/7/11 12:29
 * Description: 电池类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.equipment.model.old;

/**
 * 〈一句话功能简述〉
 * 〈电池类〉
 *
 * @author javatansanlin
 * @create 2018/7/11
 * @since 1.0.0
 */

public class Battery {
    /**电池1的ID，例如：DC000001*/
    private String BI;
    /**电池所在的卡口，例如：0*/
    private String BO;
    /**电池的电量*/
    private String BC;
    /**插线正常*/
    private String WI;
    /**设备正常*/
    private String ST;

    public Battery(){ }

    public Battery(String BI, String BO, String BC, String WI, String ST) {
        this.BI = BI;
        this.BO = BO;
        this.BC = BC;
        this.WI = WI;
        this.ST = ST;
    }

    public String getBI() {
        return BI;
    }

    public void setBI(String BI) {
        this.BI = BI;
    }

    public String getBO() {
        return BO;
    }

    public void setBO(String BO) {
        this.BO = BO;
    }

    public String getBC() {
        return BC;
    }

    public void setBC(String BC) {
        this.BC = BC;
    }

    public String getWI() {
        return WI;
    }

    public void setWI(String WI) {
        this.WI = WI;
    }

    public String getST() {
        return ST;
    }

    public void setST(String ST) {
        this.ST = ST;
    }
}
