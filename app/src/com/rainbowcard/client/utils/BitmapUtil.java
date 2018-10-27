package com.rainbowcard.client.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by wangzhiguo on 15/10/27.
 */
public class BitmapUtil {
    private static Context mContext;
    private static BitmapUtil mBitmapUtil;
    public static BitmapUtil newInstance(Context context) {
        mContext = context;
        if(mBitmapUtil == null) {
            mBitmapUtil = new BitmapUtil();
        }
        return mBitmapUtil;
    }

    public Bitmap resizeBitmap(int imageId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeResource(mContext.getResources(),imageId,options);
        return bitmap;
    }
}
