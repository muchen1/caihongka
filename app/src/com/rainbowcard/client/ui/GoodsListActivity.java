package com.rainbowcard.client.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.HotGoodModel;
import com.rainbowcard.client.ui.adapter.GoodsListAdapter;
import com.rainbowcard.client.ui.adapter.RecommendListAdapter;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.widget.LoadingFrameLayout;
import com.rainbowcard.client.widget.ScrollToFooterLoadMoreListView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017-12-7.
 */
public class GoodsListActivity extends MyBaseActivity{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.v_frame)
    LoadingFrameLayout mLoadingFrameLayout;
    @InjectView(R.id.v_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.goods_listview)
    ScrollToFooterLoadMoreListView mGoodsListView;

    private String isGoods;
    GoodsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_list);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(GoodsListActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(GoodsListActivity.this,true);
        isGoods = getIntent().getStringExtra(Constants.KEY_IS_GOODS);

        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getGoodsList();
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        switch (Integer.valueOf(isGoods)){
            case 0:
                mHeadControlPanel.setMiddleTitle("全部商品");
                break;
            case 2:
                mHeadControlPanel.setMiddleTitle("虚拟商品");
                break;
            case 3:
                mHeadControlPanel.setMiddleTitle("实物商品");
                break;
        }
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mLoadingFrameLayout.setRetryButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGoodsList();
            }
        });
        loadMore(false);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.app_blue, R.color.app_blue, R.color.app_blue, R.color.app_blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getGoodsList();
            }
        });

        adapter = new GoodsListAdapter(this);
        mGoodsListView.setAdapter(adapter);
        mGoodsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(adapter.getItem(position).remnant != 0) {
                    Intent intent = new Intent(GoodsListActivity.this, GoodsDetailActivity.class);
                    intent.putExtra(Constants.KEY_GOODS_ID, adapter.getHotGoodEntitys().get(position).id);
                    startActivity(intent);
                }
            }
        });
    }

    private void loadMore(boolean toLoad) {
        if (toLoad) {
            mGoodsListView.setOnScrollToRefreshListener(new ScrollToFooterLoadMoreListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                    getGoodsList();
                }
            });
        } else {
            mGoodsListView.setOnScrollToRefreshListener(new ScrollToFooterLoadMoreListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                    mGoodsListView.refreshComplete();
                }
            });
        }
    }


    //获取兑换商品列表
    public void getGoodsList(){
        withBtwVolley().load(API.API_GET_GOODS_LIST)
                .setHeader("Accept", API.VERSION)
                .setHeader("Authorization",String.format(getString(R.string.token), MyConfig.getSharePreStr(GoodsListActivity.this, Constants.USERINFO, Constants.UID)))
                .setParam("is_goods",isGoods)
                .method(Request.Method.GET)
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(20000)
                .setResponseHandler(new BtwVolley.ResponseHandler<HotGoodModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(HotGoodModel resp) {
                        if(resp.data != null && !resp.data.isEmpty()) {
                            mLoadingFrameLayout.showLoadingView(false);
                            adapter.setHotGoodEntitys(resp.data);
                        }else {
                            mLoadingFrameLayout.showError(getString(R.string.no_data),false);
                        }
                    }

                    @Override
                    public void onBtwError(BtwRespError<HotGoodModel> error) {
//                        UIUtils.toast(error.errorMessage);
                        mLoadingFrameLayout.showError(error.errorMessage,false);
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
//                        UIUtils.toast(getString(R.string.network_error));
                        mLoadingFrameLayout.showError(getString(R.string.network_error));
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(HotGoodModel.class);
    }
}
