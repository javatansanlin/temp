package com.equipment.entity;

import lombok.Data;

import java.util.Date;

@Data
public class RankShop {
    private Long id;

    private Long area;

    private Long manager;

    private Long plat;

    private Long provinceAgent;

    private Long cityAgent;

    private Long areaAgent;

    private Long salesAgent;

    private String code;

    private String sName;

    private String sPhone;

    private String sAddress;

    private String sLogo;

    private String sCoordinate;

    private String shopRequire;

    private String shopProfit;

    private String settleAsk;

    private String remark;

    private Integer freeUsetime;

    private Double rentCost;

    private Double highCost;

    private Integer platExtract;

    private Integer provinceExtact;

    private Integer cityExtact;

    private Integer areaExtact;

    private Integer salesExtact;

    private Integer shopExtact;

    private Double shopProfitMoney;

    private Integer isblock;

    private Integer isquick;

    private String isonecode;

    private Date registtime;

    private Long operator;

    private Double shopDeposit;

    private Double rentCostForeign;

    private Double highCostForeign;

    private Integer shopPayType;

    private Integer unitMinute;

    private String unitMinuteStr;

}