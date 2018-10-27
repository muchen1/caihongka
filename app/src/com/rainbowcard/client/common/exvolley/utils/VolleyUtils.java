package com.rainbowcard.client.common.exvolley.utils;


import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;

/**
 * Created by gc on 14-1-8.
 */
public class VolleyUtils {

    public static String parseResponse(NetworkResponse response) {
        try {
            return new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * read VolleyError in a simple way
     * @param error
     * @return
     */
    public static NetworkError parseError(VolleyError error) {
        NetworkResponse response = error.networkResponse;
        NetworkError networkError = new NetworkError();

        if (response != null) {
            networkError.code = response.statusCode;
            networkError.message = parseResponse(response);
        } else {
            networkError.code = 0;
            networkError.message = "网络连接错误";
        }

        return networkError;
    }


    public static class NetworkError {
        /**
         * status code
         */
        public int code;

        /**
         * response data
         */
        public String message;
    }
}
