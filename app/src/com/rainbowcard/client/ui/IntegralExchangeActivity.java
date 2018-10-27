package com.rainbowcard.client.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.rainbowcard.client.model.AddressEntity;
import com.rainbowcard.client.model.AddressModel;
import com.rainbowcard.client.model.ExchangeOrderModel;
import com.rainbowcard.client.model.HotGoodEntity;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.StringUtil;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.squareup.picasso.Picasso;

import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017-12-6.
 */
public class IntegralExchangeActivity extends MyBaseActivity {

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;

    @InjectView(R.id.select_layout)
    RelativeLayout selectLayout;
    @InjectView(R.id.addr_layout)
    RelativeLayout addrLayout;

    @InjectView(R.id.name)
    TextView name;
    @InjectView(R.id.number)
    TextView number;
    @InjectView(R.id.address)
    TextView address;

    @InjectView(R.id.goods_img)
    ImageView goodsImg;
    @InjectView(R.id.goods_name)
    TextView goodsName;
    @InjectView(R.id.goods_integral)
    TextView goodsIntegral;

    @InjectView(R.id.exchange_btn)
    Button exchangeBtn;

    HotGoodEntity goodEntity;
    AddressEntity addressEntity;

    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integral_exchange);
        ButterKnife.inject(this);
        goodEntity = (HotGoodEntity) getIntent().getSerializableExtra(Constants.KEY_GOODS_ENTITY);
        load();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.integral_exchange_title));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        mHeadControlPanel.setMiddleTitle(goodEntity.goodsName);


        if (TextUtils.isEmpty(goodEntity.goodsImg)) {
            goodsImg.setImageResource(R.drawable.detail_default);
        } else {
            Picasso.with(IntegralExchangeActivity.this)
                    .load(StringUtil.getUrl(goodEntity.goodsImg,IntegralExchangeActivity.this))
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.detail_default)
                    .error(R.drawable.detail_default).into(goodsImg);
        }

        goodsName.setText(goodEntity.goodsName);
        goodsIntegral.setText(String.format(getString(R.string.integral),goodEntity.integral));

        exchangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addressEntity != null) {
                    if (goodEntity.integral > goodEntity.userIntegral) {
                        showDialog("温馨提示", "", goodEntity.integral - goodEntity.userIntegral, 2);
                    } else {
                        showDialog("温馨提示", "", goodEntity.integral, 3);
                    }
                }else {
                    showDialog("温馨提示", "请选择配送地址", 0, 1);
                }
            }
        });

        selectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntegralExchangeActivity.this,AddressActivity.class);
                startActivityForResult(intent,Constants.REQUEST_ADDRESS);
            }
        });

    }
    void refreshUI(){
        addrLayout.setVisibility(View.VISIBLE);
        name.setText(addressEntity.userName);
        number.setText(addressEntity.phone);
        address.setText(addressEntity.province+addressEntity.city+addressEntity.area+addressEntity.addr);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.REQUEST_ADDRESS && data != null){
            addressEntity = (AddressEntity) data.getSerializableExtra(Constants.KEY_ADDRESS_ENTITY);
            refreshUI();
        }
    }

    void load(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(IntegralExchangeActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(API.API_GET_ADDRESS_LIST)
                .method(Request.Method.GET)
                .setHeader("Authorization", token)
                .setHeader("Accept", API.VERSION)
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<AddressModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();;
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(AddressModel resp) {
                        if(resp.data != null && !resp.data.isEmpty()) {
                            for (int i = 0;i < resp.data.size();i++){
                                if(resp.data.get(i).isDefault == Constants.IS_CHIEFLY){
                                    addressEntity = resp.data.get(i);
                                    refreshUI();
                                }
                            }
                        }
                    }

                    @Override
                    public void onBtwError(BtwRespError<AddressModel> error) {
                        UIUtils.toast(error.errorMessage);
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        UIUtils.toast(getString(R.string.network_error));
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(AddressModel.class);

    }

    //兑换商品
    void exchange(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(IntegralExchangeActivity.this, Constants.USERINFO, Constants.UID));

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("goods_id",goodEntity.id);
        parameters.put("addr_id",addressEntity.id);
        parameters.put("pay_type","");

        withBtwVolley().load(API.API_EXCHANGE_GOODS)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("goods_id",goodEntity.id)
                .setParam("addr_id",addressEntity.id)
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
//                        showDialog("温馨提示","兑换成功，可在兑换记录中查看详情",0,1);
                        Intent intent = new Intent(IntegralExchangeActivity.this,ExchangeOrderSucceedActivity.class);
                        startActivity(intent);
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
        final Dialog dialog = new Dialog(IntegralExchangeActivity.this,R.style.loading_dialog);
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
