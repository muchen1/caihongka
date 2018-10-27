package com.rainbowcard.client.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import anet.channel.util.StringUtils;

/**
 * Created by gc on 2016-5-20.
 */
public class StringUtil {

    private static final Character[] CN_NUMERIC = { '一', '二', '三', '四', '五',
            '六', '七', '八', '九', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖',
            '十', '百', '千', '拾', '佰', '仟', '万', '亿', '○', 'Ｏ', '零' };

    private static Map<Character, Integer> cnNumeric = null;

    static {
        cnNumeric = new HashMap<Character, Integer>(40, 0.85f);
        for (int j = 0; j < 9; j++)
            cnNumeric.put(CN_NUMERIC[j], j + 1);
        for (int j = 9; j < 18; j++)
            cnNumeric.put(CN_NUMERIC[j], j - 8);
        cnNumeric.put('两', 2);
        cnNumeric.put('十', 10);
        cnNumeric.put('拾', 10);
        cnNumeric.put('百', 100);
        cnNumeric.put('佰', 100);
        cnNumeric.put('千', 1000);
        cnNumeric.put('仟', 1000);
        cnNumeric.put('万', 10000);
        cnNumeric.put('亿', 100000000);
    }
    public static int isCNNumeric(char c) {
        Integer i = cnNumeric.get(c);
        if (i == null)
            return -1;
        return i.intValue();
    }
    public static int cnNumericToArabic(String cnn, boolean flag) {

        cnn = cnn.trim();
        if (cnn.length() == 1)
            return isCNNumeric(cnn.charAt(0));

        if (flag)
            cnn = cnn.replace('佰', '百').replace('仟', '千').replace('拾', '十')
                    .replace('零', ' ');
        // System.out.println(cnn);
        int yi = -1, wan = -1, qian = -1, bai = -1, shi = -1;
        int val = 0;
        yi = cnn.lastIndexOf('亿');
        if (yi > -1) {
            val += cnNumericToArabic(cnn.substring(0, yi), false) * 100000000;
            if (yi < cnn.length() - 1)
                cnn = cnn.substring(yi + 1, cnn.length());
            else
                cnn = "";

            if (cnn.length() == 1) {
                int arbic = isCNNumeric(cnn.charAt(0));
                if (arbic <= 10)
                    val += arbic * 10000000;
                cnn = "";
            }
        }

        wan = cnn.lastIndexOf('万');
        if (wan > -1) {
            val += cnNumericToArabic(cnn.substring(0, wan), false) * 10000;
            if (wan < cnn.length() - 1)
                cnn = cnn.substring(wan + 1, cnn.length());
            else
                cnn = "";
            if (cnn.length() == 1) {
                int arbic = isCNNumeric(cnn.charAt(0));
                if (arbic <= 10)
                    val += arbic * 1000;
                cnn = "";
            }
        }

        qian = cnn.lastIndexOf('千');
        if (qian > -1) {
            val += cnNumericToArabic(cnn.substring(0, qian), false) * 1000;
            if (qian < cnn.length() - 1)
                cnn = cnn.substring(qian + 1, cnn.length());
            else
                cnn = "";
            if (cnn.length() == 1) {
                int arbic = isCNNumeric(cnn.charAt(0));
                if (arbic <= 10)
                    val += arbic * 100;
                cnn = "";
            }
        }

        bai = cnn.lastIndexOf('百');
        if (bai > -1) {
            val += cnNumericToArabic(cnn.substring(0, bai), false) * 100;
            if (bai < cnn.length() - 1)
                cnn = cnn.substring(bai + 1, cnn.length());
            else
                cnn = "";
            if (cnn.length() == 1) {
                int arbic = isCNNumeric(cnn.charAt(0));
                if (arbic <= 10)
                    val += arbic * 10;
                cnn = "";
            }
        }

        shi = cnn.lastIndexOf('十');
        if (shi > -1) {
            if (shi == 0)
                val += 1 * 10;
            else
                val += cnNumericToArabic(cnn.substring(0, shi), false) * 10;
            if (shi < cnn.length() - 1)
                cnn = cnn.substring(shi + 1, cnn.length());
            else
                cnn = "";
        }

        cnn = cnn.trim();
        for (int j = 0; j < cnn.length(); j++)
            val += isCNNumeric(cnn.charAt(j))
                    * Math.pow(10, cnn.length() - j - 1);

        return val;
    }

    public static int qCNNumericToArabic(String cnn) {
        int val = 0;
        cnn = cnn.trim();
        for (int j = 0; j < cnn.length(); j++)
            val += isCNNumeric(cnn.charAt(j))
                    * Math.pow(1, cnn.length() - j - 1);
        return val;
    }

    public static void main(String[] args) {
        int val = 0;
        long s = System.nanoTime();
        val = cnNumericToArabic("三亿二千零六万七千五百六", true);
        System.out.println(val);
        val = cnNumericToArabic("一九九八", true);
        System.out.println(val);
        long e = System.nanoTime();
        System.out.format("Done[" + val + "], cost: %.5fsec\n",
                ((float) (e - s)) / 1E9);
    }

    /**
     * 微信支付签名算法sign
     * @param parameters
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String createSign(SortedMap<Object,Object> parameters){
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();//所有参与传参的参数按照accsii排序（升序）
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            Object v = entry.getValue();
            if(null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                Log.d("GCCCCCCCCCCCNNNNNN","k===="+k+"?????v===="+v);
                sb.append(k + "=" + v + "&");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(API.API_KEY);
//        String sign = getMD5(sb.toString());
        String sign = MD5Util.stringMD5(sb.toString());
        Log.d("GCCCCCCCCCMMMM","sign="+sb.toString()+"?????md5="+sign);
        return sign;
    }
    /**
     * 微信支付签名算法sign
     * @param parameters
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String createParameterSign(SortedMap<Object,Object> parameters){
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();//所有参与传参的参数按照accsii排序（升序）
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            Object v = entry.getValue();
            if(!"key".equals(k)) {
                Log.d("GCCCCCCCCCCCNNNNNN","k===="+k+"?????v===="+v);
                sb.append(k + "=" + v + "&");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        String sign = MD5Util.stringMD5(sb.toString());
        Log.d("GCCCCCCCCCMMMM","sign="+sb.toString()+"?????md5="+sign);
        return sign;
    }




    /**
     * 对字符串md5加密
     *
     * @param str
     * @return
     */
    public static String getMD5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 转义正则特殊字符 （$()*+.[]?\^{},|）
     *
     * @param keyword
     * @return
     */
    public static String escapeExprSpecialWord(String keyword) {
        if (StringUtils.isNotBlank(keyword)) {
            String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

    public static String getUrl(String url, Context context){
        if(!url.contains("http://")){
            return String.format(context.getString(R.string.img_url),url);
        }
        return url;
    }

    /**
     * 使用java正则表达式去掉多余的.与0
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s){
        if(s.indexOf(".") > 0){
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    public static String touzi_ed_values22 = "";

    /**
     * 在数字型字符串千分位加逗号
     * @param str
     * @param edtext
     * @return sb.toString()
     */
    public static String addComma(String str,EditText edtext){

        touzi_ed_values22 = edtext.getText().toString().trim().replaceAll(",","");

        boolean neg = false;
        if (str.startsWith("-")){  //处理负数
            str = str.substring(1);
            neg = true;
        }
        String tail = null;
        if (str.indexOf('.') != -1){ //处理小数点
            tail = str.substring(str.indexOf('.'));
            str = str.substring(0, str.indexOf('.'));
        }
        StringBuilder sb = new StringBuilder(str);
        sb.reverse();
        for (int i = 3; i < sb.length(); i += 4){
            sb.insert(i, ',');
        }
        sb.reverse();
        if (neg){
            sb.insert(0, '-');
        }
        if (tail != null){
            sb.append(tail);
        }
        return sb.toString();
    }

    public static boolean isOnlyPointNumber(String number) {//保留两位小数正则
        Pattern pattern = Pattern.compile("^\\d+\\.?\\d{0,2}$");
        Matcher matcher = pattern.matcher(number);
        return matcher.matches();
    }

}
