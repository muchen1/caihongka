package com.rainbowcard.client.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.MarkerInfoModel;
import com.rainbowcard.client.ui.adapter.RecommendListAdapter;
import com.rainbowcard.client.ui.adapter.SearchKeywordListAdapter;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.LoadingFrameLayout;
import com.rainbowcard.client.widget.ScrollToFooterLoadMoreListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class KeyWordsAct extends MyBaseActivity implements OnGetPoiSearchResultListener{

    private PoiSearch mPoiSearch = null;
    private int loadIndex = 0;

    private Toolbar mToolbar;
    SearchView mSearchView;

    @InjectView(R.id.v_frame)
    LoadingFrameLayout mLoadingFrameLayout;
    @InjectView(R.id.v_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.search_listview)
    ScrollToFooterLoadMoreListView mSearchListView;
    @InjectView(R.id.back_layout)
    RelativeLayout backLayout;

    SearchKeywordListAdapter adapter;


    private String mKeyword;
    private String cityName = "北京";

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.keywords_act);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(KeyWordsAct.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(KeyWordsAct.this,true);
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        initView();
	}

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.ab_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setFocusable(true);
        mSearchView.setIconified(false);//设置
//        mSearchView.onActionViewExpanded();
//        mSearchView.setIconified(false);
        mSearchView.setFocusableInTouchMode(true);
//        mSearchView.clearFocus();//清除焦点
        mSearchView.setQueryHint(Html.fromHtml("<font color = #666666>" + getResources().getString(R.string.search_input) + "</font>"));
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {
                return true;
            }
        });
        mSearchView.setQuery(mKeyword,false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    mLoadingFrameLayout.showError("请输入要搜索的地名",false);

                } else {
                    adapter.clear();
                    adapter.setKeyword(newText);
                    mLoadingFrameLayout.showLoading();
                    mKeyword = newText;
                    mPoiSearch.searchInCity((new PoiCitySearchOption())
                            .city(cityName).keyword(mKeyword).pageNum(loadIndex));
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void goToNextPage() {
        loadIndex++;
        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city(cityName).keyword(mKeyword).pageNum(loadIndex));
    }

    void initView(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
//        mToolbar.setNavigationIcon(R.drawable.nav_back);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadMore(true);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.app_blue, R.color.app_blue, R.color.app_blue, R.color.app_blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSearchView.clearFocus();
                loadIndex = 0;
                if(!TextUtils.isEmpty(mKeyword)) {
                    mPoiSearch.searchInCity((new PoiCitySearchOption())
                            .city(cityName).keyword(mKeyword).pageNum(loadIndex));
                }
            }
        });
        adapter = new SearchKeywordListAdapter(this);
        mSearchListView.setAdapter(adapter);
        mSearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.KEY_POIINFO, adapter.getKeywords().get(position));
                    setResult(Constants.REQUEST_MAIN, intent);
                    finish();
            }
        });


    }

    private void loadMore(boolean toLoad) {
        if (toLoad) {
            mSearchListView.setOnScrollToRefreshListener(new ScrollToFooterLoadMoreListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                    mSearchView.clearFocus();
                    goToNextPage();
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

    @Override
    protected void onDestroy() {
        mPoiSearch.destroy();
        super.onDestroy();
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        mSwipeRefreshLayout.setRefreshing(false);
        mLoadingFrameLayout.showLoadingView(false);
        if(poiResult != null) {
            /*ArrayList<PoiInfo> poiAddrInfos = new ArrayList<PoiInfo>();
                for (PoiInfo poiInfo : poiResult.getAllPoi()) {
                    Log.d("GCCCCCC", poiInfo.name + "????" + poiInfo.address + "!!!!" + poiInfo.location);
                    LatLng ll = poiInfo.location;
                    Log.d("GCCCCCCaaaaaaaaaa", ll.latitude + "????aaaa" + ll.longitude);
                }*/
            if(poiResult.getAllPoi() != null) {
                if (loadIndex == 0) {
                    adapter.setConsumptions(poiResult.getAllPoi());
                } else {
                    adapter.addConsumptions(poiResult.getAllPoi());
                }
                if(poiResult.getAllPoi().size() < 10){
                    loadMore(false);
                }else {
                    loadMore(true);
                }
            }else {
                mLoadingFrameLayout.showError("你搜索的地方在火星吧~~~",false);
            }
        }

        if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(KeyWordsAct.this, "未找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (poiResult.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            String strInfo = "在";
            for (CityInfo cityInfo : poiResult.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }
            strInfo += "找到结果";
            Toast.makeText(KeyWordsAct.this, strInfo, Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
        if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(KeyWordsAct.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(KeyWordsAct.this, poiDetailResult.getName() + ": " + poiDetailResult.getAddress(), Toast.LENGTH_SHORT)
                    .show();
        }

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

}
