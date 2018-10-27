package com.rainbowcard.client.common.exvolley.utils;


import com.rainbowcard.client.utils.StringUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;

/**
 * Created by gc on 13-9-10.
 */
public class UrlUtils {
    public static HashMap<String, String> UrlParse(String url) {

        String[] urlParams = url.split("[?]");
        String[] params = urlParams[1].split("&");
        HashMap<String, String> map = new HashMap<String, String>(params.length + 1);
        map.put("url", urlParams[0]);

        for (String param : params) {
            String[] vk = param.split("=");
            map.put(vk[0], vk[1]);
        }

        return map;
    }

    public static String UrlBuilder(String baseUrl, SortedMap<Object, Object> params) {
        StringBuilder url = new StringBuilder();
        url.append(baseUrl);
        if (params != null  && ! params.isEmpty()) {
            if (baseUrl.contains("?")) {
                url.append("&");
            } else {
                url.append("?");
            }

//            params.put("paramSign", StringUtil.createParameterSign(params));
        }

        Iterator iter = params.entrySet().iterator();
        Boolean hasNext = iter.hasNext();
        while(hasNext) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();

            if (key == null || val == null) {
                continue;
            }
            url.append(key);
            url.append("=");
            url.append(val);

            hasNext = iter.hasNext();
            if (hasNext) {
                url.append("&");
            }
        }

        return url.toString();
    }

}

