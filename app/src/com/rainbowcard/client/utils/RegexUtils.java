package com.rainbowcard.client.utils;

import android.text.TextUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    final static Map<Integer, String> zoneNum = new HashMap<Integer, String>();

    static {
        zoneNum.put(11, "北京");
        zoneNum.put(12, "天津");
        zoneNum.put(13, "河北");
        zoneNum.put(14, "山西");
        zoneNum.put(15, "内蒙古");
        zoneNum.put(21, "辽宁");
        zoneNum.put(22, "吉林");
        zoneNum.put(23, "黑龙江");
        zoneNum.put(31, "上海");
        zoneNum.put(32, "江苏");
        zoneNum.put(33, "浙江");
        zoneNum.put(34, "安徽");
        zoneNum.put(35, "福建");
        zoneNum.put(36, "江西");
        zoneNum.put(37, "山东");
        zoneNum.put(41, "河南");
        zoneNum.put(42, "湖北");
        zoneNum.put(43, "湖南");
        zoneNum.put(44, "广东");
        zoneNum.put(45, "广西");
        zoneNum.put(46, "海南");
        zoneNum.put(50, "重庆");
        zoneNum.put(51, "四川");
        zoneNum.put(52, "贵州");
        zoneNum.put(53, "云南");
        zoneNum.put(54, "西藏");
        zoneNum.put(61, "陕西");
        zoneNum.put(62, "甘肃");
        zoneNum.put(63, "青海");
        zoneNum.put(64, "宁夏");
        zoneNum.put(65, "新疆");
        zoneNum.put(71, "台湾");
        zoneNum.put(81, "香港");
        zoneNum.put(82, "澳门");
        zoneNum.put(91, "国外");
    }

    final static int[] PARITYBIT = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
    final static int[] POWER_LIST = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10,
            5, 8, 4, 2};
    /*
    [1] |（竖线符号）124
    [2] & （& 符号）38
    [3];（分号）59
    [4] $（美元符号）36
    [5] %（百分比符号）37
    [6] @（at 符号）64
    [7] '（单引号）39
    [8] ""（引号）34
    [9] \'（反斜杠转义单引号）92
    [10] \""（反斜杠转义引号）
    [11] <>（尖括号）60 62
    [12] ()（括号）40 41
    [13] +（加号）43
    [14] CR（回车符，ASCII 0x0d）13
    [15] LF（换行，ASCII 0x0a）10
    [16] ,（逗号）
    [17] \（反斜杠）" 92
*/
    private static int[] strangeCharAscArray = new int[]{124, 38, 59, 36, 37, 64, 39, 34, 92, 60, 62, 40, 41, 43, 10, 13, 92};

    /**
     * 邮箱验证
     *
     * @param mail
     * @return
     */
    public static boolean checkMail(String mail) {
        String mailregex = "^([a-z0-9A-Z]+[-|\\.|_]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,3}$";
        return match(mailregex, mail);
    }

    /**
     * 手机验证
     * 除去154,184 的所有  13,15,18的号段
     * 加入147号段
     *
     * @param mobile
     * @return
     */
    public static boolean checkPhone(String mobile) {
        String mobileregex = "^((13[0-9])|147|(15[^4,\\D])|(18[^4,\\D]))\\d{8}$";
        return match(mobileregex, mobile);
    }

    /**
     * 身份证号是否基本有效
     *
     * @param s 号码内容
     * @return 是否有效，null和""都是false
     */
    public static boolean checkIdCard(String s) {
        if (s == null || (s.length() != 15 && s.length() != 18))
            return false;
        final char[] cs = s.toUpperCase().toCharArray();
        // （1）校验位数
        int power = 0;
        for (int i = 0; i < cs.length; i++) {// 循环比正则表达式更快
            if (i == cs.length - 1 && cs[i] == 'X')
                break;// 最后一位可以是X或者x
            if (cs[i] < '0' || cs[i] > '9')
                return false;
            if (i < cs.length - 1)
                power += (cs[i] - '0') * POWER_LIST[i];
        }
        // （2）校验区位码
        if (!zoneNum.containsKey(Integer.valueOf(s.substring(0, 2)))) {
            return false;
        }
        // （3）校验年份
        String year = s.length() == 15 ? "19" + s.substring(6, 8) : s
                .substring(6, 10);
        final int iyear = Integer.parseInt(year);
        if (iyear < 1900 || iyear > Calendar.getInstance().get(Calendar.YEAR)) {
            return false;// 1900年的PASS，超过今年的PASS
        }
        // （4）校验月份
        String month = s.length() == 15 ? s.substring(8, 10) : s.substring(10,
                12);
        final int imonth = Integer.parseInt(month);
        if (imonth < 1 || imonth > 12)
            return false;
        // （5）校验天数
        String day = s.length() == 15 ? s.substring(10, 12) : s.substring(12,
                14);
        final int iday = Integer.parseInt(day);
        if (iday < 1 || iday > 31)
            return false;
        // （6）校验一个合法的年月日
        if (!validate(iyear, imonth, iday))
            return false;
        // （7）校验“校验码”
        if (s.length() == 15)
            return true;
        return cs[cs.length - 1] == PARITYBIT[power % 11];
    }

    static boolean validate(int year, int month, int day) {
        //比如考虑闰月，大小月等
        return true;
    }

    public static boolean checkLength(String str, int minLength, int maxLength) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return str.length() >= minLength && str.length() <= maxLength;
    }

    public static boolean isEquals(String str1, String str2) {
        if (TextUtils.isEmpty(str1) || TextUtils.isEmpty(str2)) {
            return false;
        }
        return str1.equals(str2);
    }

    /**
     * 正则验证方法
     *
     * @param regexstr
     * @param str
     * @return
     */
    public static boolean match(String regexstr, String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Pattern regex = Pattern.compile(regexstr, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = regex.matcher(str);
        return matcher.matches();
    }

    public static boolean checkStrangeChar(String str) {

        char[] charArray = str.toCharArray();
        for (char a : charArray) {
            for (int b : strangeCharAscArray) {
                if (a == b) {
                    return false;
                }
            }
        }
        return true;
    }

}
