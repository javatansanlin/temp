package com.equipment.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @Author: JavaTansanlin
 * @Description: 时间相关的操作工具类
 * @Date: Created in 17:00 2018/7/16
 * @Modified By:
 */
public class DateUtil {

    /** 时间转换为unix时间截 */
    public static String Date2TimeStamp(Date date) {
        try {
            return String.valueOf(date.getTime() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Java将Unix时间戳转换成指定格式日期字符串
     * @param timestampString 时间戳 如："1473048265";
     * @param formats 要格式化的格式 默认："yyyy-MM-dd HH:mm:ss";
     *
     * @return 返回结果 如："2016-09-05 16:06:42";
     */
    public static String TimeStamp2Date(String timestampString, String formats) {
        Long timestamp = Long.parseLong(timestampString) * 1000;
        String date = new SimpleDateFormat(formats, Locale.CHINA).format(new Date(timestamp));
        return date;
    }

    /**
     * 获取现在时间
     *
     * @return 返回短时间字符串格式yyyy-MM-dd
     */
    public static String getStringDate(Date time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(time);
        return dateString;
    }

    /**
     * 获取现在时间
     *
     * @return 返回短时间字符串格式yyyy-MM-dd
     */
    public static Date getDateString(String timeString) throws Exception{
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = formatter.parse(timeString);
        return date;
    }

    /**
     * 根据店铺的单位收费和收费标准计算显示的值
     * @param unitMinute
     * @return
     */
    public static String getShopUnitMinute(Double rentCost, Integer unitMinute){
        try{
            if(unitMinute == null){
                return "";
            }
            String unitMinuteStr = "";
            if(unitMinute == 60){
                unitMinuteStr = rentCost+"元/小时";
            }else if(unitMinute == 30){
                unitMinuteStr = rentCost+"元/半小时";
            }else if(unitMinute % 60 == 0){
                //按小时显示 ¥2.0/小时
                unitMinuteStr = rentCost+"元/"+(new BigDecimal(unitMinute+"").divide(new BigDecimal(60+""))) + "小时";
            }else{
                //按分钟显示 ¥2.0/40分钟
                unitMinuteStr = rentCost+"元/"+unitMinute + "分钟";
            }
            return unitMinuteStr;
        }catch(Exception e){
            return "";
        }
    }

}
