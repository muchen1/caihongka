package com.rainbowcard.client.utils.alipay;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gc on 14-8-4.
 */
public class AlipayUtils {

    public static int getResultStatusFromAlipayResultString(String str) {

        String[] strRets = str.split(";");
        int resultStatus = Integer.MAX_VALUE;
        if (strRets.length > 0) {
            for (String s : strRets) {
                if (s.contains("resultStatus=")) {
                    Pattern pattern = Pattern.compile("\\d+");
                    Matcher matcher = pattern.matcher(s);
                    if (matcher.find()) {
                        resultStatus = Integer.valueOf(matcher.group());
                    }
                    break;
                }
            }
        }

        return resultStatus;
    }
}
