package com.rainbowcard.client.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.rainbowcard.client.R;

/**
 * Created by gc on 16-4-15.
 */
public class LoadingFrameLayout extends FrameLayout {

    private TextView mBtnRetry;
    private TextView mTvMessage;
    private ImageView mIvNoNetwork;
    private ProgressBar mPbLoading;
    private View mVLoading;

    private OnClickListener mOnClickListener;
    private Boolean mIsShowLoadingView = false;

    public LoadingFrameLayout(Context context) {
        super(context);
        init();
    }

    public LoadingFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public LoadingFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        addLoadingView();
    }


    public void showLoading() {
        showLoadingView(true);
        mPbLoading.setVisibility(VISIBLE);
        mBtnRetry.setVisibility(INVISIBLE);
        mTvMessage.setVisibility(INVISIBLE);
        mIvNoNetwork.setVisibility(INVISIBLE);
    }

    /**
     *
     * @param message
     * @param onClickListener
     */
    public void showError(String message, OnClickListener onClickListener) {
        showLoadingView(true);
        mPbLoading.setVisibility(INVISIBLE);
        mTvMessage.setVisibility(VISIBLE);
        mBtnRetry.setVisibility(VISIBLE);
        mIvNoNetwork.setVisibility(VISIBLE);

        mTvMessage.setText(message);
        if (onClickListener != null) {
            mBtnRetry.setOnClickListener(onClickListener);
        }
    }

    public void setRetryButtonClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    public void showError(String message) {
        showError(message, mOnClickListener);
    }

    public void showError(String message,boolean noClick){
        showLoadingView(true);
        mPbLoading.setVisibility(INVISIBLE);
        mTvMessage.setVisibility(VISIBLE);
        mBtnRetry.setVisibility(INVISIBLE);
        mIvNoNetwork.setVisibility(VISIBLE);
        mIvNoNetwork.setImageResource(R.drawable.network_error);

        mTvMessage.setText(message);
    }

    private void addLoadingView() {
        mVLoading = LayoutInflater.from(getContext()).inflate(R.layout.v_loding, null);
        mBtnRetry = (TextView) mVLoading.findViewById(R.id.btn_retry);
        mIvNoNetwork = (ImageView) mVLoading.findViewById(R.id.iv_nonetwork);
        mTvMessage = (TextView) mVLoading.findViewById(R.id.tv_msg);
        mPbLoading = (ProgressBar) mVLoading.findViewById(R.id.pb_loading);

        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mVLoading.setLayoutParams(lp);

        addView(mVLoading);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        showLoadingView();
    }

    private void showLoadingView() {
        showLoadingView(mIsShowLoadingView);
    }

    public void showLoadingView(Boolean shouldShowLoading) {

        if (getChildCount() > 1) {
            for (int i = 0, len = getChildCount(); i < len; i++) {
                View view = getChildAt(i);
                if (view == mVLoading) {
                    if (shouldShowLoading) {
                        view.setVisibility(VISIBLE);
                    } else {
                        view.setVisibility(INVISIBLE);
                    }
                } else {
                    if (shouldShowLoading) {
                        view.setVisibility(INVISIBLE);
                    } else {
                        view.setVisibility(VISIBLE);
                    }
                }
            }
        }

        mIsShowLoadingView = shouldShowLoading;
    }

}
