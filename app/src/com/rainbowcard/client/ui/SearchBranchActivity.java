package com.rainbowcard.client.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.MarkerInfoModel;
import com.rainbowcard.client.ui.adapter.RecommendListAdapter;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.LoadingFrameLayout;
import com.rainbowcard.client.widget.ScrollToFooterLoadMoreListView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017-1-19.
 */
public class SearchBranchActivity extends MyBaseActivity{

    @InjectView(R.id.v_frame)
    LoadingFrameLayout mLoadingFrameLayout;
    @InjectView(R.id.v_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.branch_listview)
    ScrollToFooterLoadMoreListView mSearchListView;
    @InjectView(R.id.right_layout)
    LinearLayout cancelBtn;
    @InjectView(R.id.search_edit)
    EditText searchEdit;
    @InjectView(R.id.search_layout)
    RelativeLayout searchLayout;

    RecommendListAdapter branchAdapter;

    int mCurrentPage = 1;

    private String mKeyword;

    public String cityId;
    public double lat;
    public double lng;

    private Handler handler = new Handler();
    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private Runnable delayRun = new Runnable() {

        @Override
        public void run() {
            //在这里调用服务器的卡查询接口，获取数据
            mKeyword = searchEdit.getText().toString();
            mCurrentPage = 1;
            if(!TextUtils.isEmpty(mKeyword)) {
                getShoop(mCurrentPage);
            }else {
                branchAdapter.clear();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_branch);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(SearchBranchActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(SearchBranchActivity.this,true);

        cityId = getIntent().getStringExtra(Constants.KEY_CITY_ID);
        lat = getIntent().getDoubleExtra(Constants.KEY_LAT,0.0);
        lng = getIntent().getDoubleExtra(Constants.KEY_LNG,0.0);

        initView();
    }

    void initView(){
        searchLayout.setBackgroundResource(R.drawable.bg_search_branch);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(delayRun!=null){
                    //每次editText有变化的时候，则移除上次发出的延迟线程
                    handler.removeCallbacks(delayRun);
                }
                //延迟1200ms，如果不再输入字符，则执行该线程的run方法
                    handler.postDelayed(delayRun, 1200);


            }
        });

        loadMore(false);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.app_blue, R.color.app_blue, R.color.app_blue, R.color.app_blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!TextUtils.isEmpty(mKeyword)) {
                    mCurrentPage = 1;
                    getShoop(mCurrentPage);
                }
            }
        });
        branchAdapter = new RecommendListAdapter(this);
        mSearchListView.setAdapter(branchAdapter);
        mSearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchBranchActivity.this, ShopDetailActivity.class);
                intent.putExtra(Constants.KEY_SHOP_ID,branchAdapter.getBranchList().get(position).shopId);
                startActivity(intent);
            }
        });


    }
    private void loadMore(boolean toLoad) {
        if (toLoad) {
            mSearchListView.setOnScrollToRefreshListener(new ScrollToFooterLoadMoreListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                    mCurrentPage = mCurrentPage + 1;
                    getShoop(mCurrentPage);
                }
            });
        } else {
            mSearchListView.setOnScrollToRefreshListener(new ScrollToFooterLoadMoreListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                    mSearchListView.refreshComplete();
                }
            });
        }
    }

    public void getShoop(final int page){
        withBtwVolley().load(API.API_GET_SHOP_LIST)
                .setHeader("Accept", API.VERSION)
                .setParam("city_id",cityId)
                .setParam("query_name",mKeyword)
//                .setParam("c_page",page)
                .setParam("lng",lng)
                .setParam("lat",lat)
                .method(Request.Method.GET)
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(20000)
                .setResponseHandler(new BtwVolley.ResponseHandler<MarkerInfoModel>() {
                    @Override
                    public void onStart() {
                        if(page == 1) {
                            getUIUtils().loading();
                        }
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(MarkerInfoModel resp) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if(resp.data != null && !resp.data.isEmpty()) {
                            mLoadingFrameLayout.showLoadingView(false);
                            branchAdapter.setBranchList(resp.data);
                        }else {
                            mLoadingFrameLayout.showError(getString(R.string.no_data),false);
                        }

                    }

                    @Override
                    public void onBtwError(BtwRespError<MarkerInfoModel> error) {
                        UIUtils.toast(error.errorMessage);
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        UIUtils.toast(getString(R.string.network_error));
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(MarkerInfoModel.class);
    }
}
