package com.rainbowcard.client.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseFragment;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.AreaEntity;
import com.rainbowcard.client.model.AreaModel;
import com.rainbowcard.client.model.MarkerInfoModel;
import com.rainbowcard.client.ui.BranchActivity;
import com.rainbowcard.client.ui.ShopDetailActivity;
import com.rainbowcard.client.ui.adapter.BranchListAdapter;
import com.rainbowcard.client.ui.adapter.ConstellationAdapter;
import com.rainbowcard.client.ui.adapter.GirdDropDownAdapter;
import com.rainbowcard.client.ui.adapter.ListDropDownAdapter;
import com.rainbowcard.client.utils.LoginControl;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.DropDownMenu;
import com.rainbowcard.client.widget.LoadingFrameLayout;
import com.rainbowcard.client.widget.ScrollToFooterLoadMoreListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by gc on 2016-10-25.
 */
public class ListFragment extends MyBaseFragment {
    @InjectView(R.id.dropDownMenu)
    public DropDownMenu mDropDownMenu;
    @InjectView(R.id.fl_loading)
    LoadingFrameLayout mVContainer;
//    @InjectView(R.id.v_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
//    @InjectView(R.id.branch_listview)
    ScrollToFooterLoadMoreListView mBranchLv;
    View view;

    BranchListAdapter adapter;
    private int mCurrPageIndex = 1;
    BranchActivity instance = null;
    int selectPosition = -1;

    public String headers[] = {"全市", "默认", "筛选","彩虹卡"};
    public String headersrb[] = {"全市", "默认", "筛选","人保"};
    public String oheaders[] = {"全市", "默认", "筛选"};
    private List<View> popupViews = new ArrayList<>();

    private GirdDropDownAdapter cityAdapter;
    private ListDropDownAdapter ageAdapter;
    private ListDropDownAdapter userAdapter;
    private ConstellationAdapter constellationAdapter;
    ListView cityView;

//    private String citys[] = {"全市", "朝阳", "海淀", "东城", "西城", "通州"};
    private ArrayList<AreaEntity> citys = new ArrayList<AreaEntity>();
    private String types[] = {"默认", "按评价", "按销量", "按距离", "按价格"};
    private String user[] = {"彩虹卡", "人保"};
    private String keywords[] = {"不限", "易车生活", "爱义行", "长城润滑"};

    private int constellationPosition = 0;

    private String areaId = "";
    private String sortType = "1";
    private String queryName = "";
    private int isRb = 0;

    public static ListFragment newInstance(){
        ListFragment fragment = new ListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_branch_list, container, false);
        instance = (BranchActivity) getActivity();
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(MyConfig.getSharePreIntRb(getActivity(), Constants.USERINFO, Constants.KEY_IS_RB) != -1){
            isRb = MyConfig.getSharePreIntRb(getActivity(), Constants.USERINFO, Constants.KEY_IS_RB);
        }
        getArea();
        initView();
        loadBranch(mCurrPageIndex);
    }

    void initView(){
//        if(MyConfig.getSharePreInt(getActivity(), Constants.USERINFO, Constants.KEY_IS_RB) == 1){
//            headers[headers.length-1] = "人保";
//        }else {
//            headers[headers.length-1] = "彩虹卡";
//        }
        cityView = new ListView(getActivity());
        cityAdapter = new GirdDropDownAdapter(getActivity());
        cityView.setDivider(getResources().getDrawable(R.drawable.hengtiao));
        cityView.setDividerHeight(0);
        cityView.setAdapter(cityAdapter);

        //init age menu
        final ListView typeView = new ListView(getActivity());
        typeView.setDivider(getResources().getDrawable(R.drawable.divider_my_list));
        typeView.setDividerHeight(0);
        ageAdapter = new ListDropDownAdapter(getActivity(), Arrays.asList(types));
        typeView.setAdapter(ageAdapter);

        //init sex menu
        final ListView userView = new ListView(getActivity());
        userView.setDividerHeight(0);
        userAdapter = new ListDropDownAdapter(getActivity(), Arrays.asList(user));
        userView.setAdapter(userAdapter);

        //init constellation
        final View constellationView = getActivity().getLayoutInflater().inflate(R.layout.custom_layout, null);
        GridView constellation = ButterKnife.findById(constellationView, R.id.constellation);
        constellationAdapter = new ConstellationAdapter(getActivity(), Arrays.asList(keywords));
        constellation.setAdapter(constellationAdapter);
        TextView ok = ButterKnife.findById(constellationView, R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDropDownMenu.setTabText(constellationPosition == 0 ? headers[2] : keywords[constellationPosition]);
                queryName = keywords[constellationPosition];
                mDropDownMenu.closeMenu();
                mCurrPageIndex = 1;
                loadBranch(mCurrPageIndex);
            }
        });

        //init popupViews
        popupViews.clear();
        popupViews.add(cityView);
        popupViews.add(typeView);
//        popupViews.add(sexView);
        popupViews.add(constellationView);
        if(instance.serviceType == 1) {
            popupViews.add(userView);
        }

        //add item click event
        cityView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cityAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? citys.get(0).name : citys.get(position).name);
                areaId = citys.get(position).id;
                mDropDownMenu.closeMenu();
                mCurrPageIndex = 1;
                loadBranch(mCurrPageIndex);
            }
        });

        typeView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ageAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[1] : types[position]);
                sortType = String.valueOf(position+1);
                mDropDownMenu.closeMenu();
                mCurrPageIndex = 1;
                loadBranch(mCurrPageIndex);
            }
        });

        userView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                userAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[3] : user[position]);
                if(position == 0){
                    isRb = 0;
                    instance.isRb = 0;
                }else {
                    isRb = 1;
                    instance.isRb = 1;
                }
//                if(LoginControl.getInstance(getActivity()).isLogin()) {
                    MyConfig.putSharePre(getActivity(), Constants.USERINFO, Constants.KEY_IS_RB, isRb);
//                }
                mDropDownMenu.closeMenu();
                mCurrPageIndex = 1;
                loadBranch(mCurrPageIndex);
            }
        });

        constellation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                constellationAdapter.setCheckItem(position);
                constellationPosition = position;
            }
        });

        //init city menu
        mSwipeRefreshLayout = new SwipeRefreshLayout(getActivity());
        mSwipeRefreshLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mBranchLv = new ScrollToFooterLoadMoreListView(getActivity());
        mBranchLv.setDivider(getResources().getDrawable(R.drawable.divider_my_list));
        mBranchLv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mSwipeRefreshLayout.addView(mBranchLv);
        loadMore(true);


        //init dropdownview
        if(instance.serviceType == 1) {
            if(MyConfig.getSharePreInt(getActivity(), Constants.USERINFO, Constants.KEY_IS_RB) == 1){
                mDropDownMenu.setDropDownMenu(Arrays.asList(headersrb), popupViews, mSwipeRefreshLayout);
                userAdapter.setCheckItem(1);
            }else {
                mDropDownMenu.setDropDownMenu(Arrays.asList(headers), popupViews, mSwipeRefreshLayout);
            }
        }else {
            mDropDownMenu.setDropDownMenu(Arrays.asList(oheaders), popupViews, mSwipeRefreshLayout);
        }

        mSwipeRefreshLayout.setColorSchemeResources(R.color.app_blue, R.color.app_blue, R.color.app_blue, R.color.app_blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrPageIndex = 1;
                loadBranch(mCurrPageIndex);
            }
        });
        adapter = new BranchListAdapter(getActivity());
        adapter.setType(instance.serviceType);
        mBranchLv.setAdapter(adapter);
        mBranchLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ShopDetailActivity.class);
                intent.putExtra(Constants.KEY_SHOP_ID,adapter.getBranchList().get(position).shopId);
                intent.putExtra(Constants.KEY_SERVICE_TYPE,instance.serviceType);
                intent.putExtra(Constants.KEY_RB_USER,instance.isRb);
                intent.putExtra(Constants.KEY_IS_FREE,instance.isFree);
                intent.putExtra(Constants.KEY_ENTRANCE,true);
                startActivity(intent);
//                for (MarkerInfoUtil infoUtil : adapter.getBranchList()){
//                    infoUtil.isSelect = false;
//                }
//                adapter.getBranchList().get(position).isSelect = true;
//                instance.setmMapFragment();
//                selectPosition = position;
            }
        });
    }

    void initData(){
        cityAdapter.setCitys(citys);
    }


    private void loadMore(boolean toLoad){
        if(toLoad){
//            mCurrPageIndex ++;
            mBranchLv.setOnScrollToRefreshListener(new ScrollToFooterLoadMoreListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                    mCurrPageIndex = mCurrPageIndex + 1;
                    loadBranch(mCurrPageIndex);
                }
            });
        }else {
            mBranchLv.setOnScrollToRefreshListener(new ScrollToFooterLoadMoreListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                    mBranchLv.refreshComplete();
                }
            });
        }
    }

    public void getShop(int flag){
        isRb = flag;
        if(isRb == 1){
//            headers[headers.length-1] = "人保";
            userAdapter.setCheckItem(1);
//            mDropDownMenu.setTabText(user[1]);
            mDropDownMenu.setMyTabText("人保");
//            mDropDownMenu.closeMenu();
        }else {
            userAdapter.setCheckItem(0);
//            mDropDownMenu.setTabText(user[0]);
            mDropDownMenu.setMyTabText("彩虹卡");
//            mDropDownMenu.closeMenu();
        }
        mCurrPageIndex = 1;
        loadBranch(mCurrPageIndex);
    }

    private void getArea(){
        withBtwVolley().load(API.API_GET_AREA)
                .setHeader("Accept", API.VERSION)
                .setParam("city_id",instance.cityId)
                .method(Request.Method.GET)
                .setTimeout(10000)
                .setRetrys(0)
                .setUIComponent(this)
                .setResponseHandler(new BtwVolley.ResponseHandler<AreaModel>() {
                                        @Override
                                        public void onStart() {
                                        }

                                        @Override
                                        public void onFinish() {
                                        }

                                        @Override
                                        public void onResponse(AreaModel resp) {
                                            citys = resp.data;
                                            AreaEntity areaEntity = new AreaEntity();
                                            areaEntity.name = "全市";
                                            areaEntity.id = "0";
                                            citys.add(0,areaEntity);
                                            initData();
                                        }

                                        @Override
                                        public void onBtwError(BtwRespError<AreaModel> error) {
                                            UIUtils.toast(error.errorMessage);
                                        }

                                        @Override
                                        public void onNetworkError(VolleyUtils.NetworkError error) {
                                            UIUtils.toast(error.message);
                                        }

                                        @Override
                                        public void onRefreToken() {

                                        }
                                    }

                ).excute(AreaModel.class);
    }
    private void loadBranch(final int  page){

        BtwVolley mRequest = withBtwVolley().load(API.API_GET_SHOP_LIST)
                .setHeader("Accept", API.VERSION)
                .setParam("page", page)
                .setParam("city_id",instance.cityId)
                .setParam("area_id",areaId)
                .setParam("sort_type",sortType)
                .setParam("query_name",queryName)
                .setParam("service_type",instance.serviceType)
                .setParam("lat",instance.lat)
                .setParam("lng",instance.lng)
//                .setParam("is_rb",instance.isRb)
                .setParam("c_page",page)
                .method(Request.Method.GET)
                .setTimeout(10000)
                .setRetrys(0)
                .setUIComponent(this)
                .setResponseHandler(new BtwVolley.ResponseHandler<MarkerInfoModel>() {
                                        @Override
                                        public void onStart() {
                                            if(page == 1) {
//                                                getUIUtils().loading();
                                                mVContainer.showLoading();
                                            }
                                        }

                                        @Override
                                        public void onFinish() {
//                                            getUIUtils().dismissLoading();
                                        }

                                        @Override
                                        public void onResponse(MarkerInfoModel resp) {
                                            mVContainer.showLoadingView(false);
                                            mSwipeRefreshLayout.setRefreshing(false);
                                            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                                            mBranchLv.refreshComplete();
                                            if(resp.data != null ) {
                                                if (page == 1) {
                                                    adapter.setBranchList(resp.data,isRb);
                                                    if(resp.data.isEmpty()){
                                                        mVContainer.setVisibility(View.VISIBLE);
                                                        mVContainer.showLoadingView(true);
                                                        mVContainer.showError(getString(R.string.no_data), false);
                                                    }
                                                } else {
                                                    adapter.addBranchList(resp.data);
                                                }
                                                if (resp.data.size() < 15) {
                                                    loadMore(false);
                                                }else {
                                                    loadMore(true);
                                                }

                                            }else {
                                                mVContainer.showError(getString(R.string.no_data), false);
                                            }

                                        }

                                        @Override
                                        public void onBtwError(BtwRespError<MarkerInfoModel> error) {
                                            mVContainer.showError(error.errorMessage);
                                            loadMore(false);
                                        }

                                        @Override
                                        public void onNetworkError(VolleyUtils.NetworkError error) {
                                            mVContainer.showError(error.message);
                                            loadMore(false);
                                        }

                                        @Override
                                        public void onRefreToken() {

                                        }
                                    }

                );
                if(instance.serviceType == 1){
                    mRequest.setParam("is_rb",instance.isRb);
                }

        mRequest.excute(MarkerInfoModel.class);
    }
}
