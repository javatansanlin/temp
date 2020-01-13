package com.equipment.util;

/**
 * 描述:
 * 验证实数是否为空
 *
 * @author partner
 * @create 2018-07-17 19:12
 */
public class VerifyData {
    /**
     * 判断整形数据是否为空或者为零
     * @param data
     * @return
     */
    public static boolean intIsNotNullOrZero(Integer data) {
        if (data == null || data <= 0) {
            return false;
        }
        return true;
    }

    public static boolean doubleIsNotNullOrZero(Double data) {
        if (data == null || data <= 0) {
            return false;
        }
        return true;
    }

    /**
     * 判断字符串是否为空或者空字符串
     * @param str
     * @return
     */
    public static boolean strIsNotNull(String str) {
        if (str == null || "".equals(str)) {
            return false;
        }
        return true;
    }

}
