package com.rainbowcard.client.common.exvolley.ex;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Volley;
import com.rainbowcard.client.common.exvolley.okhttp.OkHttpStack;

/**
 * Created by gc on 14-1-6.
 */
public class ExVolley {

    private volatile static ExVolley mInstance;
    private static RequestQueue mRequestQueue;

    private ExVolley(Context context) {
//        mRequestQueue = Volley.newRequestQueue(context.getApplicationContext(), new OkHttpStack());
        mRequestQueue = Volley.newRequestQueue(context.getApplicationContext(), null); //修改于2017-08-15，是否能修改4G下网络连接异常？
    }

    public static ExRequestBuilder with(Context context) {

        if (mInstance == null) {
            synchronized (ExVolley.class) {
                if (mInstance == null) {
                    mInstance = new ExVolley(context);
                }
            }
        }

        return new ExRequestBuilder(mRequestQueue);
    }

}
