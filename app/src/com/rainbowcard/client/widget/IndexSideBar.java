package com.rainbowcard.client.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.rainbowcard.client.R;
import com.rainbowcard.client.base.YHApplication;


/**
 * Created by gc on 17-3-14.
 */
public class IndexSideBar extends View {

    public static final String[] IndexChar = {YHApplication.instance().getString(R.string.present),YHApplication.instance().getString(R.string.hot),"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
            "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private int mViewHeight;
    private int mViewWidth;

    private Paint mIndexTextPaint;
    private Paint mSideIndexBgPaint;

    private Boolean mIsPressed = false;
    private int mCurrentPos = 0;

    private OnIndexListener mListener;

    public IndexSideBar(Context context) {
        super(context);
        init();
    }

    public IndexSideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IndexSideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setOnIndexListener(OnIndexListener listener) {
        mListener = listener;
    }

    private void init() {
        mIndexTextPaint = new Paint();
        mIndexTextPaint.setColor(getResources().getColor(R.color.app_black));
        mIndexTextPaint.setAntiAlias(true);
        mIndexTextPaint.setTextSize(20);
        mIndexTextPaint.setTextAlign(Paint.Align.CENTER);

        mSideIndexBgPaint = new Paint();
        mSideIndexBgPaint.setColor(getResources().getColor(R.color.side_index_bg));
        mSideIndexBgPaint.setStyle(Paint.Style.FILL);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);

        int minh = getPaddingTop() + getPaddingBottom() + getSuggestedMinimumHeight();
        int h = resolveSizeAndState(minh, heightMeasureSpec, 1);

        if (mViewHeight != h || mViewWidth != w) {
            mViewHeight = h;
            mViewWidth = w;
            invalidate();
        }

        setMeasuredDimension(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawSideIndex(canvas);
        drawSideBackground(canvas);
    }

    private void drawSideIndex(Canvas canvas) {
        int height = mViewHeight / IndexChar.length;
        mIndexTextPaint.setTextSize(height / 2);

        for (int i = 0; i < IndexChar.length; i++) {
            canvas.drawText(IndexChar[i], mViewWidth / 2, (float) (height * (i + 0.6)), mIndexTextPaint);
        }
    }

    private void drawSideBackground(Canvas canvas) {
        if (mIsPressed) {
            canvas.drawRect(0, 0, mViewWidth, mViewHeight, mSideIndexBgPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsPressed = true;
                invalidate();

                caculateTouchPos(y);

                if (mListener != null) {
                    mListener.onTouchDown();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                caculateTouchPos(y);
                break;
            case MotionEvent.ACTION_UP:
                mIsPressed = false;
                invalidate();

                if (mListener != null) {
                    mListener.onTouchUp();
                }

                break;
            default:
        }
        return true;
    }

    private void caculateTouchPos(float y) {
        int height = mViewHeight / IndexChar.length;
        int pos = (int) (y / height);
        pos = pos >= IndexChar.length ? IndexChar.length - 1 : pos ;
        pos = pos < 0 ? 0 : pos;
        if (mCurrentPos != pos) {
            mCurrentPos = pos;
            if (mListener != null) {
                mListener.onSelect(pos, IndexChar[pos]);
            }
        }
    }

    public static interface OnIndexListener {
        public void onSelect(int pos, String index);

        public void onTouchDown();

        public void onTouchUp();
    }
}
