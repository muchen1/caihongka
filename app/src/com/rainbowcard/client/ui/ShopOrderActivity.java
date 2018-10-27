package com.rainbowcard.client.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.android.volley.Request;
import com.google.gson.Gson;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.common.model.PayResult;
import com.rainbowcard.client.common.model.PayType;
import com.rainbowcard.client.common.utils.DLog;
import com.rainbowcard.client.common.utils.ViewUtils;
import com.rainbowcard.client.model.CardEntity;
import com.rainbowcard.client.model.CardModel;
import com.rainbowcard.client.model.DiscountEntity;
import com.rainbowcard.client.model.DiscountModel;
import com.rainbowcard.client.model.OrderModel;
import com.rainbowcard.client.model.PayModel;
import com.rainbowcard.client.model.PaymentModel;
import com.rainbowcard.client.model.WXPayData;
import com.rainbowcard.client.ui.adapter.ShopPayTypeAdapter;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.StringUtil;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.utils.Validation;
import com.rainbowcard.client.utils.alipay.MobileSecurePayer;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.wxapi.model.WXPayEvent;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by gc on 2017-1-10.
 */
public class ShopOrderActivity extends MyBaseActivity{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.shop_name)
    TextView shopNameTv;
    @InjectView(R.id.service_name)
    TextView serviceNameTv;
    @InjectView(R.id.order_money)
    TextView orderMoneyTv;
    @InjectView(R.id.pay_money)
    TextView payMoneyTv;
    @InjectView(R.id.pay_layout)
    RelativeLayout paySelectLayout;
    @InjectView(R.id.pay_type)
    TextView payTypeTv;
    @InjectView(R.id.pay_button)
    TextView payButton;
    @InjectView(R.id.discount_status)
    TextView discountStatus;
    @InjectView(R.id.discount_layout)
    RelativeLayout discountLayout;
    @InjectView(R.id.lv_pay_method)
    ListView mLvPayMethod;
    @InjectView(R.id.iv_checked)
    ImageView checkedIv;

    String token;

    private final static int PAY_CASH_MODE_ALI_BOTH = 2;
    private final static int PAY_CASH_MODE_WECHAT = 3;

    private ArrayList<DiscountEntity> discountEntities = new ArrayList<DiscountEntity>();
    private DiscountEntity discountEntity ;

    private ShopPayTypeAdapter mPayTypeAdapter;
    private MobileSecurePayer mAliPay;

    private Bundle bundle;
    private OrderModel.OrderEntity orderEntity ;

    private IWXAPI mWXApi;
    private EventBus mEventBus;

    int paymentType = 1;
    String cardId;
    List<CardEntity> cardEntities = new ArrayList<CardEntity>();

    String defaultNo;

    String shopId;
    int serviceType;
    int sonServiceType;
    String serviceName;
    String shopName;
    int serviceMoney;
    String activityMoney;
    private int codeId = 0;

    private static final int SDK_PAY_FLAG = 1;
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        paySuccess(orderEntity.tradeNo);
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            paySuccess(orderEntity.tradeNo);
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            payFail();
                            discountLayout.setClickable(false);
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_order);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(ShopOrderActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(ShopOrderActivity.this,true);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(ShopOrderActivity.this, Constants.USERINFO, Constants.UID));
        defaultNo = MyConfig.getSharePreStr(ShopOrderActivity.this, Constants.USERINFO, Constants.DEFAULT_NO);
        mEventBus = EventBus.getDefault();
        mEventBus.register(this);

        mAliPay = new MobileSecurePayer(this);

        getMyCard();
        initData();
        mPayTypeAdapter = new ShopPayTypeAdapter(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        defaultNo = MyConfig.getSharePreStr(ShopOrderActivity.this, Constants.USERINFO, Constants.DEFAULT_NO);
    }

    void initData(){
        shopId = getIntent().getStringExtra(Constants.KEY_SHOP_ID);
        serviceType = getIntent().getIntExtra(Constants.KEY_SERVICE_TYPE,1);
        sonServiceType = getIntent().getIntExtra(Constants.KEY_SON_SERVICE_TYPE,1);
        shopName = getIntent().getStringExtra(Constants.KEY_SHOP_NAME);
        serviceName = getIntent().getStringExtra(Constants.KEY_SERVICE_NAME);
        activityMoney = getIntent().getStringExtra(Constants.KEY_ACTIVITY_MONEY);
        serviceMoney = getIntent().getIntExtra(Constants.KEY_SERVICE_MONEY,0);
        orderEntity = (OrderModel.OrderEntity) getIntent().getSerializableExtra(Constants.KEY_ORDER_MODEL);

//        if(orderEntity == null || TextUtils.isEmpty(activityMoney)) {
//            if(TextUtils.isEmpty(activityMoney)) {
//                getCoupon();
//            }else {
//                discountStatus.setText("暂无可用优惠券");
//                discountLayout.setClickable(false);
//            }
//        }else {
//            discountStatus.setText("暂无可用优惠券");
//            discountLayout.setClickable(false);
//        }
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.pay));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initPayType();
        mLvPayMethod.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPayTypeAdapter.setCurrentItem(position);
                switch (position){
                    case 0:
                        paymentType = 3;
                        break;
                    case 1:
                        paymentType = 4;
                        break;

                }
                checkedIv.setImageResource(R.drawable.moren);
//                startHybridPay();
            }
        });

        if(TextUtils.isEmpty(defaultNo)){
            payTypeTv.setText("账户余额");
            paymentType = 1;
            cardId = "";
        }else {

            if(Validation.isMobile(defaultNo)){
                payTypeTv.setText("账户余额");
                paymentType = 1;
                cardId = "";
            }else {
                payTypeTv.setText(String.format(getString(R.string.rainbow_no),defaultNo));
                paymentType = 2;
            }
        }

        if(orderEntity != null && !TextUtils.isEmpty(orderEntity.shopName)){
            shopNameTv.setText(orderEntity.shopName);
        }else {
            shopNameTv.setText(shopName);
        }
        if(orderEntity != null && !TextUtils.isEmpty(orderEntity.serviceTypeText)){
            serviceNameTv.setText(String.format(getString(R.string.service_text),orderEntity.serviceTypeText,orderEntity.serviceSonTypeText));
        }else {
            serviceNameTv.setText(serviceName);
            /*switch (serviceType) {
                case 1:
                    serviceNameTv.setText("洗车");
                    break;
                case 2:
                    serviceNameTv.setText("镀膜");
                    break;
                case 3:
                    serviceNameTv.setText("打蜡");
                    break;
                case 4:
                    serviceNameTv.setText("内饰清洗");
                    break;
            }*/
        }

        if(orderEntity != null && !TextUtils.isEmpty(String.valueOf(orderEntity.money))){
            orderMoneyTv.setText(String.format(getString(R.string.price),new
                    DecimalFormat("##,###,###,###,##0.00").format(orderEntity.money)));
        }else {
            if(TextUtils.isEmpty(activityMoney)) {
                orderMoneyTv.setText(String.format(getString(R.string.price), new
                        DecimalFormat("##,###,###,###,##0.00").format(serviceMoney)));
            }else {
                orderMoneyTv.setText(String.format(getString(R.string.price), new
                        DecimalFormat("##,###,###,###,##0.00").format(Float.valueOf(activityMoney))));
            }
        }
        if(orderEntity != null && !TextUtils.isEmpty(String.valueOf(orderEntity.payMoney))) {
            payMoneyTv.setText(String.format(getString(R.string.price), new
                    DecimalFormat("##,###,###,###,##0.00").format(orderEntity.payMoney)));
        }else {
            if(TextUtils.isEmpty(activityMoney)) {
                payMoneyTv.setText(String.format(getString(R.string.price), new
                        DecimalFormat("##,###,###,###,##0.00").format(serviceMoney)));
            }else {
                payMoneyTv.setText(String.format(getString(R.string.price), new
                        DecimalFormat("##,###,###,###,##0.00").format(Float.valueOf(activityMoney))));
            }
        }

        paySelectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDialog();
            }
        });

        discountLayout.setClickable(false);
        discountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopOrderActivity.this, UsableDiscountActivity.class);
                intent.putExtra(Constants.KEY_USABLE_DISCOUNT,discountEntities);
                startActivityForResult(intent,Constants.REQUEST_USABLE_DISCOUNT);
            }
        });
        if(orderEntity == null) {
            if(TextUtils.isEmpty(activityMoney)){
                getCoupon();
            }else {
                discountStatus.setText("不可用");
                discountLayout.setClickable(false);
            }
        }else {
            switch (orderEntity.couponMoneyType){
                case 0:
                    discountStatus.setText("未使用");
                    break;
                case 1:
                    discountStatus.setText(String.format(getString(R.string.sub_price), new
                            DecimalFormat("##,###,###,###,##0.00").format(orderEntity.couponMoney)));
                    break;
                case 2:
                    discountStatus.setText(String.format(getString(R.string.sub_break),orderEntity.couponDis));
                    break;
            }
            discountLayout.setClickable(false);
        }

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orderEntity != null && !TextUtils.isEmpty(orderEntity.tradeNo)){
                    payOrder(orderEntity.tradeNo);
                }else {
                    payment();
                }
            }
        });
    }


    private void initPayType() {
        List<PayType> payTypes = new ArrayList<PayType>(2);
        payTypes.add(new PayType(PayType.TYPE_WEIXIN));
        payTypes.add(new PayType(PayType.TYPE_ALIPAY));
        mPayTypeAdapter.setPayTypes(payTypes);

        mLvPayMethod.setAdapter(mPayTypeAdapter);
        ViewUtils.setListViewHeightBasedOnChildren(mLvPayMethod);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_ALIPAY_WAP:
//                checkOrderDetail(mOrderId);
                break;
        }
        if (requestCode == Constants.REQUEST_USABLE_DISCOUNT && data != null) {
            discountEntity = (DiscountEntity) data.getSerializableExtra(Constants.KEY_DISCOUNT_ENTITY);
            if(!TextUtils.isEmpty(discountEntity.title)){
//                if(discountEntity.moneyType == 1) {
//                    discountStatus.setText("-"+discountEntity.money);
//                    payMoneyTv.setText(String.format(getString(R.string.recharge_price), discountEntity.payMoney));
//                }else {
//                    discountStatus.setText(discountEntity.money+"折");
//                    payMoneyTv.setText(String.format(getString(R.string.price),discountEntity.payMoney));
//                }
                if(discountEntity.moneyType == 1) {
                    discountStatus.setText(String.format(getString(R.string.sub_price), new
                            DecimalFormat("##,###,###,###,##0.00").format(discountEntity.couponMoney)));
                }else {
                    discountStatus.setText(String.format(getString(R.string.sub_break),discountEntity.money));
                }
                payMoneyTv.setText(String.format(getString(R.string.price),new
                        DecimalFormat("##,###,###,###,##0.00").format(discountEntity.payMoney)));
                codeId = discountEntity.id;
            }else {
                discountStatus.setText(discountEntities.size()+"张可用");
                payMoneyTv.setText(String.format(getString(R.string.price),new
                        DecimalFormat("##,###,###,###,##0.00").format(serviceMoney)));
                codeId = 0;
            }
        }
    }

    void loadDialog(){
        Dialog dialog = UIUtils.alertButtonListBottom(ShopOrderActivity.this,"",true);
        Window dialogWindow = dialog.getWindow();
        Display display = getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int)(display.getWidth());
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.BOTTOM);
        UIUtils.addButtonToButtonListBottom(dialog, "账户余额", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payTypeTv.setText("账户余额");
                paymentType = 1;
                cardId = "";
                checkedIv.setImageResource(R.drawable.xuanzhong);
                mPayTypeAdapter.setCurrentItem(-1);
            }
        });

        for (int i = 0;i<cardEntities.size();i++){
            final int a = i;
            UIUtils.addButtonToButtonListBottom(dialog, "彩虹卡 "+cardEntities.get(i).num, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    payTypeTv.setText("彩虹卡 "+cardEntities.get(a).num);
                    paymentType = 2;
                    cardId = cardEntities.get(a).cardId;
                    checkedIv.setImageResource(R.drawable.xuanzhong);
                    mPayTypeAdapter.setCurrentItem(-1);
                }
            });
        }

    }

    //获取我的卡片
    void getMyCard(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(ShopOrderActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(API.API_GET_MY_CARD)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<CardModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(CardModel resp) {
                        if(resp.data != null && !resp.data.isEmpty()) {
                            cardEntities = resp.data;
                            for (int i = 0;i<cardEntities.size();i++){
                                if(defaultNo.equals(cardEntities.get(i).num)){
                                    cardId = cardEntities.get(i).cardId;
                                }
                            }
                        }else {
                            MyConfig.putSharePre(ShopOrderActivity.this, Constants.USERINFO, Constants.DEFAULT_NO, "");
                            payTypeTv.setText("账户余额");
                            paymentType = 1;
                            cardId = "";
                        }
                    }

                    @Override
                    public void onBtwError(BtwRespError<CardModel> error) {
                        Toast.makeText(ShopOrderActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(ShopOrderActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
//                        getMyCard();
                    }
                }).excute(CardModel.class);
    }

    //购买数字码
    void payment(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(ShopOrderActivity.this, Constants.USERINFO, Constants.UID));

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("type",serviceType);
        parameters.put("son_type",sonServiceType);
        parameters.put("shop_id",shopId);
        parameters.put("code_id",codeId);
        parameters.put("client_id",API.API_CLIENT_ID);

        withBtwVolley().load(API.API_PAYMENT_CODE)
                .method(Request.Method.POST)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION2D1)
                .setParam("type",serviceType)
                .setParam("son_type",sonServiceType)
                .setParam("shop_id",shopId)
                .setParam("code_id",codeId)
                .setParam("client_id",API.API_CLIENT_ID)
                .setParam("paramSign", StringUtil.createParameterSign(parameters))
//                .setParam("payment_type",paymentType)
//                .setParam("card_id",cardId)
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
                        orderEntity = resp.data;
//                        Intent intent = new Intent(ShopOrderActivity.this,ShopOrderSucceedActivity.class);
//                        intent.putExtra(Constants.KEY_TRADE_NO,resp.data.tradeNo);
//                        startActivity(intent);
//                        finish();
                        payOrder(resp.data.tradeNo);
                    }

                    @Override
                    public void onBtwError(BtwRespError<OrderModel> error) {
                        Toast.makeText(ShopOrderActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(ShopOrderActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(OrderModel.class);
    }
    //支付消费订单
    void payOrder(final String tradeNo){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(ShopOrderActivity.this, Constants.USERINFO, Constants.UID));

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("pay_type",paymentType);
        parameters.put("trade_no",tradeNo);
        parameters.put("card_id",cardId);

        withBtwVolley().load(API.API_PAYMENT_PAY)
                .method(Request.Method.POST)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION2D1)
                .setParam("pay_type",paymentType)
                .setParam("trade_no",tradeNo)
                .setParam("card_id",cardId)
                .setParam("paramSign", StringUtil.createParameterSign(parameters))
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<PayModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(PayModel resp) {
                        switch (paymentType){
                            case 3:
                                startWXPay(resp.data.sign);
                                break;
                            case 4:
                                alipayWithClient(resp.data.sign);
                                break;
                            default:
                                paySuccess(tradeNo);
                        }

                    }

                    @Override
                    public void onBtwError(BtwRespError<PayModel> error) {
                        Toast.makeText(ShopOrderActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(ShopOrderActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(PayModel.class);
    }

    private void startWXPay(String sign ) {
        WXPayData payData = parseWXPayData(sign);
        sendPayReq(payData);
    }

    private WXPayData parseWXPayData(String data) {
        Gson gson = new Gson();
        WXPayData wxPayData = null;
        String json = data.replaceAll("\\\"", "\"");
        DLog.i(json);
        try {
            wxPayData = gson.fromJson(json, WXPayData.class);
        } catch (Exception e) {
            DLog.e(Log.getStackTraceString(e));
//            payFail("数据解析失败", false);
            UIUtils.toast("数据解析失败");
        }

        return wxPayData;
    }

    private void sendPayReq(WXPayData data) {
        mWXApi = WXAPIFactory.createWXAPI(this, data.appid);
        mWXApi.registerApp(data.appid);

        boolean isPaySupported = mWXApi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
        if (!isPaySupported) {
//            payFail("您未安装微信或微信版本过低", false);
            getUIUtils().dismissLoading();
            UIUtils.toast("您未安装微信或微信版本过低");
            return;
        }


        PayReq req = new PayReq();
        req.appId = data.appid;
        req.partnerId = data.partnerid;
        req.prepayId = data.prepayid;
        req.nonceStr = data.noncestr;
        req.timeStamp = data.timestamp;
        req.packageValue = data.packageData;
        req.sign = data.sign;
        req.extData= "app data";

        mWXApi.sendReq(req);
    }

    public void onEventMainThread(WXPayEvent event) {
        getUIUtils().dismissLoading();
        if (event.errCode == 0) {
            DLog.i("wx pay success");
//            checkOrderDetail(mOrderId);
            paySuccess(orderEntity.tradeNo);
        } else {
            if (TextUtils.isEmpty(event.errStr)) {
//                payFail("微信支付失败", false);
                UIUtils.toast("微信支付失败");
            } else {
                UIUtils.toast("微信支付失败");
//                payFail(event.errStr, false);
            }
            discountLayout.setClickable(false);
        }
    }

    private void alipayWithClient(final String payInfo) {
//        String orderInfo = getOrderInfo("测试的商品", "该测试商品的详细描述", "0.01");
//        String orderInfo = "_input_charset=utf-8&body=goods_description&notify_url=http%3A%2F%2Fxxx&out_trade_no=APP_S_ALI_147920353997128&partner=2088311260983610&payment_type=1&seller_id=fengyucaihongka%40163.com&service=mobile.securitypay.pay&subject=%E5%85%85%E5%80%BC%E8%AE%A2%E5%8D%95%EF%BC%9AAPP_S_ALI_147920353997128&total_fee=110&sign=hMpwFXdyYGyXMzisKNFwHQJdGigU2k8qRbFjBHUZdqDjR2fuDA4E1lktCvF0iUzEIg%2Brsuql0Siwg6%2FRcA4lQvDyf71BVnLfT2DcG6dkT%2BqbbNafSU347sfUFb8%2BKJJpcZGRRPgaDrjKcoknGE1aQFZXtbYDF36dTzpBgZ%2BQDLU%3D&sign_type=RSA";
//        String orderInfo = "partner=\"2088421517528605\"&seller_id=\"2088421517528605\"&out_trade_no=\"JJN32F30UY478RH\"&subject=\"runbey测试商品\"&body=\"runbey商品描述\"&total_fee=\"0.01\"&notify_url=\"http://*****\"&service=\"mobile.securitypay.pay\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&show_url=\"m.alipay.com\"&sign=\"Bd3LCJusJrv0Fyi3LizYa5CH%2FIubd2MU%2BiQMNWs2zIbelKnn%2Fso%2FMU6iH5qaVo6FKEiYi8BEOdZlO7y6eEa1u0D%2FGSm3b%2BIWsZJGis615QFV1qQqUrTR1L51Nt1yg4TSjRu8NS8NgZ87Fp9B87RXveDi%2FySkdmxk%2BCMVfPQ3S5U%3D\"&sign_type=\"RSA\"";
//        mAliPay.pay(payInfo, mHandler,
//                RQF_PAY);
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(ShopOrderActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    //获取可用优惠券
    void getCoupon(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(ShopOrderActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(API.API_GET_MY_USABLE_DISCUNT)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("type",serviceType)
                .setParam("client_id",2)
                .setParam("shop_id",shopId)
                .setParam("money",serviceMoney)
                .setParam("service",1)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<DiscountModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(DiscountModel resp) {
                        if(resp.data != null && !resp.data.isEmpty()) {
                            discountStatus.setText(resp.data.size()+"张可用");
                            discountLayout.setClickable(true);
                            discountEntities.addAll(resp.data);
                        }else {
                            discountStatus.setText("暂无可用");
                            discountLayout.setClickable(false);
                        }


                    }

                    @Override
                    public void onBtwError(BtwRespError<DiscountModel> error) {
                        Toast.makeText(ShopOrderActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(ShopOrderActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(DiscountModel.class);
    }


    void paySuccess(String tradeNo){
        Intent intent = new Intent(ShopOrderActivity.this,ShopOrderSucceedActivity.class);
        intent.putExtra(Constants.KEY_TRADE_NO,tradeNo);
        startActivity(intent);
        finish();
    }

    void payFail(){
        UIUtils.toast("支付失败");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEventBus.unregister(this);
    }
}
