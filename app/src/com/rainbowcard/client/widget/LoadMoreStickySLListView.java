package com.rainbowcard.client.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;

import com.rainbowcard.client.R;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


/**
 * Created by kleist on 14-9-22.
 */
public class LoadMoreStickySLListView extends StickyListHeadersListView implements GestureDetector.OnGestureListener{
    private View mFooterView;
    private int mLastItemIndex;
    private AbsListView.OnScrollListener mCustomOnScrollListener;
    private OnScrollToRefreshListener mListener;
    private GestureDetector gestureDetector;

    public LoadMoreStickySLListView(Context context) {
        super(context);
        gestureDetector=new GestureDetector(context,this);
        init();
    }

    public LoadMoreStickySLListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gestureDetector=new GestureDetector(context,this);
        init();
    }

    public LoadMoreStickySLListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        gestureDetector=new GestureDetector(context,this);
        init();
    }

    public void refreshComplete() {
        mFooterView.setVisibility(INVISIBLE);
    }

    public void setOnScrollToRefreshListener(OnScrollToRefreshListener listener) {
        mListener = listener;
    }

    private void init() {
        super.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && mLastItemIndex == getAdapter().getCount()) {
                    mFooterView.setVisibility(VISIBLE);
                    if (mListener != null) {
                        mListener.onScrollToFooter();
                    }
                }

                if (mCustomOnScrollListener != null) {
                    mCustomOnScrollListener.onScrollStateChanged(absListView, scrollState);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                mLastItemIndex = i + i1 - getHeaderViewsCount() - getFooterViewsCount();
                if (mCustomOnScrollListener != null) {
                    mCustomOnScrollListener.onScroll(absListView, i, i1, i2);
                }
            }
        });

        mFooterView = LayoutInflater.from(getContext()).inflate(R.layout.footer_load_more, null);
        mFooterView.setVisibility(GONE);
        addFooterView(mFooterView);
    }


    @Override
    public void setOnScrollListener(AbsListView.OnScrollListener l) {
        mCustomOnScrollListener = l;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //左滑动
        if (e1.getX() - e2.getX() > 180 && Math.abs(e1.getY() - e2.getY()) < 100) {
            return true;
        }
        //右滑动
        else if (e1.getX() - e2.getX() <-180 && Math.abs(e1.getY() - e2.getY()) < 100) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    public static interface OnScrollToRefreshListener {
        public void onScrollToFooter();
    }
}
