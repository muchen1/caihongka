package com.rainbowcard.client.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.Request;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseFragment;
import com.rainbowcard.client.base.MyBasicFragment;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.BannerEntity;
import com.rainbowcard.client.model.BannerModel;
import com.rainbowcard.client.model.MarkerInfoModel;
import com.rainbowcard.client.model.NoticeModel;
import com.rainbowcard.client.model.TokenModel;
import com.rainbowcard.client.ui.AllActivity;
import com.rainbowcard.client.ui.BindCardActivity;
import com.rainbowcard.client.ui.BranchActivity;
import com.rainbowcard.client.ui.CheckIllegalActivity;
import com.rainbowcard.client.ui.CommonInsuranceActivity;
import com.rainbowcard.client.ui.FreeWashTicketEntranceActivity;
import com.rainbowcard.client.ui.GoodsDetailActivity;
import com.rainbowcard.client.ui.IntegralActivity;
import com.rainbowcard.client.ui.LoginActivity;
import com.rainbowcard.client.ui.MainActivity;
import com.rainbowcard.client.ui.MyDiscountActivity;
import com.rainbowcard.client.ui.PhotoViewActivity;
import com.rainbowcard.client.ui.RechargeAccountActivity;
import com.rainbowcard.client.ui.RechargeRainbowCardActivity;
import com.rainbowcard.client.ui.ShopDetailActivity;
import com.rainbowcard.client.ui.SurprisedActivity;
import com.rainbowcard.client.ui.adapter.GridViewAdapter;
import com.rainbowcard.client.ui.adapter.RecommendListAdapter;
import com.rainbowcard.client.ui.adapter.RvAdapter;
import com.rainbowcard.client.ui.adapter.ViewPagerAdapter;
import com.rainbowcard.client.utils.LoginControl;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.LoadingFrameLayout;
import com.rainbowcard.client.widget.ScrollToFooterLoadMoreListView;
import com.rainbowcard.client.widget.convenientbanner.ConvenientBanner;
import com.rainbowcard.client.widget.convenientbanner.holder.CBViewHolderCreator;
import com.rainbowcard.client.widget.convenientbanner.holder.Holder;
import com.rainbowcard.client.widget.convenientbanner.listener.OnItemClickListener;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2016-10-25.
 */
public class HomeFragment extends MyBasicFragment implements ViewPager.OnPageChangeListener,OnItemClickListener{

    @InjectView(R.id.v_frame)
    LoadingFrameLayout mFlLoading;
//    @InjectView(R.id.v_refresh)
//    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.lv_snatch)
    ScrollToFooterLoadMoreListView mGameLv;

    private ConvenientBanner convenientBanner;
//    private RecyclerView rv;
    private RelativeLayout washLayout;
    private RelativeLayout waxLayout;
    private RelativeLayout filmLayout;
    private RelativeLayout checkLayout;
    private ImageView bannerIv;

    RecommendListAdapter adapter;
    private RvAdapter rvAdapter;
    MainActivity instance = null;
    public int mCurrentPage = 1;
    private ViewPager mPager;
    private LinearLayout navLayout;
    private List<View> mPagerList;
    private List<BannerEntity> mDatas = new ArrayList<BannerEntity>();
    private List<NoticeModel.InfoData> mInfoDatas = new ArrayList<NoticeModel.InfoData>();
    private LinearLayout mLlDot;
    private LayoutInflater inflater;
    TextSwitcher textSwitcher;
    RelativeLayout noticeLayout;
    TextView remindText;
    TextView numberOne;
    TextView numberTwo;
    TextView numberThree;
    TextView dateThree;
    private BitHandler bitHandler;
    private MyThread myThread;
    private boolean flag = true;
    private int index = 0;

    String[] picturelist;

    /**
     * 总的页数
     */
    private int pageCount;
    /**
     * 每一页显示的个数
     */
    private int pageSize = 8;
    /**
     * 当前显示的是第几页
     */
    private int curIndex = 0;


    public static HomeFragment newInstance(){
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_home, container, false);
        ButterKnife.inject(this, view);
        instance = (MainActivity) getActivity();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        mCurrentPage = 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        flag = true;
        mCurrentPage = 1;
        curIndex = 0;
        getBanner(instance.cityId);
        //开始自动翻页
        convenientBanner.startTurning(4000);
//        if(MyConfig.getSharePreBoolean(getActivity(), Constants.USERINFO, Constants.IS_DOWNLOAD)){
//            new UpdateManger(getActivity(),MyConfig.getSharePreStr(getActivity(),Constants.USERINFO,Constants.DOWNLOAD_APK_URL)).
//                    showConfirmDialog("温馨提示",MyConfig.getSharePreStr(getActivity(),Constants.USERINFO,Constants.UPDATE_REMARKS));
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //停止翻页
        convenientBanner.stopTurning();
    }

    void init() {
//        mSwipeRefreshLayout.setColorSchemeResources(R.color.app_blue, R.color.app_blue, R.color.app_blue, R.color.app_blue);
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                mCurrentPage = 1;
//                getBanner();
//            }
//        });
        mFlLoading.setRetryButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPage = 1;
                getBanner(instance.cityId);
            }
        });
        adapter = new RecommendListAdapter(getActivity());
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.home_list_header,mGameLv,false);
        convenientBanner = (ConvenientBanner) linearLayout.findViewById(R.id.convenientBanner);
//        rv = (RecyclerView) linearLayout.findViewById(R.id.rv);
        navLayout = (LinearLayout) linearLayout.findViewById(R.id.nav_layout);
        mPager = (ViewPager) linearLayout.findViewById(R.id.viewpager);
        inflater = LayoutInflater.from(getActivity());
        mLlDot = (LinearLayout) linearLayout.findViewById(R.id.ll_dot);
        washLayout = (RelativeLayout) linearLayout.findViewById(R.id.wash_layout);
        waxLayout = (RelativeLayout) linearLayout.findViewById(R.id.wax_layout);
        filmLayout = (RelativeLayout) linearLayout.findViewById(R.id.film_layout);
        checkLayout = (RelativeLayout) linearLayout.findViewById(R.id.check_layout);
        bannerIv = (ImageView) linearLayout.findViewById(R.id.banner_image);
        bannerIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        textSwitcher = (TextSwitcher) linearLayout.findViewById(R.id.profileSwitcher);
        textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView = null;
                if(textView == null) {
                    textView = new TextView(getActivity());
                    textView.setSingleLine();
                    textView.setTextSize(13);
                    textView.setTextColor(getResources().getColor(R.color.app_black));
                    textView.setEllipsize(TextUtils.TruncateAt.END);
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    lp.gravity = Gravity.CENTER_VERTICAL;
                    textView.setLayoutParams(lp);
                }
                return textView;

            }
        });
        noticeLayout = (RelativeLayout) linearLayout.findViewById(R.id.notice_layout);
        numberOne = (TextView) linearLayout.findViewById(R.id.number_one);
        numberTwo = (TextView) linearLayout.findViewById(R.id.number_two);
        numberThree = (TextView) linearLayout.findViewById(R.id.number_three);
        dateThree = (TextView) linearLayout.findViewById(R.id.date_three);
        remindText = (TextView) linearLayout.findViewById(R.id.remind_text);
        mGameLv.addHeaderView(linearLayout);
        mGameLv.setAdapter(adapter);
        mGameLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),ShopDetailActivity.class);
                intent.putExtra(Constants.KEY_SHOP_ID,adapter.getBranchList().get(position-1).shopId);
                startActivity(intent);
            }
        });
    }

    public List<BannerEntity> mBanners = new ArrayList<BannerEntity>();
    public List<BannerEntity> middleBanners = new ArrayList<BannerEntity>();

    void initView() {
//        mBanners.clear();
//        BannerEntity bannerEntity = new BannerEntity();
//        bannerEntity.img = "http://pic89.huitu.com/res/20161109/519224_20161109105644397396_1.jpg";
//        mBanners.add(bannerEntity);
//        BannerEntity bannerEntity2 = new BannerEntity();
//        bannerEntity2.img = "http://pic89.huitu.com/res/20161104/113936_20161104103315139337_1.jpg";
//        mBanners.add(bannerEntity2);
        getShoop(instance.lat, instance.lng,instance.cityId);
        getNotice();
        loadMore(false);
        convenientBanner.setPages(
                new CBViewHolderCreator<LocalImageHolderView>() {
                    @Override
                    public LocalImageHolderView createHolder() {
                        return new LocalImageHolderView();
                    }
                }, mBanners)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused})
                //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
                .setOnPageChangeListener(this)//监听翻页事件
                .setOnItemClickListener(this);
        if(mBanners.size() < 2) {
            convenientBanner.setCanLoop(false);
        }
        washLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoBranchList(1);
            }
        });
        waxLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoBranchList(4);
            }
        });
        filmLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoBranchList(2);
            }
        });
        checkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CheckIllegalActivity.class);
                startActivity(intent);
            }
        });

        if(!middleBanners.isEmpty()){
            Picasso.with(getActivity()).load(String.format(getString(R.string.img_url),middleBanners.get(0).img))
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.banner_image_detail)
                    .error(R.drawable.banner_image_detail).into(bannerIv);
        }

        bannerIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;
                switch (middleBanners.get(0).type) {
                    case 1:
                        intent = new Intent(getActivity(), SurprisedActivity.class);
                        intent.putExtra(Constants.KEY_URL,middleBanners.get(0).url);
                        startActivity(intent);
                        break;
                    case 2:
                        if (LoginControl.getInstance(getActivity()).isLogin()) {
                            intent = new Intent(getActivity(), RechargeRainbowCardActivity.class);
                            startActivity(intent);
                        } else {
                            intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                        }
                        break;
                }
            }
        });

    }

    void gotoBranchList(int type){
        Intent intent = new Intent(getActivity(), BranchActivity.class);
        intent.putExtra(Constants.KEY_CITY_ID,instance.cityId);
        intent.putExtra(Constants.KEY_LAT,instance.lat);
        intent.putExtra(Constants.KEY_LNG,instance.lng);
        intent.putExtra(Constants.KEY_TYPE,type);
        startActivity(intent);
    }
    private void loadMore(boolean toLoad) {
        if (toLoad) {
            mGameLv.setOnScrollToRefreshListener(new ScrollToFooterLoadMoreListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                    getShoop(instance.lat, instance.lng,instance.cityId);
                }
            });
        } else {
            mGameLv.setOnScrollToRefreshListener(new ScrollToFooterLoadMoreListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                    mGameLv.refreshComplete();
                }
            });
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onItemClick(int position) {
        Intent intent;
        switch (mBanners.get(position).type) {
            case 1:
                intent = new Intent(getActivity(), SurprisedActivity.class);
                intent.putExtra(Constants.KEY_URL, mBanners.get(position).url);
//                intent.putExtra(Constants.KEY_URL,"http://sports.sohu.com/20170120/n479180172.shtml");
                startActivity(intent);
                break;
            case 2:
                if (LoginControl.getInstance(getActivity()).isLogin()) {
                    intent = new Intent(getActivity(), RechargeAccountActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case 3:
                gotoBranchList(1);
                break;
            case 4:
                gotoBranchList(2);
                break;
            case 5:

                break;
            case 6:
                gotoBranchList(4);
                break;
            case 7:
                if (LoginControl.getInstance(getActivity()).isLogin()) {
                    refreshToken(mDatas.get(position).url,7);
                } else {
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case 8:
                if (LoginControl.getInstance(getActivity()).isLogin()) {
                    intent = new Intent(getActivity(), BindCardActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case 9:
                if (LoginControl.getInstance(getActivity()).isLogin()) {
                    intent = new Intent(getActivity(), MyDiscountActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case 10:
                if (LoginControl.getInstance(getActivity()).isLogin()) {
                    intent = new Intent(getActivity(), GoodsDetailActivity.class);
                    intent.putExtra(Constants.KEY_GOODS_ID,mBanners.get(position).content);
                    startActivity(intent);
                } else {
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case 11:
                if (LoginControl.getInstance(getActivity()).isLogin()) {
                    intent = new Intent(getActivity(), RechargeRainbowCardActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case 12:
                if (LoginControl.getInstance(getActivity()).isLogin()) {
                    refreshToken(mDatas.get(position).url,12);
                } else {
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case 14:
                picturelist = mBanners.get(position).url.split("\\|");
                intent = new Intent(getActivity(), PhotoViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.KEY_PIC_URLS, picturelist);
                intent.putExtra(Constants.KEY_PIC_POS, 0);
                startActivity(intent);
                break;

        }
    }

    public class LocalImageHolderView implements Holder<BannerEntity> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, final int position, BannerEntity data) {
//            imageView.setImageResource(data);
            Picasso.with(getActivity()).load(String.format(getString(R.string.img_url),data.img))
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.banner_detail)
                    .error(R.drawable.banner_detail).into(imageView);
        }
    }

    public void getBanner(String cityId){
        withBtwVolley().load(API.API_GET_BANNER)
                .setHeader("Accept", API.VERSION)
                .setParam("city_id",cityId)
                .method(Request.Method.GET)
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(20000)
                .setResponseHandler(new BtwVolley.ResponseHandler<BannerModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
//                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(BannerModel resp) {
                        mBanners = resp.data.siteHead;
                        mDatas.clear();
                        if(!resp.data.siteMainNavigate.isEmpty()) {
                            mDatas.addAll(resp.data.siteMainNavigate);


                        }
//                        mDatas.addAll(resp.data.siteMainNavigate);
//                        mDatas.addAll(resp.data.siteHead);
                        middleBanners = resp.data.siteMiddle;
                        instance.setBannerEntities(resp.data.siteShopIndex);
//                        rv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//                        rvAdapter = new RvAdapter(getActivity(),mBanners);
//                        rv.setAdapter(rvAdapter);
//                        rvAdapter.setOnItemClickListener(new RvAdapter.OnItemClickListener() {
//                            @Override
//                            public void onItemClickListener(int position, BannerEntity data) {
//                                Toast.makeText(getActivity(), "点击了" + position, Toast.LENGTH_SHORT).show();
//                            }
//
//                        });
                        //总的页数=总数/每页数量，并取整
//                        pageCount = (int) Math.ceil(mDatas.size() * 1.0 / pageSize);
                        pageCount = 1; //固定显示两排，不带滑动，上面是可以滑动
                        mPagerList = new ArrayList<View>();
                        for (int i = 0; i < pageCount; i++) {
                            //每个页面都是inflate出一个新实例
                            GridView gridView = (GridView) inflater.inflate(R.layout.gridview, mPager, false);
                            gridView.setAdapter(new GridViewAdapter(getActivity(), mDatas, i, pageSize));
                            mPagerList.add(gridView);

                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    int pos = position + curIndex * pageSize;

//                                    MobclickAgent.onEventValue(getActivity(),mDatas.get(pos).eventId,null,pos);
                                    Intent intent;
                                    if(mDatas.size() > 8 && pos == 7){
//                                        intent = new Intent(getActivity(), AllActivity.class);
//                                        intent.putExtra(Constants.KEY_BANNERS, (Serializable) mDatas);
//                                        intent.putExtra(Constants.KEY_CITY_ID,instance.cityId);
//                                        intent.putExtra(Constants.KEY_LAT,instance.lat);
//                                        intent.putExtra(Constants.KEY_LNG,instance.lng);
//                                        startActivity(intent);
                                        intent = new Intent(getActivity(), CommonInsuranceActivity.class);
                                        startActivity(intent);
                                    }else {
                                        MobclickAgent.onEventValue(getActivity(),mDatas.get(pos).umClickKey,null,pos);
                                        switch (mDatas.get(pos).type) {
                                            case 1:
                                                intent = new Intent(getActivity(), SurprisedActivity.class);
                                                intent.putExtra(Constants.KEY_URL, mDatas.get(pos).url);
                                                startActivity(intent);
                                                break;
                                            case 2:
                                                if (LoginControl.getInstance(getActivity()).isLogin()) {
                                                    intent = new Intent(getActivity(), RechargeAccountActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    intent = new Intent(getActivity(), LoginActivity.class);
                                                    startActivity(intent);
                                                }
                                                break;
                                            case 3:
                                                gotoBranchList(1);
                                                break;
                                            case 4:
                                                gotoBranchList(2);
                                                break;
                                            case 5:

                                                break;
                                            case 6:
                                                gotoBranchList(4);
                                                break;
                                            case 7:
                                                if (LoginControl.getInstance(getActivity()).isLogin()) {
                                                    refreshToken(mDatas.get(pos).url,7);
                                                } else {
                                                    intent = new Intent(getActivity(), LoginActivity.class);
                                                    startActivity(intent);
                                                }
                                                break;
                                            case 8:
                                                if (LoginControl.getInstance(getActivity()).isLogin()) {
                                                    intent = new Intent(getActivity(), BindCardActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    intent = new Intent(getActivity(), LoginActivity.class);
                                                    startActivity(intent);
                                                }
                                                break;
                                            case 9:
                                                if (LoginControl.getInstance(getActivity()).isLogin()) {
                                                    intent = new Intent(getActivity(), MyDiscountActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    intent = new Intent(getActivity(), LoginActivity.class);
                                                    startActivity(intent);
                                                }
                                                break;
                                            case 10:
                                                if (LoginControl.getInstance(getActivity()).isLogin()) {
                                                    intent = new Intent(getActivity(), GoodsDetailActivity.class);
                                                    intent.putExtra(Constants.KEY_GOODS_ID,mBanners.get(position).content);
                                                    startActivity(intent);
                                                } else {
                                                    intent = new Intent(getActivity(), LoginActivity.class);
                                                    startActivity(intent);
                                                }
                                                break;
                                            case 11:
                                                if (LoginControl.getInstance(getActivity()).isLogin()) {
                                                    intent = new Intent(getActivity(), RechargeRainbowCardActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    intent = new Intent(getActivity(), LoginActivity.class);
                                                    startActivity(intent);
                                                }
                                                break;
                                            case 12:
                                                if (LoginControl.getInstance(getActivity()).isLogin()) {
                                                    refreshToken(mDatas.get(position).url,12);
                                                } else {
                                                    intent = new Intent(getActivity(), LoginActivity.class);
                                                    startActivity(intent);
                                                }
                                                break;
                                            case 13:
                                                if (LoginControl.getInstance(getActivity()).isLogin()) {
                                                    intent = new Intent(getActivity(),FreeWashTicketEntranceActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    intent = new Intent(getActivity(), LoginActivity.class);
                                                    startActivity(intent);
                                                }

                                                break;
                                        }
                                    }
//                                    Toast.makeText(getActivity(), mDatas.get(pos).title, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        //设置适配器
                        mPager.setAdapter(new ViewPagerAdapter(mPagerList));
                        //设置圆点
                        if(pageCount <= 1){
                            mLlDot.setVisibility(View.GONE);
                        }else {
                            mLlDot.setVisibility(View.VISIBLE);
                            setOvalLayout();
                        }
                        if(mDatas.isEmpty()){
                            navLayout.setVisibility(View.VISIBLE);
                        }else {
                            navLayout.setVisibility(View.GONE);
//                            setOvalLayout();
                        }
                        initView();
                    }

                    @Override
                    public void onBtwError(BtwRespError<BannerModel> error) {
                        UIUtils.toast(error.errorMessage);
                        initView();
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        UIUtils.toast(getString(R.string.network_error));
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(BannerModel.class);
    }

    public void refreshToken(final String url,final int showType){
        withBtwVolley().load(API.API_REFRESH_TOKEN)
                .method(Request.Method.POST)
                .setHeader("Authorization",String.format(getString(R.string.token), MyConfig.getSharePreStr(getActivity(), Constants.USERINFO, Constants.UID)))
                .setHeader("Accept", API.VERSION)
                .setUIComponent(getActivity())
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<TokenModel>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onResponse(TokenModel resp) {
                        MyConfig.putSharePre(getActivity(), Constants.USERINFO, Constants.UID, resp.data.token);
                        Intent intent;
                        if(showType == 12){
                            intent = new Intent(getActivity(), IntegralActivity.class);
                        }else {
                            intent = new Intent(getActivity(), SurprisedActivity.class);
                            intent.putExtra(Constants.KEY_PROVINCE,instance.province);
                            intent.putExtra(Constants.KEY_CITY,instance.city);
                            intent.putExtra(Constants.KEY_CITY_ID,instance.cityId);
                        }
                        intent.putExtra(Constants.KEY_URL,url);
                        startActivity(intent);
                    }

                    @Override
                    public void onBtwError(BtwRespError<TokenModel> error) {
                        Toast.makeText(getActivity(), error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(getActivity(), R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(TokenModel.class);
    }

    /**
     * 设置圆点
     */
    public void setOvalLayout() {
        mLlDot.removeAllViews();
        for (int i = 0; i < pageCount; i++) {
            mLlDot.addView(inflater.inflate(R.layout.dot, null));
        }
        // 默认显示第一页
        mLlDot.getChildAt(0).findViewById(R.id.v_dot)
                .setBackgroundResource(R.drawable.dot_selected);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageSelected(int position) {
                // 取消圆点选中
                mLlDot.getChildAt(curIndex)
                        .findViewById(R.id.v_dot)
                        .setBackgroundResource(R.drawable.dot_normal);
                // 圆点选中
                mLlDot.getChildAt(position)
                        .findViewById(R.id.v_dot)
                        .setBackgroundResource(R.drawable.dot_selected);
                curIndex = position;
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }
    public void getShoop(double lat , double lng,String cityId){
        withBtwVolley().load(API.API_GET_RECOMMEND)
                .setHeader("Accept", API.VERSION)
                .setParam("type",1)
                .setParam("lng",lng)
                .setParam("lat",lat)
                .setParam("is_rb",1)
                .setParam("city_id",cityId)
                .method(Request.Method.GET)
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(20000)
                .setResponseHandler(new BtwVolley.ResponseHandler<MarkerInfoModel>() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onResponse(MarkerInfoModel resp) {
                        adapter.setBranchList(resp.data);

                        if(mCurrentPage == 1 ){
                            if(resp.data == null || resp.data.isEmpty()) {
                                UIUtils.toast("该城市暂无数据");
                            }
                        }

                        if(resp.data.size() < 20){
                            loadMore(false);
                        }
                        mCurrentPage = mCurrentPage + 1;

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
    public void getNotice(){
        withBtwVolley().load(API.API_GET_NOTICE)
                .setHeader("Accept", API.VERSION2D11)
                .setParam("city_id",instance.cityId)
                .method(Request.Method.GET)
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(20000)
                .setResponseHandler(new BtwVolley.ResponseHandler<NoticeModel>() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onResponse(NoticeModel resp) {

                        if(resp.data.infoDatas.isEmpty() && resp.data.travelDatas.isEmpty()){
                            noticeLayout.setVisibility(View.GONE);
                        }else {
                            noticeLayout.setVisibility(View.VISIBLE);
                            if(resp.data.infoDatas.isEmpty()){
                                textSwitcher.setVisibility(View.GONE);
                                remindText.setText("限行提醒：");
                                numberOne.setText(resp.data.travelDatas.get(0).xxweihao);
                                numberTwo.setText(resp.data.travelDatas.get(1).xxweihao);
                                numberThree.setText(resp.data.travelDatas.get(2).xxweihao);
                                dateThree.setText(resp.data.travelDatas.get(2).week);
                            }else {
                                mInfoDatas = resp.data.infoDatas;
                                if(flag) {
                                    flag = false;
                                    test();
                                }
                                textSwitcher.setVisibility(View.VISIBLE);
                                remindText.setText("公告：");
                            }
                        }
                    }

                    @Override
                    public void onBtwError(BtwRespError<NoticeModel> error) {
                        UIUtils.toast(error.errorMessage);
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        UIUtils.toast(getString(R.string.network_error));
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(NoticeModel.class);
    }

    private String[] strings={"今日尾号限行3和8","今日充值200元以上送免费洗车券5张","今日易车生活洗车85折"};

    class BitHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            SpannableString sp = new SpannableString(String.format(getResources().getString(R.string.snatch_title),mHeadlines.get(index).uname,mHeadlines.get(index).prize));
//            String memberStateMent = String.format(getResources().getString(R.string.snatch_title),mHeadlines.get(index).uname,mHeadlines.get(index).prize);
//            sp.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.tab_unselected_text)), memberStateMent.indexOf(mHeadlines.get(index).uname),
//                    memberStateMent.indexOf(mHeadlines.get(index).uname) + mHeadlines.get(index).uname.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            sp.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_black)), memberStateMent.indexOf(mHeadlines.get(index).prize),
//                    memberStateMent.indexOf(mHeadlines.get(index).prize) + mHeadlines.get(index).prize.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if(textSwitcher != null) {
                textSwitcher.setText(mInfoDatas.get(index).notice);
                index++;
                if (index == mInfoDatas.size()) {
                    index = 0;
                }
            }
        }
    }

    private void test(){
        bitHandler = new BitHandler();
        if(myThread == null) {
            myThread = new MyThread();
            myThread.start();
        }
    }

    private class MyThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (index < mInfoDatas.size()) {
                try {
                    synchronized (this) {
                        bitHandler.sendEmptyMessage(0);
                        this.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
