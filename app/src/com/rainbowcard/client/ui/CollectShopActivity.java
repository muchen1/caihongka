package com.rainbowcard.client.ui;

import android.content.Intent;
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
import com.rainbowcard.client.model.MarkerInfoModel;
import com.rainbowcard.client.ui.adapter.CollectListAdapter;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.widget.LoadingFrameLayout;
import com.rainbowcard.client.widget.ScrollToFooterLoadMoreListView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017-3-29
 */
public class CollectShopActivity extends MyBaseActivity{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.v_frame)
    LoadingFrameLayout mFlLoading;
    @InjectView(R.id.v_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.lv_collect)
    ScrollToFooterLoadMoreListView mCollectLv;

    CollectListAdapter adapter;

    String token;

    public int status = 0;
    private double lat;
    private double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_branch_list);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(CollectShopActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(CollectShopActivity.this,true);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(CollectShopActivity.this, Constants.USERINFO, Constants.UID));
        lat = getIntent().getDoubleExtra(Constants.KEY_LAT,0.0);
        lng = getIntent().getDoubleExtra(Constants.KEY_LNG,0.0);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCollectShop();
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.my_collect));
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
                getCollectShop();
            }
        });
        mFlLoading.setRetryButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCollectShop();
            }
        });
        adapter = new CollectListAdapter(CollectShopActivity.this);
        mCollectLv.setAdapter(adapter);
        mCollectLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CollectShopActivity.this,ShopDetailActivity.class);
                intent.putExtra(Constants.KEY_SHOP_ID,adapter.getBranchList().get(position).shopId);
                startActivity(intent);
            }
        });
        loadMore(false);
    }

    private void loadMore(boolean toLoad){
        if(toLoad){
            mCollectLv.setOnScrollToRefreshListener(new ScrollToFooterLoadMoreListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                    getCollectShop();
                }
            });
        }else {
            mCollectLv.setOnScrollToRefreshListener(new ScrollToFooterLoadMoreListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                    mCollectLv.refreshComplete();
                }
            });
        }
    }

    //获取收藏列表
    void getCollectShop(){
        withBtwVolley().load(API.API_GET_COLLECT_LIST)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION2D1)
                .setParam("lat",lat)
                .setParam("lng",lng)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<MarkerInfoModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(MarkerInfoModel resp) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if(resp.data != null && !resp.data.isEmpty()) {
                            mFlLoading.showLoadingView(false);
                            adapter.setBranchList(resp.data);
                        }else {
                            mFlLoading.showError(getString(R.string.no_data),false);
                        }
                    }

                    @Override
                    public void onBtwError(BtwRespError<MarkerInfoModel> error) {
                        Toast.makeText(CollectShopActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(CollectShopActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(MarkerInfoModel.class);
    }
}
