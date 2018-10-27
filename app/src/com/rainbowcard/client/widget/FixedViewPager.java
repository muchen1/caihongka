package com.rainbowcard.client.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.rainbowcard.client.common.utils.DLog;


/**
 * Created by gc on 14-11-6.
 * http://blog.csdn.net/nnmmbb/article/details/28419779
 */
public class FixedViewPager extends ViewPager{
    public final static String TAG = "FixedViewPager";
    public FixedViewPager(Context context) {
        super(context);
    }

    public FixedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            DLog.e(Log.getStackTraceString(ex));
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            DLog.e(Log.getStackTraceString(ex));
        }
        return false;
    }
}
