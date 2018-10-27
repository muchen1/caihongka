package com.rainbowcard.client.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;

import com.rainbowcard.client.R;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


/**
 * Created by kleist on 14-9-22.
 */
public class LoadMoreStickyListView extends StickyListHeadersListView {
    private View mFooterView;
    private int mLastItemIndex;
    private AbsListView.OnScrollListener mCustomOnScrollListener;
    private OnScrollToRefreshListener mListener;

    public LoadMoreStickyListView(Context context) {
        super(context);
        init();
    }

    public LoadMoreStickyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadMoreStickyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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


    public static interface OnScrollToRefreshListener {
        public void onScrollToFooter();
    }
}
