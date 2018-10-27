package com.rainbowcard.client.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
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
import com.rainbowcard.client.model.FinanceRecordModel;
import com.rainbowcard.client.ui.adapter.FinanceRecordListAdapter;
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
 * Created by gc on 2018-3-10.
 */
public class FinanceRecordActivity extends MyBaseActivity{

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
    @InjectView(R.id.lv_order)
    ScrollToFooterLoadMoreListView mOrderLv;

    MyTypeStatusAdapter mMyTypeStatusAdapter;
    FinanceRecordListAdapter adapter;

    String token;

    public int status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(FinanceRecordActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(FinanceRecordActivity.this,true);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(FinanceRecordActivity.this, Constants.USERINFO, Constants.UID));
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFinanceRecord();
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.purchase_record));
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
        mMyTypeStatusAdapter.setMPosition(0);
        List<String> list = new ArrayList<String>();
        list.add("全部");
        list.add("未到期");
        list.add("已到期");
        mMyTypeStatusAdapter.setmGetTypeList(list);
        mLvTypeStatus.setAdapter(mMyTypeStatusAdapter);
        mLvTypeStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMyTypeStatusAdapter.setMPosition(position);
                switch (position){
                    case 0:
                        status = 0;
                        break;
                    case 1:
                        status = 2;
                        break;
                    case 2:
                        status = 3;
                        break;
                }
                getFinanceRecord();
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.app_blue, R.color.app_blue, R.color.app_blue, R.color.app_blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFinanceRecord();
            }
        });
        mFlLoading.setRetryButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFinanceRecord();
            }
        });
        adapter = new FinanceRecordListAdapter(FinanceRecordActivity.this);
        mOrderLv.setAdapter(adapter);
        loadMore(false);
    }

    private void loadMore(boolean toLoad){
        if(toLoad){
            mOrderLv.setOnScrollToRefreshListener(new ScrollToFooterLoadMoreListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                    getFinanceRecord();
                }
            });
        }else {
            mOrderLv.setOnScrollToRefreshListener(new ScrollToFooterLoadMoreListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                    mOrderLv.refreshComplete();
                }
            });
        }
    }

    //获取购买记录
    void getFinanceRecord(){
        withBtwVolley().load(API.API_GET_FINANCE_RECORD_LIST)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("status",status)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<FinanceRecordModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(FinanceRecordModel resp) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if(resp.data != null && !resp.data.isEmpty()) {
                            mFlLoading.showLoadingView(false);
                            adapter.setFinanceRecordEntitys(resp.data);
                        }else {
                            mFlLoading.showError(getString(R.string.no_data),false);
                        }


                    }

                    @Override
                    public void onBtwError(BtwRespError<FinanceRecordModel> error) {
                        Toast.makeText(FinanceRecordActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(FinanceRecordActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(FinanceRecordModel.class);
    }

}
