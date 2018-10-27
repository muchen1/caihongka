package com.rainbowcard.client.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
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
import com.rainbowcard.client.model.ExchangeDetailModel;
import com.rainbowcard.client.model.ExchangeEntity;
import com.rainbowcard.client.model.ExchangeOrderModel;
import com.rainbowcard.client.model.GoodDetailModel;
import com.rainbowcard.client.model.HotGoodEntity;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.StringUtil;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.widget.LoadingFrameLayout;
import com.rainbowcard.client.widget.MyScrollView;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017-12-8.
 */
public class ExchangeDetailActivity extends MyBaseActivity {

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.scrollView)
    MyScrollView scrollView;
    @InjectView(R.id.fl_loading)
    LoadingFrameLayout mFlLoading;
    @InjectView(R.id.goods_img)
    ImageView goodsImg;
    @InjectView(R.id.goods_name)
    TextView goodsName;
    @InjectView(R.id.goods_integral)
    TextView goodsIntegral;
    @InjectView(R.id.shipping_status)
    TextView shippingStatus;
    @InjectView(R.id.user_name)
    TextView userName;
    @InjectView(R.id.phone)
    TextView phone;
    @InjectView(R.id.address)
    TextView address;
    @InjectView(R.id.goods_info)
    TextView goodsInfo;
    @InjectView(R.id.exchange_integral)
    TextView exchangeIntegral;
    @InjectView(R.id.exchange_btn)
    TextView exchangeBtn;
    @InjectView(R.id.addr_layout)
    RelativeLayout addrLayout;

    String tradeNo;
    ExchangeEntity exchangeEntity;

    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_detail);
        ButterKnife.inject(this);
        tradeNo = getIntent().getStringExtra(Constants.KEY_TRADE_NO);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getExchangeDetail();
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        scrollView.smoothScrollTo(0, 0);

    }
    void refreshUI(final ExchangeEntity exchangeEntity){

        mHeadControlPanel.setMiddleTitle(exchangeEntity.goodsTitle);
            if (TextUtils.isEmpty(exchangeEntity.goodsImg)) {
                goodsImg.setImageResource(R.drawable.detail_default);
            } else {
                Picasso.with(ExchangeDetailActivity.this)
                        .load(StringUtil.getUrl(exchangeEntity.goodsImg,ExchangeDetailActivity.this))
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.detail_default)
                        .error(R.drawable.detail_default).into(goodsImg);
            }

        goodsName.setText(exchangeEntity.goodsTitle);
        goodsIntegral.setText(String.format(getString(R.string.integral),exchangeEntity.payIntegral));
        goodsInfo.setText(Html.fromHtml(exchangeEntity.goodsInfo));

        if(exchangeEntity.goodsType == 3){
            addrLayout.setVisibility(View.VISIBLE);
            userName.setText(exchangeEntity.address.userName);
            phone.setText(exchangeEntity.address.phone);
            address.setText(exchangeEntity.address.province+exchangeEntity.address.city+exchangeEntity.address.area+exchangeEntity.address.addr);
        }else {
            addrLayout.setVisibility(View.GONE);
        }

        switch (exchangeEntity.status){
            case 1:
                shippingStatus.setText("待支付");
                break;
            case 2:
                shippingStatus.setText("待发货");
                break;
            case 3:
                shippingStatus.setText("已完成");
                break;
            case 5:
                shippingStatus.setText("已发货");
                break;
            case 8:
                shippingStatus.setText("已完成");
                break;
            default:
                shippingStatus.setText("异常");
        }
    }


    //获取兑换商品订单详情
    void getExchangeDetail(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(ExchangeDetailActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(String.format(getString(R.string.url),API.API_GET_EXCHANGE_DETAIL,tradeNo))
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .method(Request.Method.GET)
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(20000)
                .setResponseHandler(new BtwVolley.ResponseHandler<ExchangeDetailModel>() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onResponse(ExchangeDetailModel resp) {
                        exchangeEntity = resp.data;
                        refreshUI(resp.data);
                    }

                    @Override
                    public void onBtwError(BtwRespError<ExchangeDetailModel> error) {
                        UIUtils.toast(error.errorMessage);
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        UIUtils.toast(getString(R.string.network_error));
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(ExchangeDetailModel.class);
    }

}
