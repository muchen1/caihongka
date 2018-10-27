package com.rainbowcard.client.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.common.PayActivity;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.OrderModel;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.TimeUtil;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;

import java.text.DecimalFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.iwgang.countdownview.CountdownView;

/**
 * Created by gc on 2017-3-24.
 */
public class RechargeOrderDetailActivity extends MyBaseActivity{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;

    @InjectView(R.id.status)
    TextView statusTv;
    @InjectView(R.id.shop_type)
    TextView shopType;
    @InjectView(R.id.price)
    TextView priceTv;
    @InjectView(R.id.discount)
    TextView discountTv;
    @InjectView(R.id.pay)
    TextView payTv;
    @InjectView(R.id.need_pay)
    TextView needPayTv;
    @InjectView(R.id.pay_type)
    TextView payType;
    @InjectView(R.id.order)
    TextView orderTv;
    @InjectView(R.id.date)
    TextView dateTv;
    @InjectView(R.id.countdown)
    CountdownView countdown;
    @InjectView(R.id.cancel_order)
    TextView cancelOrder;
    @InjectView(R.id.pay_order)
    TextView payOrder;
    @InjectView(R.id.no_pay_layout)
    LinearLayout noPayLayout;
    @InjectView(R.id.user_layout)
    RelativeLayout userLayout;
    @InjectView(R.id.name)
    TextView nameTv;
    @InjectView(R.id.phone)
    TextView phoneTv;
    @InjectView(R.id.address)
    TextView addrTv;
    @InjectView(R.id.yuci)
    TextView yuciTv;
    @InjectView(R.id.service_text)
    TextView serviceTv;

    @InjectView(R.id.give_text)
    TextView giveText;
    @InjectView(R.id.give_price)
    TextView givePrice;

    OrderModel.OrderEntity orderEntity;

    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_order_detail);
        ButterKnife.inject(this);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(RechargeOrderDetailActivity.this, Constants.USERINFO, Constants.UID));
        UIUtils.setMiuiStatusBarDarkMode(RechargeOrderDetailActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(RechargeOrderDetailActivity.this,true);
        orderEntity = (OrderModel.OrderEntity) getIntent().getSerializableExtra(Constants.KEY_ORDER_MODEL);
        initView();
    }
    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.order_detail));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        switch (orderEntity.status){
            case 1:
                statusTv.setText("待支付");
                noPayLayout.setVisibility(View.VISIBLE);
                needPayTv.setText("需付款：");
                break;
            case 2:
                statusTv.setText("已支付");
                noPayLayout.setVisibility(View.GONE);
                needPayTv.setText("实付款：");
                break;
        }


        if(!TextUtils.isEmpty(orderEntity.cardTypeText)){
            if(orderEntity.cardTypeText.equals("办卡")){
                userLayout.setVisibility(View.VISIBLE);
                yuciTv.setText("卡面金额：");
                serviceTv.setText("服务类型：");
                shopType.setText(orderEntity.cardTypeText);
                givePrice.setVisibility(View.GONE);
                giveText.setVisibility(View.GONE);
            }else {
                userLayout.setVisibility(View.GONE);
                yuciTv.setText("充值金额：");
                serviceTv.setText("充值账户：");
                if(orderEntity.cardTypeText.equals("账户余额")){
                    if(orderEntity.rebateMoney > 0) {
                        givePrice.setVisibility(View.VISIBLE);
                        giveText.setVisibility(View.VISIBLE);
                    }else {
                        givePrice.setVisibility(View.GONE);
                        giveText.setVisibility(View.GONE);
                    }
                    shopType.setText(orderEntity.cardTypeText);
                }else {
                    givePrice.setVisibility(View.GONE);
                    giveText.setVisibility(View.GONE);
                    shopType.setText(orderEntity.cardTypeText+orderEntity.cardNumber);
                }
                givePrice.setText(String.format(getString(R.string.price),new
                        DecimalFormat("##,###,###,###,##0.00").format(orderEntity.rebateMoney)));
            }
        }



        nameTv.setText(orderEntity.userName);
        phoneTv.setText(orderEntity.phone);
        addrTv.setText(orderEntity.address);
        if(!TextUtils.isEmpty(String.valueOf(orderEntity.money))){
            priceTv.setText(String.format(getString(R.string.price),new
                    DecimalFormat("##,###,###,###,##0.00").format(orderEntity.money)));
        }
        if(!TextUtils.isEmpty(String.valueOf(orderEntity.payMoney))){
            payTv.setText(String.format(getString(R.string.price),new
                    DecimalFormat("##,###,###,###,##0.00").format(orderEntity.payMoney)));
        }

        discountTv.setText(String.format(getString(R.string.sub_price),new
                DecimalFormat("##,###,###,###,##0.00").format(orderEntity.couponMoney)));

        if(!TextUtils.isEmpty(orderEntity.time)){
            dateTv.setText(orderEntity.time);
        }
        if(!TextUtils.isEmpty(orderEntity.tradeNo)){
            orderTv.setText(orderEntity.tradeNo);
        }
        if(!TextUtils.isEmpty(orderEntity.payType)){
            payType.setText(orderEntity.payType);
        }

        if(!TextUtils.isEmpty(orderEntity.expire)) {
            countdown.start(TimeUtil.countDownTime(orderEntity.expire));
        }
        countdown.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                finish();
            }
        });

        payOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RechargeOrderDetailActivity.this, PayActivity.class);
                if(orderEntity.cardTypeText.equals("账户余额")){
                    intent.putExtra(Constants.KEY_RAINBOW_TYPE, Constants.RAINBOW_RECHARGE_ACCOUNT);
                }
                if(orderEntity.cardTypeText.equals("办卡")){
                    intent.putExtra(Constants.KEY_RAINBOW_TYPE,Constants.RAINBOW_BUY);
                }
                if(orderEntity.cardTypeText.equals("彩虹卡")){
                    intent.putExtra(Constants.KEY_RAINBOW_TYPE, Constants.RAINBOW_RECHARGE);
                }
                intent.putExtra(Constants.KEY_ORDER_MODEL,orderEntity);
                startActivity(intent);
            }
        });

        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("温馨提示","订单取消将无法恢复",orderEntity.tradeNo);
            }
        });
    }

    private void showDialog(String title, String tip,final String tradeNo) {
        final Dialog dialog = new Dialog(RechargeOrderDetailActivity.this,R.style.loading_dialog);
        dialog.setContentView(R.layout.ui_alert_dialog);
        Window dialogWindow = dialog.getWindow();
        Display display = getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = -80;
        lp.width = (int)(display.getWidth() * 0.95);
        dialogWindow.setAttributes(lp);

        TextView alertTitle = (TextView) dialog.findViewById(R.id.alert_title);
        TextView alertTip = (TextView) dialog.findViewById(R.id.alert_tip);
        TextView alertOk = (TextView) dialog.findViewById(R.id.alert_ok);
        TextView alertCancle = (TextView) dialog.findViewById(R.id.alert_cancel);
        alertTitle.setText(title);
        alertTip.setText(tip);
        alertOk.setText("继续");
        alertCancle.setText("关闭");
        dialog.findViewById(R.id.alert_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrder(tradeNo);
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

    //取消订单
    void cancelOrder(String tradeNo){
        withBtwVolley().load(String.format(getString(R.string.url), API.API_DELETE_ORDER,tradeNo))
                .method(Request.Method.POST)
                .setHeader("Authorization",token)
                .setHeader("Accept", "application/vnd.caihongka.v2.1.0+json")
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<String>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(String resp) {
                        UIUtils.toast("订单已取消");
                        finish();
                    }

                    @Override
                    public void onBtwError(BtwRespError<String> error) {
                        Toast.makeText(RechargeOrderDetailActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(RechargeOrderDetailActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute();
    }
}
