package com.rainbowcard.client.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
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
import com.rainbowcard.client.common.PayActivity;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.OrderModel;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;

import java.text.DecimalFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2016-6-23.
 */
public class PayStatusActivity extends MyBaseActivity {

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.text)
    TextView payStatus;
    @InjectView(R.id.pay_image)
    ImageView payIv;
    @InjectView(R.id.info)
    TextView infoTv;
//    @InjectView(R.id.tv_name)
//    TextView nameTv;
//    @InjectView(R.id.tv_phone)
//    TextView phoneTv;
//    @InjectView(R.id.tv_addr)
//    TextView addrTv;
//    @InjectView(R.id.tv_limit)
//    TextView limitTv;
//    @InjectView(R.id.tv_price)
//    TextView priceTv;
    @InjectView(R.id.confirm)
    Button confirmBtn;
    @InjectView(R.id.detail_btn)
    Button detailBtn;
    private int status;

//    private String rechargeNum;
//    private String rechargeName;
//    private String rechargePhone;
//    private String rechargeAddr;
//    private int rechargePrice;
    private OrderModel.OrderEntity orderEntity;

    private int rainbowType;
    private String cityId;



    private int isReturnFreeWashOrder;  //充值完成后是否返回免费洗车券下单   为1返回

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_status);
        ButterKnife.inject(this);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        UIUtils.setMiuiStatusBarDarkMode(PayStatusActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(PayStatusActivity.this,true);
        status = getIntent().getIntExtra(Constants.KEY_PAY_STATUS,0);
//        rechargeNum = getIntent().getStringExtra(Constants.KEY_RECHARGE_NO);
//        rechargeName = getIntent().getStringExtra(Constants.KEY_NAME);
//        rechargePhone = getIntent().getStringExtra(Constants.KEY_PHONE);
//        rechargeAddr = getIntent().getStringExtra(Constants.KEY_ADDR);
//        rechargePrice = getIntent().getIntExtra(Constants.KEY_PRICE,100);
        orderEntity = (OrderModel.OrderEntity) getIntent().getSerializableExtra(Constants.KEY_ORDER_MODEL);
        rainbowType = getIntent().getIntExtra(Constants.KEY_RAINBOW_TYPE,0);
        cityId = getIntent().getStringExtra(Constants.KEY_CITY_ID);
        isReturnFreeWashOrder = getIntent().getIntExtra(Constants.KEY_IS_RETURN_FREE_WACH_ORDER,0);
        initView();
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
                if(isReturnFreeWashOrder == 1){
                    Intent intent = new Intent(PayStatusActivity.this, FreeWashOrderActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        });

        switch (rainbowType){
            case 0:
                refreRecharge();
                break;
            case 1:
                refreBuy();
                break;
            case 2:
                refreAccountUI();
                break;
        }

//        if(status == 0){
//            payStatus.setText("支付失败");
//            payIv.setImageResource(R.drawable.user_zfsb);
//        }else {
//            payStatus.setText("支付成功");
//            payIv.setImageResource(R.drawable.user_zfcg);
//        }

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                Log.d("GCCCCCCCCC","????????"+rainbowType);
                if(isReturnFreeWashOrder == 1){
                    intent = new Intent(PayStatusActivity.this, FreeWashOrderActivity.class);
                    startActivity(intent);
                }else {
                    switch (rainbowType) {
                        case 0:
                            intent = new Intent(PayStatusActivity.this, MyCardActivity.class);
                            startActivity(intent);
                            break;
                        case 2:
                            intent = new Intent(PayStatusActivity.this, MainActivity.class);
                            intent.putExtra(Constants.KEY_CITY_ID, cityId);
                            startActivity(intent);
                            finish();
                            break;
                    }
                }
                finish();
            }
        });
        detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(PayStatusActivity.this,RechargeOrderDetailActivity.class);
//                intent.putExtra(Constants.KEY_ORDER_MODEL,orderEntity);
//                startActivity(intent);
//                finish();
                getOrderDetail(orderEntity.tradeNo);
            }
        });
        /*goPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status == 1){
//                    MainActivity.getInstance().refreshBottomPanel(2,false);
                    finish();
                }else {
                    Intent intent = new Intent(PayStatusActivity.this, PayActivity.class);
                    intent.putExtra(Constants.KEY_TENPAN,tenPay);
                    intent.putExtra(Constants.KEY_RECHARGE_NO,mGoodsName);
                    intent.putExtra(Constants.KEY_AMOUNT,mPoints);
                    startActivity(intent);
                    finish();
                }
            }
        });*/
    }

    void refreRecharge(){
        payStatus.setText("已支付完成，充值成功");
        View view = ((ViewStub)findViewById(R.id.sub_recharge)).inflate();
        TextView cardTv = (TextView) view.findViewById(R.id.tv_card);
        TextView priceTv = (TextView) view.findViewById(R.id.tv_goods_money);
        TextView phoneTv = (TextView) view.findViewById(R.id.tv_phone);
        TextView nameTv = (TextView) view.findViewById(R.id.tv_name);
        TextView disconutTv = (TextView) view.findViewById(R.id.tv_discount);
        TextView moneyTv = (TextView) view.findViewById(R.id.tv_money);
        cardTv.setText(String.format(getString(R.string.rainbow_no),orderEntity.cardNumber));
        nameTv.setText(orderEntity.userName);
        phoneTv.setText(orderEntity.phone);
        priceTv.setText(String.format(getString(R.string.price),new
                DecimalFormat("##,###,###,###,##0.00").format(orderEntity.money)));
        disconutTv.setText(String.format(getString(R.string.sub_price),new
                DecimalFormat("##,###,###,###,##0.00").format(orderEntity.couponMoney)));
        moneyTv.setText(String.format(getString(R.string.price),new
                DecimalFormat("##,###,###,###,##0.00").format(orderEntity.money-orderEntity.couponMoney)));
    }
    void refreBuy(){
        payStatus.setText("已支付完成，购卡成功");
        infoTv.setVisibility(View.VISIBLE);
        View view = ((ViewStub)findViewById(R.id.sub_buy_status)).inflate();
        TextView nameTv = (TextView) view.findViewById(R.id.tv_name);
        TextView phoneTv = (TextView) view.findViewById(R.id.tv_phone);
        TextView addrTv = (TextView) view.findViewById(R.id.tv_addr);
        TextView limitTv = (TextView) view.findViewById(R.id.tv_limit);
        TextView disconutTv = (TextView) view.findViewById(R.id.tv_discount);
        TextView moneyTv = (TextView) view.findViewById(R.id.tv_money);
        nameTv.setText(orderEntity.userName);
        phoneTv.setText(orderEntity.phone);
        addrTv.setText(orderEntity.address);
        limitTv.setText(String.format(getString(R.string.price),new
                DecimalFormat("##,###,###,###,##0.00").format(orderEntity.money)));
        disconutTv.setText(String.format(getString(R.string.sub_price),new
                DecimalFormat("##,###,###,###,##0.00").format(orderEntity.couponMoney)));
        moneyTv.setText(String.format(getString(R.string.price),new
                DecimalFormat("##,###,###,###,##0.00").format(orderEntity.money-orderEntity.couponMoney)));
//        priceTv.setText(String.format(getString(R.string.recharge_price),orderEntity.money));
    }

    //显示充值账户UI
    void refreAccountUI(){
        payStatus.setText("已支付完成，充值成功");
        View view = ((ViewStub)findViewById(R.id.sub_recharge_account)).inflate();
        TextView limitTv = (TextView) view.findViewById(R.id.tv_limit);
        TextView disconutTv = (TextView) view.findViewById(R.id.tv_discount);
        TextView moneyTv = (TextView) view.findViewById(R.id.tv_money);
        TextView givePrice = (TextView) view.findViewById(R.id.give_price);
        TextView giveTv = (TextView) view.findViewById(R.id.give_text);
        limitTv.setText(String.format(getString(R.string.price),new
                DecimalFormat("##,###,###,###,##0.00").format(orderEntity.money)));
        disconutTv.setText(String.format(getString(R.string.sub_price),new
                DecimalFormat("##,###,###,###,##0.00").format(orderEntity.couponMoney)));
        moneyTv.setText(String.format(getString(R.string.price),new
                DecimalFormat("##,###,###,###,##0.00").format(orderEntity.money-orderEntity.couponMoney)));
        givePrice.setText(String.format(getString(R.string.price),new
                DecimalFormat("##,###,###,###,##0.00").format(orderEntity.rebateMoney)));
        if(orderEntity.rebateMoney == 0.0){
            giveTv.setVisibility(View.GONE);
            givePrice.setVisibility(View.GONE);
        }else {
            giveTv.setVisibility(View.VISIBLE);
            givePrice.setVisibility(View.VISIBLE);
        }
//        priceTv.setText(String.format(getString(R.string.recharge_price),orderEntity.money));

    }

    //获取订单详情
    void getOrderDetail(String tradeNo){
        String token = String.format(getString(R.string.token), MyConfig.getSharePreStr(PayStatusActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(String.format(getString(R.string.url), API.API_GET_USER_RECHARGE_DETAIL,tradeNo))
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", "application/vnd.caihongka.v2.1.0+json")
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
                        Intent intent = new Intent(PayStatusActivity.this,RechargeOrderDetailActivity.class);
                        intent.putExtra(Constants.KEY_ORDER_MODEL,resp.data);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onBtwError(BtwRespError<OrderModel> error) {
                        Toast.makeText(PayStatusActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(PayStatusActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
//                        getOrder();
                    }
                }).excute(OrderModel.class);
    }
}
