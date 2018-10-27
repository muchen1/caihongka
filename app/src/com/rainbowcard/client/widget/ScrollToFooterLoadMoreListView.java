package com.rainbowcard.client.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.rainbowcard.client.R;


/**
 * Created by gc on 16-4-15.
 */
public class ScrollToFooterLoadMoreListView extends ListView {

    private View mFooterView;
    private int mLastItemIndex;
    private OnScrollListener mCustomOnScrollListener;
    private OnScrollToRefreshListener mListener;
    private ProgressBar mPbLoading;


    public ScrollToFooterLoadMoreListView(Context context) {
        super(context);
        init();
    }

    public ScrollToFooterLoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollToFooterLoadMoreListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void refreshComplete() {
        mFooterView.setVisibility(INVISIBLE);
    }

    public void setOnScrollToRefreshListener(OnScrollToRefreshListener listener) {
        mListener = listener;
    }

    @Override
    public void addFooterView(View v) {
        super.addFooterView(v);
        mFooterView = v;
    }

    private void init() {
        super.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
                        && mLastItemIndex == getAdapter().getCount() - 1) {
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
                mLastItemIndex = i + i1 -1;
                if (mCustomOnScrollListener != null) {
                    mCustomOnScrollListener.onScroll(absListView, i, i1, i2);
                }
            }
        });

        mFooterView = LayoutInflater.from(getContext()).inflate(R.layout.footer_load_more, this, false);
        mPbLoading = (ProgressBar) mFooterView.findViewById(R.id.pb_loading);
        mFooterView.setVisibility(GONE);
        addFooterView(mFooterView);
    }


    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mCustomOnScrollListener = l;
    }

    public static interface OnScrollToRefreshListener {
        public void onScrollToFooter();
    }
}
