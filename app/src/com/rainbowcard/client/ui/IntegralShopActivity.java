package com.rainbowcard.client.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.BannerEntity;
import com.rainbowcard.client.model.HotGoodModel;
import com.rainbowcard.client.model.IntegralBannerModel;
import com.rainbowcard.client.model.IntegralModel;
import com.rainbowcard.client.model.TokenModel;
import com.rainbowcard.client.ui.adapter.GoodsAdapter;
import com.rainbowcard.client.utils.LoginControl;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.GridViewWithHeaderAndFooter;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.widget.LoadingFrameLayout;
import com.rainbowcard.client.widget.convenientbanner.ConvenientBanner;
import com.rainbowcard.client.widget.convenientbanner.holder.CBViewHolderCreator;
import com.rainbowcard.client.widget.convenientbanner.holder.Holder;
import com.rainbowcard.client.widget.convenientbanner.listener.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2016-10-25.
 */
public class IntegralShopActivity extends MyBaseActivity implements ViewPager.OnPageChangeListener,OnItemClickListener{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.v_frame)
    LoadingFrameLayout mFlLoading;
    @InjectView(R.id.lv_snatch)
    GridViewWithHeaderAndFooter mGameLv;

    private ConvenientBanner convenientBanner;

    private RelativeLayout fictitiousLayout;
    private RelativeLayout entityLayout;
    private RelativeLayout goodsLayout;
    private TextView integral;
    private TextView conversionRecord;

    GoodsAdapter adapter;
    private List<BannerEntity> mDatas = new ArrayList<BannerEntity>();

    private String cityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integral_shop);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(IntegralShopActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(IntegralShopActivity.this,true);
        cityId = getIntent().getStringExtra(Constants.KEY_CITY_ID);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        getMyIntegral();

        //开始自动翻页
        convenientBanner.startTurning(4000);
    }

    @Override
    public void onPause() {
        super.onPause();
        //停止翻页
        convenientBanner.stopTurning();
    }

    void init() {
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.integral_shop));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
                getMyIntegral();
            }
        });
        adapter = new GoodsAdapter(IntegralShopActivity.this);
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(IntegralShopActivity.this).inflate(R.layout.integral_grid_header,mGameLv,false);
        convenientBanner = (ConvenientBanner) linearLayout.findViewById(R.id.convenientBanner);
        fictitiousLayout = (RelativeLayout) linearLayout.findViewById(R.id.fictitious_layout);
        entityLayout = (RelativeLayout) linearLayout.findViewById(R.id.entity_layout);
        goodsLayout = (RelativeLayout) linearLayout.findViewById(R.id.goods_layout);
        conversionRecord = (TextView) linearLayout.findViewById(R.id.conversion_record);
        integral = (TextView) linearLayout.findViewById(R.id.integral);
        mGameLv.addHeaderView(linearLayout);
        mGameLv.setAdapter(adapter);
        mGameLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(adapter.getItem(position).remnant != 0) {
                    Intent intent = new Intent(IntegralShopActivity.this, GoodsDetailActivity.class);
                    intent.putExtra(Constants.KEY_GOODS_ID, adapter.getHotGoodEntitys().get(position).id);
                    startActivity(intent);
                }
            }
        });
        conversionRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntegralShopActivity.this,ExchangeListActivity.class);
                startActivity(intent);
            }
        });
    }

    public List<BannerEntity> mBanners = new ArrayList<BannerEntity>();

    void initView() {
//        mBanners.clear();
//        BannerEntity bannerEntity = new BannerEntity();
//        bannerEntity.img = "http://pic89.huitu.com/res/20161109/519224_20161109105644397396_1.jpg";
//        mBanners.add(bannerEntity);
//        BannerEntity bannerEntity2 = new BannerEntity();
//        bannerEntity2.img = "http://pic89.huitu.com/res/20161104/113936_20161104103315139337_1.jpg";
//        mBanners.add(bannerEntity2);

        getHotGoods();
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
        fictitiousLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoGoodsList("2");
            }
        });
        entityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoGoodsList("3");
            }
        });
        goodsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoGoodsList("0");
            }
        });

    }

    void gotoGoodsList(String type){
        Intent intent = new Intent(IntegralShopActivity.this,GoodsListActivity.class);
        intent.putExtra(Constants.KEY_IS_GOODS,type);
        startActivity(intent);
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
                intent = new Intent(IntegralShopActivity.this, SurprisedActivity.class);
                intent.putExtra(Constants.KEY_URL, mBanners.get(position).url);
//                intent.putExtra(Constants.KEY_URL,"http://sports.sohu.com/20170120/n479180172.shtml");
                startActivity(intent);
                break;
            case 2:
                if (LoginControl.getInstance(IntegralShopActivity.this).isLogin()) {
                    intent = new Intent(IntegralShopActivity.this, RechargeAccountActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(IntegralShopActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case 3:
                gotoGoodsList("2");
                break;
            case 4:
                gotoGoodsList("3");
                break;
            case 5:

                break;
            case 6:
                gotoGoodsList("0");
                break;
            case 7:
                if (LoginControl.getInstance(IntegralShopActivity.this).isLogin()) {
                    refreshToken(mDatas.get(position).url,7);
                } else {
                    intent = new Intent(IntegralShopActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case 8:
                if (LoginControl.getInstance(IntegralShopActivity.this).isLogin()) {
                    intent = new Intent(IntegralShopActivity.this, BindCardActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(IntegralShopActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case 9:
                if (LoginControl.getInstance(IntegralShopActivity.this).isLogin()) {
                    intent = new Intent(IntegralShopActivity.this, MyDiscountActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(IntegralShopActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case 10:
                if (LoginControl.getInstance(IntegralShopActivity.this).isLogin()) {
                    intent = new Intent(IntegralShopActivity.this, GoodsDetailActivity.class);
                    intent.putExtra(Constants.KEY_GOODS_ID,mBanners.get(position).content);
                    startActivity(intent);
                } else {
                    intent = new Intent(IntegralShopActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case 11:
                if (LoginControl.getInstance(IntegralShopActivity.this).isLogin()) {
                    intent = new Intent(IntegralShopActivity.this, RechargeRainbowCardActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(IntegralShopActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case 12:
                if (LoginControl.getInstance(IntegralShopActivity.this).isLogin()) {
                    refreshToken(mDatas.get(position).url,12);
                } else {
                    intent = new Intent(IntegralShopActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
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
            Picasso.with(IntegralShopActivity.this).load(String.format(getString(R.string.img_url),data.img))
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.banner_detail)
                    .error(R.drawable.banner_detail).into(imageView);
        }
    }

    public void getMyIntegral(){
        withBtwVolley().load(API.API_GET_MY_INTEGRAL)
                .setHeader("Accept", API.VERSION)
                .setHeader("Authorization",String.format(getString(R.string.token), MyConfig.getSharePreStr(IntegralShopActivity.this, Constants.USERINFO, Constants.UID)))
                .method(Request.Method.GET)
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(20000)
                .setResponseHandler(new BtwVolley.ResponseHandler<IntegralModel>() {
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
                    public void onResponse(IntegralModel resp) {
                        String userIntegral = String.valueOf(resp.data.integral);
                        integral.setText(String.format(getString(R.string.usable_integral),userIntegral));
                        Spannable span = new SpannableString(integral.getText());
                        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.money_color)), 4, 4 + userIntegral.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        integral.setText(span);
                        getBanner(cityId);
                    }

                    @Override
                    public void onBtwError(BtwRespError<IntegralModel> error) {
                        UIUtils.toast(error.errorMessage);
                        integral.setText(String.format(getString(R.string.usable_integral),0));
                        Spannable span = new SpannableString(integral.getText());
                        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.money_color)), 4, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        integral.setText(span);
                        getBanner(cityId);
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        UIUtils.toast(getString(R.string.network_error));
                        integral.setText(String.format(getString(R.string.usable_integral),0));
                        Spannable span = new SpannableString(integral.getText());
                        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.money_color)), 4, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        integral.setText(span);
                        getBanner(cityId);
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(IntegralModel.class);

    }

    public void getBanner(String cityId){
        withBtwVolley().load(API.API_GET_INTEGRAL_BANNER)
                .setHeader("Accept", API.VERSION)
                .setHeader("Authorization",String.format(getString(R.string.token), MyConfig.getSharePreStr(IntegralShopActivity.this, Constants.USERINFO, Constants.UID)))
                .setParam("city_id",cityId)
                .method(Request.Method.GET)
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(20000)
                .setResponseHandler(new BtwVolley.ResponseHandler<IntegralBannerModel>() {
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
                    public void onResponse(IntegralBannerModel resp) {
                        mBanners = resp.data;
                        mDatas.clear();
                        if(!resp.data.isEmpty()) {
                            mDatas.addAll(resp.data);
                        }
                        initView();
                    }

                    @Override
                    public void onBtwError(BtwRespError<IntegralBannerModel> error) {
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
                }).excute(IntegralBannerModel.class);
    }

    public void refreshToken(final String url,final int showType){
        withBtwVolley().load(API.API_REFRESH_TOKEN)
                .method(Request.Method.POST)
                .setHeader("Authorization",String.format(getString(R.string.token), MyConfig.getSharePreStr(IntegralShopActivity.this, Constants.USERINFO, Constants.UID)))
                .setHeader("Accept", API.VERSION)
                .setUIComponent(IntegralShopActivity.this)
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
                        MyConfig.putSharePre(IntegralShopActivity.this, Constants.USERINFO, Constants.UID, resp.data.token);
                        Intent intent;
                        if(showType == 12){
                            intent = new Intent(IntegralShopActivity.this, IntegralActivity.class);
                        }else {
                            intent = new Intent(IntegralShopActivity.this, SurprisedActivity.class);
                        }
                        intent.putExtra(Constants.KEY_URL,url);
                        startActivity(intent);
                    }

                    @Override
                    public void onBtwError(BtwRespError<TokenModel> error) {
                        Toast.makeText(IntegralShopActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(IntegralShopActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(TokenModel.class);
    }

    //获取热门商品列表
    public void getHotGoods(){
        withBtwVolley().load(API.API_GET_HOT_GOODS_LIST)
                .setHeader("Accept", API.VERSION)
                .setHeader("Authorization",String.format(getString(R.string.token), MyConfig.getSharePreStr(IntegralShopActivity.this, Constants.USERINFO, Constants.UID)))
                .method(Request.Method.GET)
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(20000)
                .setResponseHandler(new BtwVolley.ResponseHandler<HotGoodModel>() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onResponse(HotGoodModel resp) {
                        adapter.setHotGoodEntitys(resp.data);
                    }

                    @Override
                    public void onBtwError(BtwRespError<HotGoodModel> error) {
                        UIUtils.toast(error.errorMessage);
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        UIUtils.toast(getString(R.string.network_error));
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(HotGoodModel.class);
    }
}
