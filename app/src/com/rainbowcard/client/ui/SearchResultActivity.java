package com.rainbowcard.client.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.SearchResultModel;
import com.rainbowcard.client.ui.adapter.IllegalEntityAdapter;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.widget.LoadingFrameLayout;
import com.rainbowcard.client.widget.ScrollToFooterLoadMoreListView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017-1-6.
 */
public class SearchResultActivity extends MyBaseActivity{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.right_layout)
    LinearLayout rightLayout;
    @InjectView(R.id.right_image)
    ImageView rightIv;
    @InjectView(R.id.v_frame)
    LoadingFrameLayout mFlLoading;
    @InjectView(R.id.v_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.lv_snatch)
    ScrollToFooterLoadMoreListView mResultLv;
    @InjectView(R.id.plate_number)
    TextView plateNumber;
    @InjectView(R.id.illegal_info)
    TextView illegalInfo;

    @InjectView(R.id.succeed_layout)
    RelativeLayout succeedLayout;
    @InjectView(R.id.succeed_text)
    TextView succeedTv;
    @InjectView(R.id.plate_text)
    TextView plateTv;

    IllegalEntityAdapter adapter;
    String sign;

    String shareTitle;
    private String shareUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(SearchResultActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(SearchResultActivity.this,true);
        sign = getIntent().getStringExtra(Constants.KEY_SIGN);
        initView();
        getSearchResult();
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.illegal_result));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightLayout.setVisibility(View.VISIBLE);
        rightIv.setVisibility(View.VISIBLE);
        rightIv.setImageResource(R.drawable.nav_share);
        rightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.toast("去分享");
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.app_blue, R.color.app_blue, R.color.app_blue, R.color.app_blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSearchResult();
            }
        });
        mFlLoading.setRetryButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSearchResult();
            }
        });

        loadMore(false);
        adapter = new IllegalEntityAdapter(this);
        mResultLv.setAdapter(adapter);
        mResultLv.refreshComplete();
        mResultLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void loadMore(boolean toLoad){
        if(toLoad){
            mResultLv.setOnScrollToRefreshListener(new ScrollToFooterLoadMoreListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                    getSearchResult();
                }
            });
        }else {
            mResultLv.setOnScrollToRefreshListener(new ScrollToFooterLoadMoreListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                    mResultLv.refreshComplete();
                }
            });
        }
    }

    //获取查询结果
    void getSearchResult(){
        withBtwVolley().load(API.API_GET_SEARCH_RESULT + sign)
                .method(Request.Method.GET)
                .setHeader("Accept", API.VERSION)
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(20000)
                .setResponseHandler(new BtwVolley.ResponseHandler<SearchResultModel>() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {
                        mFlLoading.showLoadingView(false);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(SearchResultModel resp) {
                        if(resp.data.list == null || resp.data.list.isEmpty()){
                            succeedLayout.setVisibility(View.VISIBLE);
                            succeedTv.setText(String.format(getString(R.string.succeed_info),resp.data.cityName));
                            plateTv.setText(resp.data.hphm);
                            shareTitle = getString(R.string.no_regulations);
                        }else {
                            succeedLayout.setVisibility(View.GONE);
                            plateNumber.setText(resp.data.hphm);
                            illegalInfo.setText(String.format(getString(R.string.illegal_info),resp.data.cityName,resp.data.count,resp.data.fen,resp.data.money));
                            adapter.setmIllegalEntitys(resp.data.list);
                            shareTitle = String.format(getString(R.string.regulations),resp.data.money);
                        }
                        shareUrl = resp.data.url;
                    }

                    @Override
                    public void onBtwError(BtwRespError<SearchResultModel> error) {
                        UIUtils.toast(error.errorMessage);
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        UIUtils.toast(getString(R.string.network_error));
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(SearchResultModel.class);
    }

}
