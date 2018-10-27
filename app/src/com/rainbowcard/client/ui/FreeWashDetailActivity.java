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
public class FreeWashDetailActivity extends MyBaseActivity{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.lv_type_status)
    HorizontalListView mLvTypeStatus;
    @InjectView(R.id.v_frame)
    LoadingFrameLayout mFlLoading;
    @InjectView(R.id.v_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.lv_discount)
    ScrollToFooterLoadMoreListView mDiscountLv;

    MyTypeStatusAdapter mMyTypeStatusAdapter;
    AllTicketListAdapter allTicketListAdapter;
    FreezeTicketListAdapter freezeTicketListAdapter;

    String token;

    public int status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_wash_detail);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(FreeWashDetailActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(FreeWashDetailActivity.this,true);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(FreeWashDetailActivity.this, Constants.USERINFO, Constants.UID));
        status = getIntent().getIntExtra(Constants.MESSAGE_TYPE,0);

        allTicketListAdapter = new AllTicketListAdapter(FreeWashDetailActivity.this);
        freezeTicketListAdapter = new FreezeTicketListAdapter(FreeWashDetailActivity.this);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (status){
            case 0:
                mDiscountLv.setAdapter(allTicketListAdapter);
                getBlotter();
                break;
            case 1:
                mDiscountLv.setAdapter(freezeTicketListAdapter);
                getLock();
                break;
        }
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.free_wash_detail));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mMyTypeStatusAdapter = new MyTypeStatusAdapter(this);
        mMyTypeStatusAdapter.setMPosition(status);
        List<String> list = new ArrayList<String>();
        list.add("全部");
        list.add("冻结中");
        mMyTypeStatusAdapter.setmGetTypeList(list);
        mLvTypeStatus.setAdapter(mMyTypeStatusAdapter);
        mLvTypeStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMyTypeStatusAdapter.setMPosition(position);
                status = position;
                switch (position){
                    case 0:
                        mDiscountLv.setAdapter(allTicketListAdapter);
                        getBlotter();
                        break;
                    case 1:
                        mDiscountLv.setAdapter(freezeTicketListAdapter);
                        getLock();
                        break;
                }
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.app_blue, R.color.app_blue, R.color.app_blue, R.color.app_blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switch (status){
                    case 0:
                        mDiscountLv.setAdapter(allTicketListAdapter);
                        getBlotter();
                        break;
                    case 1:
                        mDiscountLv.setAdapter(freezeTicketListAdapter);
                        getLock();
                        break;
                }
            }
        });
        mFlLoading.setRetryButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (status){
                    case 0:
                        mDiscountLv.setAdapter(allTicketListAdapter);
                        getBlotter();
                        break;
                    case 1:
                        mDiscountLv.setAdapter(freezeTicketListAdapter);
                        getLock();
                        break;
                }
            }
        });

        mDiscountLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        loadMore(false);

    }

    private void loadMore(boolean toLoad){
        if(toLoad){
            mDiscountLv.setOnScrollToRefreshListener(new ScrollToFooterLoadMoreListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                    switch (status){
                        case 0:
                            getBlotter();
                            break;
                        case 1:
                            getLock();
                            break;
                    }
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

    //获取免费券状态
    void getBlotter(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(FreeWashDetailActivity.this, Constants.USERINFO, Constants.UID));
        Log.d("GCCCCCCCCCCC",token);
        withBtwVolley().load(API.API_GET_BLOTTER_LIST)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<TicketStateModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(TicketStateModel resp) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if(resp.data != null && !resp.data.isEmpty()) {
                            mFlLoading.showLoadingView(false);
                            allTicketListAdapter.setTicketStateEntitys(resp.data);
                        }else {
                            mFlLoading.showError(getString(R.string.no_data),false);
                        }


                    }

                    @Override
                    public void onBtwError(BtwRespError<TicketStateModel> error) {
                        Toast.makeText(FreeWashDetailActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(FreeWashDetailActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(TicketStateModel.class);
    }
    //获取冻结券状态
    void getLock(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(FreeWashDetailActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(API.API_GET_LOOK_LIST)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<TicketFreezeModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(TicketFreezeModel resp) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if(resp.data != null && !resp.data.isEmpty()) {
                            mFlLoading.showLoadingView(false);
                            freezeTicketListAdapter.setTicketFreezeEntitys(resp.data);
                        }else {
                            mFlLoading.showError(getString(R.string.no_data),false);
                        }


                    }

                    @Override
                    public void onBtwError(BtwRespError<TicketFreezeModel> error) {
                        Toast.makeText(FreeWashDetailActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(FreeWashDetailActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(TicketFreezeModel.class);
    }
}
