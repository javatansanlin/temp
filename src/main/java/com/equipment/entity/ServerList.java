package com.equipment.entity;

import java.util.Date;

/**
 * 服务器列表
 */
public class ServerList {

    /** 主键 **/
    private Long id;

    /** 操作员 - SysMember外键 **/
    private Long operator;

    /** 服务器名称 **/
    private String name;

    /** 服务器地址 **/
    private String serverIp;

    /** 服务器端口 **/
    private String serverPort;

    /** 注册时间 **/
    private Date registtime;

    public ServerList(){  }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOperator() {
        return operator;
    }

    public void setOperator(Long operator) {
        this.operator = operator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp == null ? null : serverIp.trim();
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort == null ? null : serverPort.trim();
    }

    public Date getRegisttime() {
        return registtime;
    }

    public void setRegisttime(Date registtime) {
        this.registtime = registtime;
    }
}