package com.rainbowcard.client.utils;

import android.content.Context;
import android.graphics.Paint;
import android.view.WindowManager;

public class DensityUtil {
    private static WindowManager mWindowManager;
    /** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }

    public static int getTextWidth(Paint paint, String str)
    {
        int iRet = 0;
        if ((str != null) && (str.length() > 0)) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int i = 0; i < len; i++) {
                iRet += (int)Math.ceil(widths[i]);
            }
        }
        return iRet;
    }

    public static int getScreenWidth(Context context) {
        mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
        return screenWidth;
    }

}
