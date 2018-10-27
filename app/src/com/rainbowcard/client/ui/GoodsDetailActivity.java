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

import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017-12-6.
 */
public class GoodsDetailActivity extends MyBaseActivity {

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
    @InjectView(R.id.goods_info)
    TextView goodsInfo;
    @InjectView(R.id.exchange_integral)
    TextView exchangeIntegral;
    @InjectView(R.id.exchange_btn)
    TextView exchangeBtn;

    String goodsId;
    int goodsType;
    HotGoodEntity goodEntity;

    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_detail);
        ButterKnife.inject(this);
        goodsId = getIntent().getStringExtra(Constants.KEY_GOODS_ID);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getGoodsDetail();
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
    void refreshUI(final HotGoodEntity goodEntity){

        mHeadControlPanel.setMiddleTitle(goodEntity.goodsName);
            if (TextUtils.isEmpty(goodEntity.goodsImg)) {
                goodsImg.setImageResource(R.drawable.detail_default);
            } else {
                Picasso.with(GoodsDetailActivity.this)
                        .load(StringUtil.getUrl(goodEntity.goodsImg,GoodsDetailActivity.this))
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.detail_default)
                        .error(R.drawable.detail_default).into(goodsImg);
            }

        goodsName.setText(goodEntity.goodsName);
        goodsIntegral.setText(String.format(getString(R.string.integral),goodEntity.integral));
        exchangeIntegral.setText(goodEntity.integral+"");
        goodsInfo.setText(Html.fromHtml(goodEntity.goodsInfo));

        exchangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(goodEntity.goodsType == 3){
                    Intent intent = new Intent(GoodsDetailActivity.this,IntegralExchangeActivity.class);
                    intent.putExtra(Constants.KEY_GOODS_ENTITY,goodEntity);
                    startActivity(intent);
                }else {
                    if (goodEntity.integral > goodEntity.userIntegral) {
                        showDialog("温馨提示","",goodEntity.integral -goodEntity.userIntegral,2);
                    }else {
                        showDialog("温馨提示","",goodEntity.integral,3);
                    }
                }
            }
        });

        if(goodEntity.showBut){
            exchangeBtn.setBackgroundResource(R.drawable.buy_select_item);
            exchangeBtn.setClickable(true);
        }else {
            exchangeBtn.setBackgroundColor(getResources().getColor(R.color.app_gray));
            exchangeBtn.setClickable(false);
        }
    }


    //获取兑换商品详情
    void getGoodsDetail(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(GoodsDetailActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(String.format(getString(R.string.url),API.API_GET_GOODS_DETAIL,goodsId))
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .method(Request.Method.GET)
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(20000)
                .setResponseHandler(new BtwVolley.ResponseHandler<GoodDetailModel>() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onResponse(GoodDetailModel resp) {
                        goodEntity = resp.data;
                        if(goodEntity != null) {
                            goodsType = goodEntity.goodsType;
                            refreshUI(resp.data);
                        }
                    }

                    @Override
                    public void onBtwError(BtwRespError<GoodDetailModel> error) {
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
                }).excute(GoodDetailModel.class);
    }

    //兑换商品
    void exchange(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(GoodsDetailActivity.this, Constants.USERINFO, Constants.UID));

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("goods_id",goodsId);
        parameters.put("addr_id","");
        parameters.put("pay_type","");

        withBtwVolley().load(API.API_EXCHANGE_GOODS)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("goods_id",goodsId)
                .setParam("addr_id","")
                .setParam("pay_type","")
                .setParam("paramSign",StringUtil.createParameterSign(parameters))
                .method(Request.Method.POST)
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(20000)
                .setResponseHandler(new BtwVolley.ResponseHandler<ExchangeOrderModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(ExchangeOrderModel resp) {
                        if(goodsType == 1 || goodsType == 2){
                            showDialog("温馨提示","兑换成功，请在优惠券查看",0,1);
                        }else {
                            showDialog("温馨提示","兑换成功，可在兑换记录中查看详情",0,1);
                        }
                    }

                    @Override
                    public void onBtwError(BtwRespError<ExchangeOrderModel> error) {
                        showDialog("温馨提示",error.errorMessage,0,1);
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        UIUtils.toast(getString(R.string.network_error));
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(ExchangeOrderModel.class);
    }



    private void showDialog(String title, String tip, int integral,int type) {
        final Dialog dialog = new Dialog(GoodsDetailActivity.this,R.style.loading_dialog);
        dialog.setContentView(R.layout.ui_ios_alert_dialog);
        Window dialogWindow = dialog.getWindow();
        Display display = getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = -80;
        lp.width = (int)(display.getWidth() * 0.90);
        dialogWindow.setAttributes(lp);

        TextView alertTitle = (TextView) dialog.findViewById(R.id.alert_title);
        TextView alertTip = (TextView) dialog.findViewById(R.id.alert_tip);
        TextView alertOk = (TextView) dialog.findViewById(R.id.alert_ok);
        TextView alertCancle = (TextView) dialog.findViewById(R.id.alert_cancel);
        ImageView line = (ImageView) dialog.findViewById(R.id.line);

        //type  1 我知道了  2差额   3可以兑换
        if(type == 1){
            line.setVisibility(View.GONE);
            alertCancle.setText("我知道啦");
        }

        switch (type){
            case 1:
                alertTitle.setText(title);
                alertTip.setText(tip);
                line.setVisibility(View.GONE);
                alertOk.setVisibility(View.GONE);
                alertCancle.setText("我知道啦");
                break;
            case 2:
                alertTitle.setText(title);
                alertTip.setText(String.format(getString(R.string.little_integral),integral));
                Spannable span = new SpannableString(alertTip.getText());
                span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.money_color)), 4, 4 + String.valueOf(integral).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                alertTip.setText(span);
                line.setVisibility(View.GONE);
                alertOk.setVisibility(View.GONE);
                alertCancle.setText("我知道啦");
                break;
            case 3:
                alertTitle.setText(title);
                alertTip.setText(String.format(getString(R.string.integral_exchange),integral));
                Spannable span2 = new SpannableString(alertTip.getText());
                span2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.money_color)), 4, 4 + String.valueOf(integral).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                alertTip.setText(span2);
                line.setVisibility(View.VISIBLE);
                alertOk.setVisibility(View.VISIBLE);
                alertOk.setText("确定");
                alertCancle.setText("取消");
                break;
        }

//        alertOk.setText("继续");
//        alertCancle.setText("取消");
        dialog.findViewById(R.id.alert_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exchange();
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.alert_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
