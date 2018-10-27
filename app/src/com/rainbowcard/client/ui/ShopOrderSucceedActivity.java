package com.rainbowcard.client.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.rainbowcard.client.model.CardModel;
import com.rainbowcard.client.model.OrderModel;
import com.rainbowcard.client.model.ShopOrderSucceedModel;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.widget.LoadingFrameLayout;

import java.text.DecimalFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017-1-18.
 */
public class ShopOrderSucceedActivity extends MyBaseActivity{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.shop_name)
    TextView shopNameTv;
    @InjectView(R.id.service)
    TextView serviceTv;
    @InjectView(R.id.order_price)
    TextView orderPriceTv;
    @InjectView(R.id.pay_price)
    TextView payPriceTv;
    @InjectView(R.id.code)
    TextView codeTv;
    @InjectView(R.id.fulfill_btn)
    Button fulfillBtn;
    @InjectView(R.id.detail_btn)
    Button detailBtn;
    @InjectView(R.id.coupon_price)
    TextView couponPrice;
    @InjectView(R.id.v_frame)
    LoadingFrameLayout mFlLoading;

    @InjectView(R.id.finance_image)
    ImageView financeIv;

    String token;
    String tradeNo;
    boolean isFree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_order_succeed);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(ShopOrderSucceedActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(ShopOrderSucceedActivity.this,true);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(ShopOrderSucceedActivity.this, Constants.USERINFO, Constants.UID));
        tradeNo = getIntent().getStringExtra(Constants.KEY_TRADE_NO);
        isFree = getIntent().getBooleanExtra(Constants.KEY_IS_FREE,false);
        initView();
        getOrderSucceed();
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.recharge_result));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fulfillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (isFree){
                    intent = new Intent(ShopOrderSucceedActivity.this,FreeWashTicketActivity.class);
                }else {
                    intent = new Intent(ShopOrderSucceedActivity.this,MainActivity.class);
                }
                startActivity(intent);
                finish();
            }
        });

        detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOrderDetail(tradeNo);
            }
        });

        if(isFree){
            financeIv.setVisibility(View.GONE);
        }else {
            financeIv.setVisibility(View.VISIBLE);
        }

        financeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopOrderSucceedActivity.this,FreeWashTicketEntranceActivity.class);
//                Intent intent = new Intent(ShopOrderSucceedActivity.this,InviteActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    void refreshUI(ShopOrderSucceedModel.ShopOrderSucceedEntity shopOrderSucceedEntity){
        shopNameTv.setText(shopOrderSucceedEntity.shopName);
        if(TextUtils.isEmpty(shopOrderSucceedEntity.serviceSonTypeText)){
            serviceTv.setText(shopOrderSucceedEntity.serviceTypeText);
        }else {
            serviceTv.setText(String.format(getString(R.string.service_text), shopOrderSucceedEntity.serviceTypeText, shopOrderSucceedEntity.serviceSonTypeText));
        }
        orderPriceTv.setText(String.format(getString(R.string.price),new
                DecimalFormat("##,###,###,###,##0.00").format(shopOrderSucceedEntity.money)));
        payPriceTv.setText(String.format(getString(R.string.price),new
                DecimalFormat("##,###,###,###,##0.00").format(shopOrderSucceedEntity.payMoney)));
        couponPrice.setText(String.format(getString(R.string.sub_price),new
                DecimalFormat("##,###,###,###,##0.00").format(shopOrderSucceedEntity.couponMoney)));
        codeTv.setText(shopOrderSucceedEntity.code);
    }

    void getOrderDetail(String tradeNo){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(ShopOrderSucceedActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(API.API_GET_PAYMENT_DETAIL)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("trade_no",tradeNo)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<OrderModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(OrderModel resp) {
                        Intent intent = new Intent(ShopOrderSucceedActivity.this,OrderDetailActivity.class);
                        intent.putExtra(Constants.KEY_ORDER_MODEL,resp.data);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onBtwError(BtwRespError<OrderModel> error) {
                        Toast.makeText(ShopOrderSucceedActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(ShopOrderSucceedActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(OrderModel.class);
    }

    void getOrderSucceed(){
        withBtwVolley().load(API.API_GET_PAYMENT_INFO)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("trade_no",tradeNo)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<ShopOrderSucceedModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(ShopOrderSucceedModel resp) {
                        refreshUI(resp.data);
                    }

                    @Override
                    public void onBtwError(BtwRespError<ShopOrderSucceedModel> error) {
                        mFlLoading.showError(error.errorMessage,false);
                        Toast.makeText(ShopOrderSucceedActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        mFlLoading.showError("订单异常，请联系客服400-825-7788",false);
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
//                        getOrderSucceed();
                    }
                }).excute(ShopOrderSucceedModel.class);
    }
}
