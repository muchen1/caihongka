package com.rainbowcard.client.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.rainbowcard.client.model.BannerEntity;
import com.rainbowcard.client.model.TokenModel;
import com.rainbowcard.client.ui.adapter.ClassifyGridAdapter;
import com.rainbowcard.client.utils.LoginControl;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.widget.MyGridView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017-7-6.
 */
public class AllActivity extends MyBaseActivity {

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.rainbow_grid)
    MyGridView rainbowGrid;
    @InjectView(R.id.recommend_grid)
    MyGridView recommendGrid;

    ClassifyGridAdapter rainbowAdapter;
    ClassifyGridAdapter classifyAdapter;
    private String cityId;
    private double lat;
    private double lng;
    private List<BannerEntity> mDatas = new ArrayList<BannerEntity>();
    private List<BannerEntity> mRainbowDatas = new ArrayList<BannerEntity>();
    private List<BannerEntity> mClassifyDatas = new ArrayList<BannerEntity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(AllActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(AllActivity.this,true);
        mDatas = (List<BannerEntity>) getIntent().getSerializableExtra(Constants.KEY_BANNERS);
        cityId = getIntent().getStringExtra(Constants.KEY_CITY_ID);
        lat = getIntent().getDoubleExtra(Constants.KEY_LAT,0.0);
        lng = getIntent().getDoubleExtra(Constants.KEY_LNG,0.0);
        initData();
        initView();
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.all_classify));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rainbowAdapter = new ClassifyGridAdapter(this);
        rainbowAdapter.setBannerEntitys(mRainbowDatas);
        classifyAdapter = new ClassifyGridAdapter(this);
        classifyAdapter.setBannerEntitys(mClassifyDatas);
        rainbowGrid.setAdapter(rainbowAdapter);
        recommendGrid.setAdapter(classifyAdapter);
        rainbowGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MobclickAgent.onEventValue(AllActivity.this,mRainbowDatas.get(position).umClickKey,null,position);
                Intent intent;
                switch (mRainbowDatas.get(position).type) {
                    case 1:
                        intent = new Intent(AllActivity.this, SurprisedActivity.class);
                        intent.putExtra(Constants.KEY_URL, mRainbowDatas.get(position).url);
                        startActivity(intent);
                        break;
                    case 2:
                        if (LoginControl.getInstance(AllActivity.this).isLogin()) {
                            intent = new Intent(AllActivity.this, RechargeRainbowCardActivity.class);
                            startActivity(intent);
                        } else {
                            intent = new Intent(AllActivity.this, LoginActivity.class);
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
                        if (LoginControl.getInstance(AllActivity.this).isLogin()) {
                            refreshToken(mRainbowDatas.get(position).url,7);
                        } else {
                            intent = new Intent(AllActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case 8:
                        if (LoginControl.getInstance(AllActivity.this).isLogin()) {
                            intent = new Intent(AllActivity.this, BindCardActivity.class);
                            startActivity(intent);
                        } else {
                            intent = new Intent(AllActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case 9:
                        if (LoginControl.getInstance(AllActivity.this).isLogin()) {
                            intent = new Intent(AllActivity.this, MyDiscountActivity.class);
                            startActivity(intent);
                        } else {
                            intent = new Intent(AllActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case 10:
                        if (LoginControl.getInstance(AllActivity.this).isLogin()) {
                            intent = new Intent(AllActivity.this, GoodsDetailActivity.class);
                            intent.putExtra(Constants.KEY_GOODS_ID,mRainbowDatas.get(position).content);
                            startActivity(intent);
                        } else {
                            intent = new Intent(AllActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case 11:
                        if (LoginControl.getInstance(AllActivity.this).isLogin()) {
                            intent = new Intent(AllActivity.this, RechargeRainbowCardActivity.class);
                            startActivity(intent);
                        } else {
                            intent = new Intent(AllActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case 12:
                        if (LoginControl.getInstance(AllActivity.this).isLogin()) {
                            refreshToken(mRainbowDatas.get(position).url,12);
                        } else {
                            intent = new Intent(AllActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case 13:
                        if (LoginControl.getInstance(AllActivity.this).isLogin()) {
                            intent = new Intent(AllActivity.this,FreeWashTicketEntranceActivity.class);
                            startActivity(intent);
                        } else {
                            intent = new Intent(AllActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        break;
                }
            }
        });
        recommendGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MobclickAgent.onEventValue(AllActivity.this,mClassifyDatas.get(position).umClickKey,null,position);
                Intent intent;
                switch (mClassifyDatas.get(position).type) {
                    case 1:
                        intent = new Intent(AllActivity.this, SurprisedActivity.class);
                        intent.putExtra(Constants.KEY_URL, mClassifyDatas.get(position).url);
                        startActivity(intent);
                        break;
                    case 2:
                        if (LoginControl.getInstance(AllActivity.this).isLogin()) {
                            intent = new Intent(AllActivity.this, RechargeRainbowCardActivity.class);
                            startActivity(intent);
                        } else {
                            intent = new Intent(AllActivity.this, LoginActivity.class);
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
                        if (LoginControl.getInstance(AllActivity.this).isLogin()) {
                            refreshToken(mClassifyDatas.get(position).url,7);
                        } else {
                            intent = new Intent(AllActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case 8:
                        if (LoginControl.getInstance(AllActivity.this).isLogin()) {
                            intent = new Intent(AllActivity.this, BindCardActivity.class);
                            startActivity(intent);
                        } else {
                            intent = new Intent(AllActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case 9:
                        if (LoginControl.getInstance(AllActivity.this).isLogin()) {
                            intent = new Intent(AllActivity.this, MyDiscountActivity.class);
                            startActivity(intent);
                        } else {
                            intent = new Intent(AllActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case 10:
                        if (LoginControl.getInstance(AllActivity.this).isLogin()) {
                            intent = new Intent(AllActivity.this, GoodsDetailActivity.class);
                            intent.putExtra(Constants.KEY_GOODS_ID,mClassifyDatas.get(position).content);
                            startActivity(intent);
                        } else {
                            intent = new Intent(AllActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case 11:
                        if (LoginControl.getInstance(AllActivity.this).isLogin()) {
                            intent = new Intent(AllActivity.this, RechargeRainbowCardActivity.class);
                            startActivity(intent);
                        } else {
                            intent = new Intent(AllActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case 12:
                        if (LoginControl.getInstance(AllActivity.this).isLogin()) {
                            refreshToken(mClassifyDatas.get(position).url,12);
                        } else {
                            intent = new Intent(AllActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case 13:
                        if (LoginControl.getInstance(AllActivity.this).isLogin()) {
                            intent = new Intent(AllActivity.this,FreeWashTicketEntranceActivity.class);
                            startActivity(intent);
                        } else {
                            intent = new Intent(AllActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        break;
                }
            }
        });
    }

    void gotoBranchList(int type){
        Intent intent = new Intent(AllActivity.this, BranchActivity.class);
        intent.putExtra(Constants.KEY_CITY_ID,cityId);
        intent.putExtra(Constants.KEY_LAT,lat);
        intent.putExtra(Constants.KEY_LNG,lng);
        intent.putExtra(Constants.KEY_TYPE,type);
        startActivity(intent);
    }

    void initData(){
        for (BannerEntity bannerEntity : mDatas){
            if(bannerEntity.classType == 2){
                mClassifyDatas.add(bannerEntity);
            }else {
                mRainbowDatas.add(bannerEntity);
            }
        }
    }

    public void refreshToken(final String url,final int showType){
        withBtwVolley().load(API.API_REFRESH_TOKEN)
                .method(Request.Method.POST)
                .setHeader("Authorization",String.format(getString(R.string.token), MyConfig.getSharePreStr(AllActivity.this, Constants.USERINFO, Constants.UID)))
                .setHeader("Accept", API.VERSION)
                .setUIComponent(AllActivity.this)
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
                        MyConfig.putSharePre(AllActivity.this, Constants.USERINFO, Constants.UID, resp.data.token);
                        Intent intent;
                        if(showType == 12){
                            intent = new Intent(AllActivity.this, IntegralActivity.class);
                        }else {
                            intent = new Intent(AllActivity.this, SurprisedActivity.class);
                            intent.putExtra(Constants.KEY_CITY_ID,cityId);
                        }
                        intent.putExtra(Constants.KEY_URL,url);
                        startActivity(intent);
                    }

                    @Override
                    public void onBtwError(BtwRespError<TokenModel> error) {
                        Toast.makeText(AllActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(AllActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(TokenModel.class);
    }
}
