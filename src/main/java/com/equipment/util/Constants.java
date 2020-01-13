package com.equipment.util;

public class Constants {
    public static final String DEFAULT_EQ_HEART_TIME_VALUE = "设备长时间未开机";

    /**
     * 电池管理
     */
    public static final Integer BORROW_STATUS_1 = 1;//租借状态 待租（在库）
    public static final Integer BORROW_STATUS_2 = 2;//租借状态 在租（借出）
    public static final Integer POWER_STATUS_1 = 1;//电池状态 不锁定
    public static final Integer POWER_STATUS_2 = 2;//电池状态 锁定

    /**
     * 电池redis中相关key
     */
    public static final String POWER_UPDATE_LIST_KEY = "UPDATE-POWERINFO";//需要更新的电池队列key，value为电池编号
    public static final String POWER_POWER_CODE_KEY = "-PowerCode";//电池编号key
    public static final String POWER_BORROW_TIMES_KEY = "-BorrowTimes";//电池租借次数key
    public static final String POWER_ERROR_TIMES_KEY = "-ErrorTimes";//电池错误次数key
    public static final String POWER_POWER_NUMS_KEY = "-PowerNums";//电池电量key
    public static final String POWER_ONLINE_EQUIP_KEY = "-OnlineEquip";//电池所在设备key
    public static final String POWER_BEFORE_EQUIP_KEY = "-BeforeEquip";//电池上一设备key
    public static final String POWER_BORROW_STATUS_KEY = "-BorrowStatus";//电池租借状态key
    public static final String POWER_POWER_STATUS_KEY = "-PowerStatus";//电池锁定状态key
    public static final String POWER_USE_MINUTES_KEY = "-UseMinutes";//电池使用时长key
    public static final String POWER_POWER_PROFIT_KEY = "-PowerProfit";//电池收益key
}
