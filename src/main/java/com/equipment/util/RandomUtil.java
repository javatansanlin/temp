package com.equipment.util;

import java.util.Random;

/**
 * 描述:
 *
 * @author partner
 * @create 2018-08-20 19:58
 */
public class RandomUtil {
    private static Random random = new Random();

    public static String getRandomString(Integer length) {
        //含有字符和数字的字符串
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        //随机类初始化
        Random random = new Random();
        //StringBuffer类生成，为了拼接字符串
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < length; ++i) {
            // [0,62)
            int number = random.nextInt(62);

            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static String getRandomInt(Integer length) {
        //含有字符和数字的字符串
        String str = "0123456789";
        //随机类初始化
        Random random = new Random();
        //StringBuffer类生成，为了拼接字符串
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < length; ++i) {
            // [0,62)
            int number = random.nextInt(10);

            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    /**
     *
     * @return
     */
    public static String getRandomStringFiveLength() {
        return  ((int)(random.nextDouble() * (99999 - 10000 + 1)) + 10000)+"";
    }

}
