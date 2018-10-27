package com.rainbowcard.client.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.TicketFreezeModel;
import com.rainbowcard.client.model.TicketStateModel;
import com.rainbowcard.client.ui.adapter.AllTicketListAdapter;
import com.rainbowcard.client.ui.adapter.CapitalDetailListAdapter;
import com.rainbowcard.client.ui.adapter.FreezeTicketListAdapter;
import com.rainbowcard.client.ui.adapter.MyTypeStatusAdapter;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.widget.HorizontalListView;
import com.rainbowcard.client.widget.LoadingFrameLayout;
import com.rainbowcard.client.widget.ScrollToFooterLoadMoreListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2018-3-6.
 */
public class CapitalDetailActivity extends MyBaseActivity{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.v_frame)
    LoadingFrameLayout mFlLoading;
    @InjectView(R.id.v_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.lv_discount)
    ScrollToFooterLoadMoreListView mDiscountLv;

    CapitalDetailListAdapter capitalDetailListAdapter;
    String token;

    private int mCurrPageIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capital_detail);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(CapitalDetailActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(CapitalDetailActivity.this,true);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(CapitalDetailActivity.this, Constants.USERINFO, Constants.UID));
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBlotter(mCurrPageIndex);
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.capital_detail));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mSwipeRefreshLayout.setColorSchemeResources(R.color.app_blue, R.color.app_blue, R.color.app_blue, R.color.app_blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrPageIndex = 1;
                getBlotter(mCurrPageIndex);
            }
        });
        mFlLoading.setRetryButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBlotter(mCurrPageIndex);
            }
        });

        capitalDetailListAdapter = new CapitalDetailListAdapter(CapitalDetailActivity.this);
        mDiscountLv.setAdapter(capitalDetailListAdapter);
        mDiscountLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        loadMore(true);

    }

    private void loadMore(boolean toLoad){
        if(toLoad){
            mDiscountLv.setOnScrollToRefreshListener(new ScrollToFooterLoadMoreListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                    mCurrPageIndex = mCurrPageIndex + 1;
                    getBlotter(mCurrPageIndex);
                }
            });
        }else {
            mDiscountLv.setOnScrollToRefreshListener(new ScrollToFooterLoadMoreListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                    mDiscountLv.refreshComplete();
                }
            });
        }
    }

    //获取资金明细
    void getBlotter(final int page){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(CapitalDetailActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(API.API_GET_MONEY_BLOTTER)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("page",page)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<TicketStateModel>() {
                    @Override
                    public void onStart() {
                        if(page == 1) {
                            mFlLoading.showLoading();
                        }
                    }

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onResponse(TicketStateModel resp) {
                        mFlLoading.showLoadingView(false);
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                        mDiscountLv.refreshComplete();
                        if(resp.data != null && !resp.data.isEmpty()) {
                            if (page == 1) {
                                capitalDetailListAdapter.setTicketStateEntitys(resp.data);
                                if(resp.data.isEmpty()){
                                    mFlLoading.setVisibility(View.VISIBLE);
                                    mFlLoading.showLoadingView(true);
                                    mFlLoading.showError(getString(R.string.no_data), false);
                                }
                            } else {
                                capitalDetailListAdapter.addTicketStateEntitys(resp.data);
                            }
                            if (resp.data.size() < 10) {
                                loadMore(false);
                            }else {
                                loadMore(true);
                            }
                        }else {
                            mFlLoading.showError(getString(R.string.no_data),false);
                        }


                    }

                    @Override
                    public void onBtwError(BtwRespError<TicketStateModel> error) {
                        Toast.makeText(CapitalDetailActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(CapitalDetailActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(TicketStateModel.class);
    }
}
