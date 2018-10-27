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
import com.rainbowcard.client.model.InviterModel;
import com.rainbowcard.client.model.TicketFreezeModel;
import com.rainbowcard.client.model.TicketStateModel;
import com.rainbowcard.client.ui.adapter.AllTicketListAdapter;
import com.rainbowcard.client.ui.adapter.FreezeTicketListAdapter;
import com.rainbowcard.client.ui.adapter.InviteTicketListAdapter;
import com.rainbowcard.client.ui.adapter.InviterListAdapter;
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
 * Created by gc on 2018-3-30.
 */
public class InviteDetailActivity extends MyBaseActivity{

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
    InviteTicketListAdapter inviteTicketListAdapter;
    InviterListAdapter inviterListAdapter;

    String token;

    public int status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_wash_detail);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(InviteDetailActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(InviteDetailActivity.this,true);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(InviteDetailActivity.this, Constants.USERINFO, Constants.UID));
        status = getIntent().getIntExtra(Constants.MESSAGE_TYPE,0);

        inviteTicketListAdapter = new InviteTicketListAdapter(InviteDetailActivity.this);
        inviterListAdapter = new InviterListAdapter(InviteDetailActivity.this);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (status){
            case 1:
                mDiscountLv.setAdapter(inviteTicketListAdapter);
                getInviteTicket();
                break;
            case 0:
                mDiscountLv.setAdapter(inviterListAdapter);
                getInviter();
                break;
        }
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.invite));
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
        list.add("邀请人数");
        list.add("参与订单");
        mMyTypeStatusAdapter.setmGetTypeList(list);
        mLvTypeStatus.setAdapter(mMyTypeStatusAdapter);
        mLvTypeStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMyTypeStatusAdapter.setMPosition(position);
                status = position;
                switch (position){
                    case 1:
                        mDiscountLv.setAdapter(inviteTicketListAdapter);
                        getInviteTicket();
                        break;
                    case 0:
                        mDiscountLv.setAdapter(inviterListAdapter);
                        getInviter();
                        break;
                }
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.app_blue, R.color.app_blue, R.color.app_blue, R.color.app_blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switch (status){
                    case 1:
                        mDiscountLv.setAdapter(inviteTicketListAdapter);
                        getInviteTicket();
                        break;
                    case 0:
                        mDiscountLv.setAdapter(inviterListAdapter);
                        getInviter();
                        break;
                }
            }
        });
        mFlLoading.setRetryButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (status){
                    case 1:
                        mDiscountLv.setAdapter(inviteTicketListAdapter);
                        getInviteTicket();
                        break;
                    case 0:
                        mDiscountLv.setAdapter(inviterListAdapter);
                        getInviter();
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
                        case 1:
                            getInviteTicket();
                            break;
                        case 0:
                            getInviter();
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

    //获取从邀请朋友获得的券列表
    void getInviteTicket(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(InviteDetailActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(API.API_GET_INVITE_TICKET)
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
                            inviteTicketListAdapter.setTicketStateEntitys(resp.data);
                        }else {
                            mFlLoading.showError(getString(R.string.no_data),false);
                        }


                    }

                    @Override
                    public void onBtwError(BtwRespError<TicketStateModel> error) {
                        Toast.makeText(InviteDetailActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(InviteDetailActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(TicketStateModel.class);
    }
    //获取被邀请人列表
    void getInviter(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(InviteDetailActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(API.API_GET_INVITER_LIST)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<InviterModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(InviterModel resp) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if(resp.data != null && !resp.data.isEmpty()) {
                            mFlLoading.showLoadingView(false);
                            inviterListAdapter.setInviterEntitys(resp.data);
                        }else {
                            mFlLoading.showError(getString(R.string.no_data),false);
                        }


                    }

                    @Override
                    public void onBtwError(BtwRespError<InviterModel> error) {
                        Toast.makeText(InviteDetailActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(InviteDetailActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(InviterModel.class);
    }
}
