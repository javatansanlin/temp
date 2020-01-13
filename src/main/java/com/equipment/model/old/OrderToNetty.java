package com.equipment.model.old;

/**
 * @Author: JavaTansanlin
 * @Description: 订单服务通知netty服务进行租借弹出   包装类
 * @Date: Created in 19:10 2018/8/1
 * @Modified By:
 */
public class OrderToNetty {

    //订单编号
    private String orderCode;
    //设备编号
    private String equipCode;
    //电池编号
    private String powerBi;
    //时间截
    private String time;
    //类型
    private String type;

    //无参构造
    public OrderToNetty(){ }

    //全部构造
    public OrderToNetty(String orderCode, String equipCode, String powerBi, String time, String type) {
        this.orderCode = orderCode;
        this.equipCode = equipCode;
        this.powerBi = powerBi;
        this.time = time;
        this.type = type;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getEquipCode() {
        return equipCode;
    }

    public void setEquipCode(String equipCode) {
        this.equipCode = equipCode;
    }

    public String getPowerBi() {
        return powerBi;
    }

    public void setPowerBi(String powerBi) {
        this.powerBi = powerBi;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
