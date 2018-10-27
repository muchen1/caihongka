package com.rainbowcard.client.ui;

import android.content.Intent;
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
import com.rainbowcard.client.model.MessageModel;
import com.rainbowcard.client.ui.adapter.MessageListAdapter;
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
 * Created by gc on 2017-5-4.
 */
public class MessageActivity extends MyBaseActivity{

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
    MessageListAdapter adapter;

    String token;

    public int status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(MessageActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(MessageActivity.this,true);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(MessageActivity.this, Constants.USERINFO, Constants.UID));
        status = getIntent().getIntExtra(Constants.MESSAGE_TYPE,0);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (status){
            case 0:
                getEventsNews();
                break;
            case 1:
                getSystemMessage();
                break;
        }
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.message_centre));
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
        list.add("活动消息");
        list.add("系统通知");
        mMyTypeStatusAdapter.setmGetTypeList(list);
        mLvTypeStatus.setAdapter(mMyTypeStatusAdapter);
        mLvTypeStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMyTypeStatusAdapter.setMPosition(position);
                status = position;
                switch (position){
                    case 0:
                        getEventsNews();
                        break;
                    case 1:
                        getSystemMessage();
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
                        getEventsNews();
                        break;
                    case 1:
                        getSystemMessage();
                        break;
                }
            }
        });
        mFlLoading.setRetryButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (status){
                    case 0:
                        getEventsNews();
                        break;
                    case 1:
                        getSystemMessage();
                        break;
                }
            }
        });
        adapter = new MessageListAdapter(MessageActivity.this);
        mDiscountLv.setAdapter(adapter);
        mDiscountLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(MyDiscountActivity.this,OrderDetailActivity.class);
//                intent.putExtra(Constants.KEY_SHOP_ORDER,adapter.getDiscountEntitys().get(position));
//                startActivity(intent);
                Intent intent;
                switch (Integer.valueOf(adapter.getMessageEntitys().get(position).extra.type)){
                    case 1:
                        intent = new Intent(MessageActivity.this, SurprisedActivity.class);
                        intent.putExtra(Constants.KEY_URL, adapter.getMessageEntitys().get(position).extra.url);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(MessageActivity.this,MyDiscountActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(MessageActivity.this,MessageDetailActivity.class);
                        intent.putExtra(Constants.KEY_CONTENT, adapter.getMessageEntitys().get(position).extra.content);
                        startActivity(intent);
                        break;
                }
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
                            getEventsNews();
                            break;
                        case 1:
                            getSystemMessage();
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

    //获取广播消息
    void getEventsNews(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(MessageActivity.this, Constants.USERINFO, Constants.UID));
        Log.d("GCCCCCCCCC!!!!!!!!",token);
        withBtwVolley().load(API.API_GET_MESSAGE_ALL)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION2D11)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<MessageModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(MessageModel resp) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if(resp.data != null && !resp.data.isEmpty()) {
                            mFlLoading.showLoadingView(false);
                            adapter.setMessageEntitys(resp.data);
                        }else {
                            mFlLoading.showError(getString(R.string.no_data),false);
                        }


                    }

                    @Override
                    public void onBtwError(BtwRespError<MessageModel> error) {
                        Toast.makeText(MessageActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(MessageActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(MessageModel.class);
    }
    //获取系统通知
    void getSystemMessage(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(MessageActivity.this, Constants.USERINFO, Constants.UID));
        Log.d("GCCCCCCCCC",token);
        withBtwVolley().load(API.API_GET_MESSAGE_ALONE)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION2D11)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<MessageModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(MessageModel resp) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if(resp.data != null && !resp.data.isEmpty()) {
                            mFlLoading.showLoadingView(false);
                            adapter.setMessageEntitys(resp.data);
                        }else {
                            mFlLoading.showError(getString(R.string.no_data),false);
                        }


                    }

                    @Override
                    public void onBtwError(BtwRespError<MessageModel> error) {
                        Toast.makeText(MessageActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(MessageActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(MessageModel.class);
    }
}
