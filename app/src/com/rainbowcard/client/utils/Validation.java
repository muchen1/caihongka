package com.rainbowcard.client.utils;

/*
 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.rainbowcard.client.common.utils.DLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author gc
 */
public class  Validation {

    private static Pattern chinaStr = Pattern.compile("^[\\u4E00-\\u9FA5]+(·[\\u4E00-\\u9FA5]+)*$");
    private static Pattern userStr = Pattern.compile("^[0-9A-Za-z_]+$");
    private static Pattern emailStr = Pattern.compile("^(\\w+)@(\\w+)(\\.\\w+)+");
    private static Pattern dateStr = Pattern.compile("^(\\d{4})[-/](\\d{1,2})[-/](\\d{1,2})$");
    private static Pattern idno15 = Pattern.compile("^([1-9][0-9]{14})$");
    private static Pattern idno18 = Pattern.compile("^([1-9][0-9]{16}[0-9Xx])");
//    private static Pattern mobile = Pattern.compile("^([1][3758][0-9]{9})$");
    private static Pattern mobile = Pattern.compile("^((13[0-9])|(14[7])|(15[^4,\\D])|(18[0-9])|(19[0-9])|(17[0-9]))\\d{8}$");  ;
    private static Pattern zip = Pattern.compile("^[0-9][0-9]{4}$");
    private static Pattern ver_code = Pattern.compile("^[0-9][0-9]{1}$");
    private static Pattern creditcard = Pattern.compile("^[0-9][0-9]{15}$");
    private static Pattern nameStr = Pattern.compile("^[a-zA-Z\\u4e00-\\u9fa5]+$");
    /**
     * 判断手机号码正确性
     *
     * @return
     */
    public static boolean isZip(String zipstr) {
        return zip.matcher(zipstr).matches();
    }
    /**
     * 判断验证码合法性
     * 6位随机数字
     *
     * @param codestr
     * @return
     */
    public static boolean isVer_Code(String codestr){
    	return ver_code.matcher(codestr).matches();
    }
    /**
     * 验证信用卡号合法性
     * 此处简单判断是否为16位数字
     *
     * @param creditstr
     * @return
     */
    public static boolean isCreditCard(String creditstr){
    	return creditcard.matcher(creditstr).matches();
    }
    /**
     * 是否有效的真实姓名，姓名要求必须中文。或者外文音译，名字中间最多有一个英文半角字符点(.)
     *
     * @param name
     * @return
     */
    public static boolean isChinaStr(String name) {
        return chinaStr.matcher(name).matches();
    }

    //是否有效姓名，姓名为中英文
    public static boolean isNameStr(String name){
        return nameStr.matcher(name).matches();//姓名为中英文
        /*if(nameStr.matcher(name).matches()){
            if (isEnglish(name)){
                return true;
            }else {
                return isChinaStr(name);
            }
        }else {
            return false;
        }*/
    }


    /**
     * 是否是英文
     */
    public static boolean isEnglish(String charaString){
        return charaString.matches("^[a-zA-Z]*");
    }

    /**
     * 判断手机号码正确性
     *
     * @param mobilestr
     * @return
     */
    public static boolean isMobile(String mobilestr) {
        return mobile.matcher(mobilestr).matches();
    }

    /**
     * 校验是否有效注册系统用户名用户名的有效范围是英文字母 和阿拉伯数字
     *
     * @param user
     * @return
     */
    public static boolean isUserStr(String user) {

        return userStr.matcher(user).matches();

    }

    /**
     * 校验是否有效的电子邮箱
     *
     * @param email
     * @return
     */
    public static boolean isMailStr(String email) {

        return emailStr.matcher(email).matches();

    }

    private static String countCheckCode(String idno) {
        //长度检查

        if (idno.length() != 15
                && idno.length() != 18) {
            return null;
        }

        //生成前17位身份证号
        String idno17 = "";
        if (idno.length() == 15) {
            idno17 = idno.substring(0, 6) + "19" + idno.substring(6, 9);
        }
        if (idno.length() == 18) {
            idno17 = idno.substring(0, 17);
        }

        //生成校验码
        int[] iW = new int[]{7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2}; //加权因子常数
        String lastCode = "10X98765432";

        //进行加权求和
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum = sum + Integer.parseInt(idno17.substring(i, i+1)) * iW[i];
        }
        int mod = sum % 11;//取模运算，得到模值
        DLog.e("lastCode.substring(mod, mod+1).toUpperCase():" +
                lastCode.substring(mod, mod + 1).toUpperCase());
        return lastCode.substring(mod, mod+1).toUpperCase();
    }

    /**
     * 校验日期是否正确
     *
     * @param date
     * @return
     */
    public static boolean isDate(String date) {

        if (!dateStr.matcher(date).matches()) {
            return false;
        }
        String[] datePart = date.split("[-/]");
        if (datePart.length != 3) {
            return false;
        }

        int dateYear = 0, month = 0, day = 0;
        String dateMonth = "";
        String dateDay = "";
        dateYear = Integer.parseInt(datePart[0]);
        dateMonth = datePart[1];
        if (dateMonth.substring(0, 1).equals("0")) {
            month = Integer.parseInt(dateMonth.substring(1));
        } else {
            month = Integer.parseInt(dateMonth);
        }

        dateDay = datePart[2];
        if (dateDay.substring(0, 1) == "0") {
            day = Integer.parseInt(dateDay.substring(1));
        } else {
            day = Integer.parseInt(dateDay);
        }

        if (dateYear > 2099 || dateYear < 1900) {
            return false;
        }
        if (month > 12 || month < 1) {
            return false;
        }
        if (day > 31 || day < 1) {
            return false;
        }
        int[] arrayDays = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (dateYear % 4 == 0) {
            arrayDays[1] = 29;
        }
        if (dateYear % 1000 == 0 && (dateYear & 4000) != 0) {
            arrayDays[1] = 28;
        }
        if (day > arrayDays[month - 1]) {
            return false;
        }
        return true;
    }

    /**
     * 校验是否有效的身份证号码
     *
     * @param idno
     * @return
     */
    public static boolean isValidIdno(String idno) {

        if (idno.length() != 18) {
            DLog.e("=============长度不对");
            return false;
        }

        if (!idno18.matcher(idno).matches()) {
            DLog.e("=============格式不对");
            return false;
        }
        String provinceCode
                = "11,12,13,14,15,21,22,23,31,32,33,34,35,36,37,41,42,43,44,45,46,50,51,52,53,54,61,62,63,64,65,71,81,82,91,";
        if (provinceCode.indexOf(idno.substring(0, 2) + ',') < 0) {
            DLog.e("=============省份不对");
            return false;
        }

        //生日是否为合法日期检查
        String birthday = "";
        if (idno.length() == 15) {
            birthday = "19" + idno.substring(6, 8) + "-" + idno.substring(8, 10) + "-" + idno.substring(10, 12);
        } else {
            birthday = idno.substring(6, 10) + "-" + idno.substring(10, 12) + "-" + idno.substring(12, 14);
        }
        if (!isDate(birthday)) {
            DLog.e("=============生日不对");
            return false;
        }

        //校验码检查
        if (idno.length() == 18) {
            if (!idno.substring(17, 18).equalsIgnoreCase(countCheckCode(idno))) {
                DLog.e("=============校验码不对");
                return false;
            }
        }

        return true;

    }
    /**
     * 检查申购信息录入是否正确.
     * @param map
     * @return
     */
    public static List<String> checkApply(Map<String, String> map) {
        List<String> messages = new ArrayList();

        if (map.get("family_type").equals("42")) {// 非本市户籍家庭（连续5年（含）以上在本市缴纳个人所得税）
            if (checkTrc(map, messages)) {
                return messages;
            }
            if (!checkContact(map, messages)) {
                return messages;
            }
        } else if (map.get("family_type").equals("41")) {//非本市户籍家庭（连续5年（含）以上在本市缴纳社会保险）
            if (checkTrc(map, messages)) {
                return messages;
            }
            if (!checkContact(map, messages)) {
                return messages;
            }

        } else if (map.get("family_type").equals("6")) {//外籍家庭

            if (!checkContact(map, messages)) {
                return messages;
            }
        } else if (map.get("family_type").equals("5")) {// 港澳台家庭
            if (!checkContact(map, messages)) {
                return messages;
            }

        } else if (map.get("family_type").equals("3")) {//持有效《北京市工作居住证》家庭

            if (!checkWrc(map, messages)) {
                return messages;
            }
            if (!checkContact(map, messages)) {
                return messages;
            }

        } else if (map.get("family_type").equals("2")) {//驻京部队现役军人和现役武警家庭
            if (!checkContact(map, messages)) {
                return messages;
            }

        } else if (map.get("family_type").equals("1")) {//本市户籍居民家庭
            if (!checkContact(map, messages)) {
                return messages;
            }

        } else if (map.get("family_type").equals("0")) {//住保
            if (!checkZhubaoYaoHao(map, messages, 0)) {
                return messages;
            }
            if (!checkContact(map, messages)) {
                return messages;
            }

        } else if (map.get("family_type").equals("-1")) {
            if (!checkZhubaoYaoHao(map, messages, -1)) {
                return messages;
            }

        }
        //----开始检查人员信息部分
        int family_type = Integer.parseInt(map.get("family_type"));
        if (family_type > 0) {
            if (isBlankStr(map.get("family_member_count"))) {
                messages.add("请选择家庭人口数！");
                return messages;
            }
            int family_member_count = Integer.parseInt(map.get("family_member_count"));
            if (family_member_count < 1 || family_member_count > 9) {
                messages.add("请选择家庭人口数！(大于1小于9)");
                return messages;
            }
            //循环处理人的关系
            int buyer_count = 0;
            for (int i = 1; i <= family_member_count; i++) {
                //
                if (isBlankStr(map.get("people_name_" + i))) {
                    messages.add("请录入姓名！");
                    break;
                }
                if (isBlankStr(map.get("id_type_" + i))) {
                    messages.add("请选择证件类型！");
                    break;
                }
                if (map.get("id_type_" + i).equals("1") || map.get("id_type_" + i).equals("31")) {
                    if (!isValidIdno(map.get("id_no_" + i))) {
                        messages.add("请录入有效的身份证号码！");
                        break;
                    }

                } else {
                    if (isBlankStr(map.get("id_no_" + i))) {
                        messages.add("请录入有效的证件号码！");
                        break;
                    }
                }

                if (isBlankStr(map.get("residence_" + i))) {
                    messages.add("请选择户籍！");
                    break;
                }
                if (map.get("residence_" + i).equals("34")) {

                    if (isBlankStr(map.get("nationality_" + i))) {
                        messages.add("请录入国籍！");
                        break;
                    }

                }

                if (i == 1) {
                    if (isBlankStr(map.get("marriage_type_" + i))) {
                        messages.add("请选择婚姻状况！");
                        break;
                    }
                }
                if (i == 1 && family_type == 2) {
                    if (isBlankStr(map.get("id_type_cop_" + i))) {
                        messages.add("请选择军官（警官）证！");
                        break;
                    }
                    if (isBlankStr(map.get("id_no_cop_" + i))) {
                        messages.add("请录入军官（警官）证号码！");
                        break;
                    }
                }
                if (i != 1) {
                    if (isBlankStr(map.get("relation_ship_" + i))) {
                        messages.add("请选择与申请人关系！");
                        break;
                    }

                }

            }

        }
        return messages;
    }

    private static boolean checkTrc(Map<String, String> map, List<String> messages) {
        if (isBlankStr(map.get("trc_cert_no"))) {
            messages.add("请录入暂住住证编号！");
            return false;
        }
        if (!checkTrcYear(map, messages, "trc_start_year", 1)) {

            return false;
        }
        if(!checkTrcMonth(map, messages, "trc_start_month", 1)){
            return false;
        }
        if (!checkTrcYear(map, messages, "trc_end_year", 2)) {

            return false;
        }
        if(!checkTrcMonth(map, messages, "trc_end_month", 2)){
            return false;
        }
        return true;

    }

    private static boolean checkTrcYear(Map<String, String> map, List<String> messages, String year, int type) throws NumberFormatException {
        if (isBlankStr(map.get(year))) {
            messages.add(type == 1 ? "请录入暂住证有效期限－起始日期－年！" : "请录入暂住证有效期限－截止日期－年！");
            return false;
        }
        int wrc_start_year = Integer.parseInt(map.get(year));
        if (wrc_start_year < 2000 || wrc_start_year > 2020) {
            messages.add(type == 1 ? "录入有效暂住证有效期限－起始日期－年(2000至2020)！" : "录入有效暂住证有效期限－截止日期－年(2000至2020)！");
            return false;
        }
        return true;
    }

    private static boolean checkTrcMonth(Map<String, String> map, List<String> messages, String month, int type) throws NumberFormatException {
        if (isBlankStr(map.get(month))) {
            messages.add(type == 1 ? "暂住证有效期限－起始日期－月" : "暂住证有效期限－截止日期－月！");
            return false;
        }
        int wrc_start_year = Integer.parseInt(map.get(month));
        if (wrc_start_year < 1 || wrc_start_year > 12) {
            messages.add(type == 1 ? "请录入有效暂住证有效期限－起始日期－月(1至12)！"
                    : "请录入有效暂住证有效期限－截止日期－月(1至12)！");
            return false;
        }
        return true;
    }

    private static boolean checkWrc(Map<String, String> map, List<String> messages) {
        if (isBlankStr(map.get("wrc_cert_no"))) {
            messages.add("请录入工作居住证编号！");
            return false;
        }
        if (!checkWrcYear(map, messages, "wrc_start_year", 1)) {
            return false;
        }
        if (!checkWrcMonth(map, messages, "wrc_start_month", 1)) {
            return false;
        }
        if (checkWrcYear(map, messages, "wrc_end_year", 2)) {
            return false;
        }
        if (!checkWrcMonth(map, messages, "wrc_end_month", 2)) {
            return false;
        }
        if (isBlankStr(map.get("wrc_type"))) {
            messages.add("请选择工作居住证类型！");
            return false;
        }
        if (isBlankStr(map.get("wrc_id_type"))) {
            messages.add("请选择办理使用证件类型！");
            return false;
        }

        if (isBlankStr(map.get("wrc_id_no"))) {
            messages.add("请录入办理使用证件号码！");
            return false;
        }

        if (map.get("wrc_id_type").equals("1") && isValidIdno(map.get("wrc_id_no"))) {
            messages.add("请录入办理使用证件号码！");
            return false;
        }
        return true;

    }

    private static boolean checkWrcYear(Map<String, String> map, List<String> messages, String year, int type) throws NumberFormatException {
        if (isBlankStr(map.get(year))) {
            messages.add(type == 1 ? "请录入工作居住证有效期限－起始日期－年！" : "请录入工作居住证有效期限－截止日期－年！");
            return false;
        }
        int wrc_start_year = Integer.parseInt(map.get(year));
        if (wrc_start_year < 2000 || wrc_start_year > 2020) {
            messages.add(type == 1 ? "录入有效工作居住证有效期限－起始日期－年(2000至2020)！" : "录入有效工作居住证有效期限－截止日期－年(2000至2020)！");
            return false;
        }
        return true;
    }

    private static boolean checkWrcMonth(Map<String, String> map, List<String> messages, String month, int type) throws NumberFormatException {
        if (isBlankStr(map.get(month))) {
            messages.add(type == 1 ? "工作居住证有效期限－起始日期－月" : "工作居住证有效期限－截止日期－月！");
            return false;
        }
        int wrc_start_year = Integer.parseInt(map.get(month));
        if (wrc_start_year < 1 || wrc_start_year > 12) {
            messages.add(type == 1 ? "请录入有效工作居住证有效期限－起始日期－月(1至12)！"
                    : "请录入有效工作居住证有效期限－截止日期－月(1至12)！");
            return false;
        }
        return true;
    }

    private static boolean checkContact(Map<String, String> map, List<String> messages) {

        if (isBlankStr(map.get("contact_people"))) {
            messages.add("请录入联系人！");
            return false;
        }
        if (!isChinaStr(map.get("contact_people"))) {
            messages.add("请录入联系人中文姓名(英文音译可以用单个.隔开姓名)！");
            return false;
        }
        if (isBlankStr(map.get("contact_telephone"))) {
            messages.add("请录入联系人手机号码！");
            return false;
        }
        if (!isMobile(map.get("contact_telephone"))) {
            messages.add("请录入正确联系人手机号码！");
            return false;
        }
        if (isBlankStr(map.get("contact_address"))) {
            messages.add("请录入联系人通讯地址！");
            return false;
        }

        if (isBlankStr(map.get("contact_zip"))) {
            messages.add("请录入联系人通讯邮编！");
            return false;
        }
        if (isZip(map.get("contact_zip"))) {
            messages.add("请录入正确联系人通讯邮编！");
            return false;
        }
        return true;
    }

    private static boolean checkZhubaoYaoHao(Map<String, String> map, List<String> messages, int type) {
        //摇号
        if (isBlankStr(map.get("guarantee_family_no"))) {
            messages.add(type == -1 ? "请录入摇号编号！" : "保障房家庭备案编号");
            return false;
        }
        if (isBlankStr(map.get("guarantee_applier_name"))) {
            messages.add(type == -1 ? "请录入申请人！" : "保障房家庭申请人");
            return false;
        }
        if (!isChinaStr(map.get("guarantee_applier_name"))) {
            messages.add(type == -1 ? "请录入申请人中文姓名(英文音译可以用单个.隔开姓名)！"
                    : "请录入保障房家庭申请人中文姓名(英文音译可以用单个.隔开姓名)！");
            return false;
        }
        if (isBlankStr(map.get("guarantee_applier_id_type"))) {
            messages.add(type == -1 ? "请选择申请人证件名称！" : "保障房家庭申请人证件名称");
            return false;
        }
        if (isBlankStr(map.get("guarantee_applier_id_no"))) {
            messages.add(type == -1 ? "申请人证件号码！" : "保障房家庭申请人证件号码");
            return false;
        }
        if (map.get("guarantee_applier_id_type").equals("1")
                && !isValidIdno(map.get("guarantee_applier_id_no"))) {
            messages.add(type == -1 ? "申请人证件号码！" : "申请人证件号码！");
            return false;
        }
        return true;
    }

    public static boolean isBlankStr(String value) {
        if (value == null) {
            return true;
        }
        if (value.trim().equals("")) {
            return true;
        }
        return false;

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        System.out.println(AndroidValidation.isChinaStr("xxxxx"));
//        System.out.println(AndroidValidation.isChinaStr("中国"));
//        System.out.println(AndroidValidation.isMailStr("t@163.com"));
//        System.out.println(AndroidValidation.isMailStr("ttt"));
//        System.out.println(AndroidValidation.isUserStr("yyyyyy"));
//        System.out.println(AndroidValidation.isUserStr("xx."));
        //System.out.println( Validation.isValidIdno("372923199001010072"));


    }
 // 返回单位是米
 	private static final double EARTH_RADIUS = 6378137.0;
 	public static String getDistance(double longitude1, double latitude1,
 			double longitude2, double latitude2) {
 		double Lat1 = rad(latitude1);
 		double Lat2 = rad(latitude2);
 		double a = Lat1 - Lat2;
 		double b = rad(longitude1) - rad(longitude2);
 		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
 				+ Math.cos(Lat1) * Math.cos(Lat2)
 				* Math.pow(Math.sin(b / 2), 2)));
 		s = s * EARTH_RADIUS;
 		if(s*10000/10000/1000<1){
 			s = Math.round(s * 10000) / 10000;
 			return s+"米";
 		}else{
 			s = Math.round(s * 10000) / 10000/1000;
 			return s+"千米";
 		}

 	}
 	public static String getDistance(int distance){
 		double s = distance /1000;
 		if(s < 1){
 			return s + "米";
 		}else{
 			return s + "千米";
 		}
 	}

 	private static double rad(double d) {
 		return d * Math.PI / 180.0;
 	}

}
