package com.rainbowcard.client.common;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.android.volley.Request;
import com.bestpay.app.PaymentTask;
import com.google.gson.Gson;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.YHApplication;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.common.model.PayResult;
import com.rainbowcard.client.model.ALiPayModel;
import com.rainbowcard.client.model.DiscountEntity;
import com.rainbowcard.client.model.DiscountModel;
import com.rainbowcard.client.model.OrderModel;
import com.rainbowcard.client.model.WXPayData;
import com.rainbowcard.client.model.WXPayModel;
import com.rainbowcard.client.ui.AlipayWapActivity;
import com.rainbowcard.client.ui.BankTransferActivity;
import com.rainbowcard.client.ui.PayStatusActivity;
import com.rainbowcard.client.ui.UsableDiscountActivity;
import com.rainbowcard.client.utils.CryptTool;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.PayUtil;
import com.rainbowcard.client.utils.StringUtil;
import com.rainbowcard.client.utils.UIUtils;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.common.model.PayType;
import com.rainbowcard.client.common.utils.DLog;
import com.rainbowcard.client.common.utils.ViewUtils;
import com.rainbowcard.client.model.OrderEntity;
import com.rainbowcard.client.utils.alipay.AlipayUtils;
import com.rainbowcard.client.utils.alipay.MobileSecurePayer;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.wxapi.model.WXPayEvent;

import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by gc on 14-10-16.
 */
public class PayActivity extends MyBaseActivity {

    private final static int STATUS_NOT_PAID = 99;

    private final static float UNIT_YUAN = 100f;
    private final static int RATE_BTW_TO_RMB = 5;
    private final static int RQF_PAY = 1;

    private final static int PAY_CASH_MODE_ALI_BOTH = 2;
    private final static int PAY_CASH_MODE_WECHAT = 3;
    private final static int PAY_CASH_MODE_BESTPAY = 4;
    private final static int PAY_CASH_MODE_BANK = 5;
    public double userRate;

    private ArrayList<DiscountEntity> discountEntities = new ArrayList<DiscountEntity>();
    private DiscountEntity discountEntity ;

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.tv_confirm)
    TextView mTvConfirm;
    //    @InjectView(R.id.tv_card)
//    TextView mTvGoodsName;
//    @InjectView(R.id.tv_name)
//    TextView mTvGoodsPoints;
//    @InjectView(R.id.tv_phone)
//    TextView mTvGoodsPrice;
//    @InjectView(R.id.tv_goods_money)
//    TextView mTvMoney;
    @InjectView(R.id.lv_pay_method)
    ListView mLvPayMethod;
    @InjectView(R.id.discount_count)
    TextView discountCount;
    @InjectView(R.id.discount_status)
    TextView discountStatus;
    @InjectView(R.id.discount_layout)
    RelativeLayout discountLayout;
    @InjectView(R.id.tv_price)
    TextView priceTv;
    @InjectView(R.id.affirm_layout)
    RelativeLayout affirmLayout;
    @InjectView(R.id.tv_order)
    TextView orderTv;
    @InjectView(R.id.tv_time)
    TextView timeTv;

    private TextView mTvMoney;
    private TextView mTvGoodsPrice;
    private TextView mTvGoodsPoints;

    private PayTypeAdapter mPayTypeAdapter;
    private MobileSecurePayer mAliPay;

    private Bundle bundle;

    private int isReturnFreeWashOrder;  //判断充值成功后是否返回免费洗车券  为1返回

    private int mOrderId;
    //    private String mOrderNum;
//    private String mGoodsName;
    private int rainbowType;//用来区别是彩虹卡购买还是充值 0充值  1购买
    //    private int price;
//    private String name;
//    private String phone;
//    private String addr;
    private OrderModel.OrderEntity orderEntity ;

    private Boolean mIsFirstPay = true;
    private boolean mIsSetBtwValue = false;
    private IWXAPI mWXApi;
    private EventBus mEventBus;

    private int codeId = 0;
    private int payType;
    private String card;
    private int price;
    private float rebate;
    private String name;
    private String phone;
    private String address;
    private boolean isShow;

    private String cityId;

    String token;
    private boolean isDiscount = false;

    PaymentTask task = new PaymentTask(PayActivity.this);
    private boolean mIsH5Payment = false;
    static final int ORDER_FAIL = -1;
    static final int ORDER_SUCCESS = 0;
    Hashtable<String, String> paramsHashtable = new Hashtable<String, String>();

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
                        paySuccess();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            paySuccess();
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
//                            payFail();
//                            paySuccess();
                            payFail("支付宝支付失败");
                            mTvConfirm.setEnabled(true);
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
        setContentView(R.layout.act_pay);
        ButterKnife.inject(this);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        UIUtils.setMiuiStatusBarDarkMode(PayActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(PayActivity.this,true);
//        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(PayActivity.this, Constants.USERINFO, Constants.UID));
        mEventBus = EventBus.getDefault();
        mEventBus.register(this);

        mAliPay = new MobileSecurePayer(this);
        getData();

        mPayTypeAdapter = new PayTypeAdapter(this);
        initView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEventBus.unregister(this);
    }

    private void getData() {
        Intent intent = getIntent();
        orderEntity = (OrderModel.OrderEntity) intent.getSerializableExtra(Constants.KEY_ORDER_MODEL);
        rainbowType = intent.getIntExtra(Constants.KEY_RAINBOW_TYPE,0);
        card = intent.getStringExtra(Constants.KEY_CARD);
        price = intent.getIntExtra(Constants.KEY_PRICE,0);
        rebate = intent.getFloatExtra(Constants.KEY_REBATE,0);
        name = intent.getStringExtra(Constants.KEY_NAME);
        phone = intent.getStringExtra(Constants.KEY_PHONE);
        address = intent.getStringExtra(Constants.KEY_ADDR);
        isShow = intent.getBooleanExtra(Constants.KEY_IS_SHOW,false);
        isDiscount = intent.getBooleanExtra(Constants.KEY_IS_DISCOUNT,false);
        cityId = intent.getStringExtra(Constants.KEY_CITY_ID);
        isReturnFreeWashOrder = intent.getIntExtra(Constants.KEY_IS_RETURN_FREE_WACH_ORDER,0);

        bundle = intent.getExtras();
//        mOrderNum = bundle.getString(Constants.KEY_RECHARGE_NO);
//        mOrderId = intent.getIntExtra(Constants.KEY_ORDER_ID, -1);

        if (mOrderId == -1) {
            setResult(RESULT_CANCELED);
            finish();
        }

        switch (rainbowType){
            case 0:
                refreRechargeUI();
                payType = 5;
                break;
            case 1:
                refreBuyUI();
                payType = 6;
                break;
            case 2:
                refreAccountUI();
                payType = 5;
                break;

        }
//        if(orderEntity == null) {
//            if(isDiscount){
//                getCoupon();
//            }else {
//                discountStatus.setText("暂无可用优惠券");
//                discountLayout.setClickable(false);
//            }
//        }

        if(orderEntity != null){
//            affirmLayout.setVisibility(View.VISIBLE);
            orderTv.setText(orderEntity.tradeNo);
            timeTv.setText(orderEntity.time);
        }else {
            affirmLayout.setVisibility(View.GONE);
        }

    }

    //显示充值UI
    void refreRechargeUI(){
        View view = ((ViewStub)findViewById(R.id.sub_recharge)).inflate();
        TextView mTvGoodsName = (TextView) view.findViewById(R.id.tv_card);
//        mGoodsName = bundle.getString(Constants.KEY_CARD);
        if (orderEntity != null && !TextUtils.isEmpty(orderEntity.cardNumber)) {
            mTvGoodsName.setText(String.format(getString(R.string.rainbow_no),orderEntity.cardNumber));
        }else{
            mTvGoodsName.setText(String.format(getString(R.string.rainbow_no),card));
        }

//        phone = bundle.getString(Constants.KEY_PHONE);
//        price = bundle.getInt(Constants.KEY_PRICE);
//        name = bundle.getString(Constants.KEY_NAME);
        mTvMoney = (TextView) view.findViewById(R.id.tv_goods_money);
        if(orderEntity != null && !TextUtils.isEmpty(String.valueOf(orderEntity.payMoney))){
            mTvMoney.setText(String.format(getString(R.string.price),new
                    DecimalFormat("##,###,###,###,##0.00").format(orderEntity.payMoney)));
            priceTv.setText(String.format(getString(R.string.price),new
                    DecimalFormat("##,###,###,###,##0.00").format(orderEntity.payMoney)));
        }else {
            mTvMoney.setText(String.format(getString(R.string.price),new
                    DecimalFormat("##,###,###,###,##0.00").format(price)));
            priceTv.setText(String.format(getString(R.string.price),new
                    DecimalFormat("##,###,###,###,##0.00").format(price)));
        }
        mTvGoodsPrice = (TextView) view.findViewById(R.id.tv_phone);
            if(orderEntity != null && !TextUtils.isEmpty(orderEntity.phone)){
                mTvGoodsPrice.setText(orderEntity.phone);
            }else{
                mTvGoodsPrice.setText(phone);
            }
        mTvGoodsPoints = (TextView) view.findViewById(R.id.tv_name);
        if(orderEntity != null && !TextUtils.isEmpty(orderEntity.userName)){
            mTvGoodsPoints.setText(orderEntity.userName);
        }else{
            mTvGoodsPoints.setText(name);
        }
    }
    //显示购买UI
    void refreBuyUI(){
        View view = ((ViewStub)findViewById(R.id.sub_buy)).inflate();
        TextView nameTv = (TextView) view.findViewById(R.id.tv_name);
        TextView phoneTv = (TextView) view.findViewById(R.id.tv_phone);
        TextView addrTv = (TextView) view.findViewById(R.id.tv_addr);
        TextView limitTv = (TextView) view.findViewById(R.id.tv_limit);
//        name = bundle.getString(Constants.KEY_NAME);
//        phone = bundle.getString(Constants.KEY_PHONE);
//        addr = bundle.getString(Constants.KEY_ADDR);
//        price = bundle.getInt(Constants.KEY_PRICE);
        if(orderEntity != null && !TextUtils.isEmpty(orderEntity.userName)){
            nameTv.setText(orderEntity.userName);
        }else {
            nameTv.setText(name);
        }
        if(orderEntity != null && !TextUtils.isEmpty(orderEntity.phone)){
            phoneTv.setText(orderEntity.phone);
        }else {
            phoneTv.setText(phone);
        }
        if(orderEntity != null && !TextUtils.isEmpty(orderEntity.address)){
            addrTv.setText(orderEntity.address);
        }else {
            addrTv.setText(address);
        }
        if(orderEntity != null && !TextUtils.isEmpty(String.valueOf(orderEntity.money))){
            limitTv.setText(String.format(getString(R.string.price),new
                    DecimalFormat("##,###,###,###,##0.00").format(orderEntity.money)));
        }else {
            limitTv.setText(String.format(getString(R.string.price), new
                    DecimalFormat("##,###,###,###,##0.00").format(price)));
        }
        if(orderEntity != null && !TextUtils.isEmpty(String.valueOf(orderEntity.payMoney))){
            priceTv.setText(String.format(getString(R.string.price),new
                    DecimalFormat("##,###,###,###,##0.00").format(orderEntity.payMoney)));
        }else {
            priceTv.setText(String.format(getString(R.string.price),new
                    DecimalFormat("##,###,###,###,##0.00").format(price)));
        }

    }
    //显示充值账户UI
    void refreAccountUI(){
        View view = ((ViewStub)findViewById(R.id.sub_recharge_account)).inflate();
        TextView limitTv = (TextView) view.findViewById(R.id.tv_limit);
        TextView givePrice = (TextView) view.findViewById(R.id.give_price);
        RelativeLayout giveLayout = (RelativeLayout) view.findViewById(R.id.give_layout);
        if(orderEntity != null && !TextUtils.isEmpty(String.valueOf(orderEntity.money))){
            limitTv.setText(String.format(getString(R.string.price),new
                    DecimalFormat("##,###,###,###,##0.00").format(orderEntity.money)));
        }else {
            limitTv.setText(String.format(getString(R.string.price), new
                    DecimalFormat("##,###,###,###,##0.00").format(price)));
        }

        if(orderEntity != null && !TextUtils.isEmpty(String.valueOf(orderEntity.rebateMoney))){
            givePrice.setText(String.format(getString(R.string.price),new
                    DecimalFormat("##,###,###,###,##0.00").format(orderEntity.rebateMoney)));
            if(orderEntity.rebateMoney == 0.0 ){
                giveLayout.setVisibility(View.GONE);
            }else {
                giveLayout.setVisibility(View.VISIBLE);
            }
        }else {
            givePrice.setText(String.format(getString(R.string.price), new
                    DecimalFormat("##,###,###,###,##0.00").format(rebate)));
            if(rebate == 0.0 ){
                giveLayout.setVisibility(View.GONE);
            }else {
                giveLayout.setVisibility(View.VISIBLE);
            }
        }

        if(orderEntity != null && !TextUtils.isEmpty(String.valueOf(orderEntity.payMoney))){
            priceTv.setText(String.format(getString(R.string.price),new
                    DecimalFormat("##,###,###,###,##0.00").format(orderEntity.payMoney)));
        }else {
            priceTv.setText(String.format(getString(R.string.price), new
                    DecimalFormat("##,###,###,###,##0.00").format(price)));
        }
    }

    private void initView() {

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
//                startHybridPay();
            }
        });

        mTvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startPay();
                if(getPayCashMode() == 0){
                    UIUtils.toast("请选择支付方式");
                }else {
                    if(orderEntity != null && !TextUtils.isEmpty(orderEntity.tradeNo)){
                        startHybridPay(orderEntity.tradeNo);
                    }else {
                        switch (rainbowType){
                            case 0:
                               recharge();
                                break;
                            case 1:
                                buyCard();
                                break;
                            case 2:
                                rechargeAccount();
                                break;

                        }
                    }
                }
            }
        });

        discountLayout.setClickable(false);
        discountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PayActivity.this, UsableDiscountActivity.class);
                intent.putExtra(Constants.KEY_USABLE_DISCOUNT,discountEntities);
                startActivityForResult(intent,Constants.REQUEST_USABLE_DISCOUNT);
            }
        });
        if(orderEntity == null) {
            if(isDiscount){
                getCoupon();
            }else {
                    discountStatus.setText("不可用");
                    discountLayout.setClickable(false);
            }
        }else {
            discountLayout.setClickable(false);
//            if(((int)orderEntity.couponMoney) != 0) {
//                discountStatus.setText(String.format(getString(R.string.sub_price), new
//                        DecimalFormat("##,###,###,###,##0.00").format(orderEntity.couponMoney)));
//            }else {
//                discountStatus.setText("未使用");
//            }
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
        }

    }

    private void initPayType() {
        List<PayType> payTypes = new ArrayList<PayType>(2);
        payTypes.add(new PayType(PayType.TYPE_WEIXIN));
        payTypes.add(new PayType(PayType.TYPE_ALIPAY));
        if(rainbowType == 2){
            payTypes.add(new PayType(PayType.TYPE_BANK));
        }
//        payTypes.add(new PayType(PayType.TYPE_BESTPAY));
        mPayTypeAdapter.setPayTypes(payTypes);

        mLvPayMethod.setAdapter(mPayTypeAdapter);
        ViewUtils.setListViewHeightBasedOnChildren(mLvPayMethod);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getUIUtils().dismissLoading();
        if(data!=null)
        {
            mTvConfirm.setEnabled(true);
//            Toast.makeText(PayActivity.this,data.getExtras().getString("result"), Toast.LENGTH_LONG).show();
        }
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
//                }else {
//                    discountStatus.setText(discountEntity.money+"折");
//                }
//                discountStatus.setText(String.format(getString(R.string.sub_price),new
//                        DecimalFormat("##,###,###,###,##0.00").format(discountEntity.couponMoney)));
                if(discountEntity.moneyType == 1) {
                    discountStatus.setText(String.format(getString(R.string.sub_price), new
                            DecimalFormat("##,###,###,###,##0.00").format(discountEntity.couponMoney)));
                }else {
                    discountStatus.setText(String.format(getString(R.string.sub_break),discountEntity.money));
                }
                /*if(rainbowType == 0){
                    if(discountEntity.moneyType == 1) {
                        mTvMoney.setText(String.format(getString(R.string.recharge_price), discountEntity.payMoney));
                    }else {
                        mTvMoney.setText(String.format(getString(R.string.recharge_price), discountEntity.payMoney));
                    }
                }else {
                    if(discountEntity.moneyType == 1) {
                        priceTv.setText(String.format(getString(R.string.recharge_price), discountEntity.payMoney));
                    }else {
                        priceTv.setText(String.format(getString(R.string.recharge_price), discountEntity.payMoney));
                    }
                }*/
                if(discountEntity.moneyType == 1) {
                    priceTv.setText(String.format(getString(R.string.price), new
                            DecimalFormat("##,###,###,###,##0.00").format(discountEntity.payMoney)));
                }else {
                    priceTv.setText(String.format(getString(R.string.price), new
                            DecimalFormat("##,###,###,###,##0.00").format(discountEntity.payMoney)));
                }
                codeId = discountEntity.id;
            }else {
                discountStatus.setText(discountEntities.size()+"张可用");
//                if(rainbowType == 0){
//                    mTvMoney.setText(String.format(getString(R.string.recharge_price),price));
//                }else {
//                    priceTv.setText(String.format(getString(R.string.recharge_price),price));
//                }
                priceTv.setText(String.format(getString(R.string.price),new
                        DecimalFormat("##,###,###,###,##0.00").format(price)));
                codeId = 0;
            }
        }
    }


    private void startPay(String tradeNo) {
        startHybridPay(tradeNo);
    }


    private int getPayCashMode() {
        int item = mPayTypeAdapter.getCurrentItem();
        switch (item) {
            case 1:
                return PAY_CASH_MODE_ALI_BOTH;
            case 0:
                return PAY_CASH_MODE_WECHAT;
            case 2:
                return PAY_CASH_MODE_BANK;
            case 3:
                return PAY_CASH_MODE_BESTPAY;
            default:
                return 0;
        }
    }


    private void startHybridPay(String tradeNo) {
        mTvConfirm.setEnabled(false);
        if (isPaySuccess(100)) {
//            paySuccess();
        } else {
            switch (getPayCashMode()) {
                case PAY_CASH_MODE_ALI_BOTH:
//                    startAliPay();
                    aLiPay(tradeNo);
                    break;
                case PAY_CASH_MODE_WECHAT:
                    wxPay(tradeNo);
                    break;
                case PAY_CASH_MODE_BESTPAY:
                    bestPay();
                    break;
                case PAY_CASH_MODE_BANK:
                    bankPay(tradeNo);
                    break;

            }
        }
    }

    private void startWXPay(WXPayData tenPay) {
//        WXPayData payData = parseWXPayData("{\"appid\": \"wxa02cab04e7402a38\",\"partnerid\": \"1415459802\",\"prepayid\": \"wx201611231715348d13fb9fb30208130960\",\"package\": \"Sign=WXPay\",\"noncestr\": \"qiMfhXINBQdgHgv8hxtTOF136hNNQItN\",\"timestamp\": 1479892534,\"sign\": \"3FC78EF24C7550C209D0E24D17228ADA\"}");
//        sendPayReq(payData);
        WXPayData payData = new WXPayData();
        if(tenPay != null) {
            payData.appid = tenPay.appid;
            payData.sign = tenPay.sign;
            payData.noncestr = tenPay.noncestr;
            payData.packageData = tenPay.packageData;
            payData.partnerid = tenPay.partnerid;
            payData.timestamp = tenPay.timestamp;
            payData.prepayid = tenPay.prepayid;
            DLog.i(payData.toString());
            sendPayReq(payData);
        }
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
            payFail("数据解析失败", false);
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
            mTvConfirm.setEnabled(true);
            discountLayout.setClickable(false);
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
            paySuccess();
        } else {
            if (TextUtils.isEmpty(event.errStr)) {
//                payFail("微信支付失败", false);
//                paySuccess();
                payFail("微信支付失败");
                mTvConfirm.setEnabled(true);
                discountLayout.setClickable(false);
            } else {
//                payFail(event.errStr, false);
//                paySuccess();
                payFail("微信支付失败");
                mTvConfirm.setEnabled(true);
                discountLayout.setClickable(false);
            }
        }
    }


    private void startAliPay() {
        if (mAliPay.bindPayService()) {
//            alipayWithClient();
        } else {
            alipayWithWap();
        }
//        payFail();
    }

//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case RQF_PAY:
//                    String result = (String) msg.obj;
//                    handleClientPayResult(result);
//                    break;
//                default:
//                    return;
//            }
//        }
//    };

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
                PayTask alipay = new PayTask(PayActivity.this);
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

    private String getOrderInfo(String subject, String body, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + "2088311260983610" + "\"";  //2016072001640579    2088311260983610

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + "664646788@163.com" + "\"";      //fengyucaihongka@163.com

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + "10888814455555" + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    private void alipayWithWap() {
        Intent intent = new Intent(this, AlipayWapActivity.class);
        intent.putExtra(Constants.KEY_ALIPAY_URL,
                "www.alipay.com");
        startActivityForResult(intent, Constants.REQUEST_ALIPAY_WAP);
    }

    private void handleClientPayResult(String result) {
        int resultStatus = Integer.MAX_VALUE;
        if (!TextUtils.isEmpty(result)) {
            resultStatus = AlipayUtils
                    .getResultStatusFromAlipayResultString(result);
        }

        switch (resultStatus) {
            case 8000:
            case 9000:
//                checkOrderDetail(mOrderId);
//                paySuccess();
                break;
            default:
                payFail();
        }

    }

    /*private void checkOrderDetail(int orderId) {
        withBtwVolley()
                .load(API.API_GET_BTW_ORDER_DETAIL)
                .method(Request.Method.GET)
                .setParam("order_id", orderId)
                .setUIComponent(this)
                .setResponseHandler(new BtwVolley.ResponseHandler<OrderDetail>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading(false, R.string.loading);
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(OrderDetail resp) {
                        checkOrderStatus(resp);
                    }

                    @Override
                    public void onBtwError(BtwRespError error) {
                        payFail("订单查询失败", true);
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        payFail(getString(R.string.network_error), true);
                    }
                })
                .excute();
    }*/

    private void checkOrderStatus(OrderEntity.OrderDetail result) {
        if (isPaySuccess(200)) {
//            paySuccess();
        } else {
            payFail();
        }
    }

    private void paySuccess() {
        Intent intent = new Intent(PayActivity.this, PayStatusActivity.class);
        intent.putExtra(Constants.KEY_PAY_STATUS,1);
        intent.putExtra(Constants.KEY_ORDER_MODEL,orderEntity);
        intent.putExtra(Constants.KEY_RAINBOW_TYPE,rainbowType);
        intent.putExtra(Constants.KEY_CITY_ID,cityId);
        intent.putExtra(Constants.KEY_REBATE,rebate);
        intent.putExtra(Constants.KEY_IS_RETURN_FREE_WACH_ORDER,isReturnFreeWashOrder);
        startActivity(intent);
        finish();
//        mTvConfirm.setEnabled(true);
//        DialogBuilder.buildOneButtonDialog(PayActivity.this, "支付成功",
//                new DialogBuilder.OnConfirmListener() {
//                    @Override
//                    public void onConfirm() {
//                        setResult(RESULT_OK);
//                        finish();
//                    }
//                }).show();
    }

    private void payFail(String string, final boolean shouldExit) {
        mTvConfirm.setEnabled(true);
        if (TextUtils.isEmpty(string)) {
            string = "支付失败";
        }
        Intent intent = new Intent(PayActivity.this, PayStatusActivity.class);
        intent.putExtra(Constants.KEY_PAY_STATUS,0);
        intent.putExtra(Constants.KEY_ORDER_MODEL,orderEntity);
        intent.putExtra(Constants.KEY_RAINBOW_TYPE,rainbowType);
        intent.putExtra(Constants.KEY_REBATE,rebate);
        intent.putExtra(Constants.KEY_IS_RETURN_FREE_WACH_ORDER,isReturnFreeWashOrder);
        startActivity(intent);
//        finish();
        /*DialogBuilder.buildOneButtonDialog(PayActivity.this, string,
                new DialogBuilder.OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        if (shouldExit) {
                            finish();
                        } else {
//                            loadOrderDetail(mOrderId);
                        }
                    }
                }).show();*/
    }
    private void payFail(String toast) {
        UIUtils.toast(toast);
    }

    private void payFail() {
        payFail("", false);
    }

    private Boolean isPaySuccess(int status) {
        return status >= 199;
    }

    private void aLiPay(String tradeNo){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(PayActivity.this, Constants.USERINFO, Constants.UID));

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("trade_no",tradeNo);

        withBtwVolley().load(API.API_RAINBOW_ALI_PAY)
                .method(Request.Method.POST)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("trade_no",tradeNo)
                .setParam("paramSign", StringUtil.createParameterSign(parameters))
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<ALiPayModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(ALiPayModel resp) {
                            alipayWithClient(resp.data.rst);
                    }

                    @Override
                    public void onBtwError(BtwRespError<ALiPayModel> error) {
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
                }).excute(ALiPayModel.class);
    }

    private void bankPay(String tradeNo){
        mTvConfirm.setEnabled(true);
        Intent intent = new Intent(PayActivity.this,BankTransferActivity.class);
        intent.putExtra(Constants.KEY_CITY_ID,cityId);
        intent.putExtra(Constants.KEY_TRADE_NO,tradeNo);
        intent.putExtra(Constants.KEY_ACTIVITY_MONEY,price);
        intent.putExtra(Constants.KEY_IS_RETURN_FREE_WACH_ORDER,isReturnFreeWashOrder);
        startActivity(intent);

    }

    private void wxPay(String tradeNo){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(PayActivity.this, Constants.USERINFO, Constants.UID));

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("trade_no",tradeNo);

        withBtwVolley().load(API.API_RAINBOW_WX_PAY)
                .method(Request.Method.POST)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("trade_no",tradeNo)
                .setParam("paramSign", StringUtil.createParameterSign(parameters))
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<WXPayModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onResponse(WXPayModel resp) {
                        startWXPay(resp.data);
                    }

                    @Override
                    public void onBtwError(BtwRespError<WXPayModel> error) {
                        UIUtils.toast(error.errorMessage);
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        UIUtils.toast(getString(R.string.network_error));
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
//                        wxPay();
                    }
                }).excute(WXPayModel.class);
    }

    //获取可用优惠券
    void getCoupon(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(PayActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(API.API_GET_MY_USABLE_DISCUNT)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("type",payType)
                .setParam("client_id",2)
                .setParam("shop_id","")
                .setParam("money",price)
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
                        Toast.makeText(PayActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(PayActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(DiscountModel.class);
    }

    //账户充值
    void rechargeAccount(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(PayActivity.this, Constants.USERINFO, Constants.UID));

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("phone",card);
        parameters.put("money",price);
        parameters.put("code_id",codeId);
        parameters.put("client_id",API.API_CLIENT_ID);
        parameters.put("city_id",cityId);

        withBtwVolley().load(API.API_RECHARGE_ACCOUNT)
                .method(Request.Method.POST)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("phone",card)
                .setParam("money",price)
                .setParam("code_id",codeId)
                .setParam("client_id",API.API_CLIENT_ID)
                .setParam("city_id",cityId)
                .setParam("paramSign", StringUtil.createParameterSign(parameters))
                .setRetrys(0)
                .setUIComponent(this)
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
                        startHybridPay(resp.data.tradeNo);
                    }

                    @Override
                    public void onBtwError(BtwRespError<OrderModel> error) {
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
                }).excute(OrderModel.class);
    }

    //卡充值下单
    void recharge(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(PayActivity.this, Constants.USERINFO, Constants.UID));

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("card_number",card);
        parameters.put("money",price);
        parameters.put("code_id",codeId);
        parameters.put("client_id",API.API_CLIENT_ID);

        BtwVolley btwVolley = withBtwVolley().load(API.API_RAINBOWCARD_RECHARGE)
                .method(Request.Method.POST)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("card_number",card)
                .setParam("money",price)
                .setParam("code_id",codeId)
                .setParam("client_id",API.API_CLIENT_ID)
                .setParam("paramSign", StringUtil.createParameterSign(parameters))
                .setRetrys(0)
                .setUIComponent(this)
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
                        mTvGoodsPoints.setText(resp.data.userName);
                        mTvGoodsPrice.setText(resp.data.phone);
                        startHybridPay(resp.data.tradeNo);
                    }

                    @Override
                    public void onBtwError(BtwRespError<OrderModel> error) {
                        UIUtils.toast(error.errorMessage);
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        UIUtils.toast(getString(R.string.network_error));
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
//                        recharge(price);
                    }
                });
        if(isShow){
            btwVolley.setParam("user_name",name);
            btwVolley.setParam("phone",phone);
        }
        btwVolley.excute(OrderModel.class);
    }

    //购卡下单
    void buyCard(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(PayActivity.this, Constants.USERINFO, Constants.UID));

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("money",price);
        parameters.put("phone",phone);
        parameters.put("address", URLEncoder.encode(address));
        parameters.put("user_name",URLEncoder.encode(name));
        parameters.put("code_id",codeId);
        parameters.put("client_id",API.API_CLIENT_ID);

        withBtwVolley().load(API.API_RAINBOW_RECHARGE)
                .setHeader("Accept", API.VERSION)
                .setHeader("Authorization",token)
                .method(Request.Method.POST)
                .setParam("money",price)
                .setParam("phone",phone)
                .setParam("address",address)
                .setParam("user_name",name)
                .setParam("code_id",codeId)
                .setParam("client_id",API.API_CLIENT_ID)
                .setParam("paramSign", StringUtil.createParameterSign(parameters))
                .setRetrys(0)
                .setUIComponent(this)
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
                        startHybridPay(resp.data.tradeNo);
                    }

                    @Override
                    public void onBtwError(BtwRespError<OrderModel> error) {
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
                }).excute(OrderModel.class);
    }

    void bestPay(){
        String merchantId = "043101180050000";
        String orderseq = String.valueOf(System.currentTimeMillis());
        String orderTranseq = System.currentTimeMillis() + "00001";
        String orderTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()));
        String ordervalidityTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()+ 60* 1000 * 60 * 24));


        //16个参数；
        StringBuffer md5Buffer = new StringBuffer();
        md5Buffer.append("SERVICE=").append("mobile.security.pay")
                .append("&MERCHANTID=").append(merchantId)
                .append("&MERCHANTPWD=").append("534231")
                .append("&SUBMERCHANTID=").append("")
                .append("&BACKMERCHANTURL=").append("http://127.0.0.1:8040/wapBgNotice.action")
                .append("&ORDERSEQ=").append(orderseq)
                .append("&ORDERREQTRANSEQ=").append(orderTranseq)
                .append("&ORDERTIME=").append(orderTime)
                .append("&ORDERVALIDITYTIME=").append(ordervalidityTime)
                .append("&CURTYPE=").append("RMB")
                .append("&ORDERAMOUNT=").append("0.01")
                .append("&SUBJECT=").append("商品测试")
                .append("&PRODUCTID=").append("04")
                .append("&PRODUCTDESC=").append("Test")
                .append("&CUSTOMERID=").append("12345678901")
                .append("&SWTICHACC=").append("true")
                .append("&KEY=").append("344C4FB521F5A52EA28FB7FC79AEA889478D4343E4548C02");//添加key是用于key校验改造；

        Log.i("GCCCC", "sign加密原串："+md5Buffer.toString());
        String sign = null;

        try {
            sign = CryptTool.md5Digest(md5Buffer.toString());

        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Log.i("GCCC", "加密sign值："+sign);

//						String mac = "MERCHANTID="+ merchantId
//								+ "&ORDERSEQ="+ orderseq
//								+ "&ORDERREQTRNSEQ="+ orderTranseq
//								+ "&ORDERTIME="+ orderTime
//								+ "&KEY=" + TestConstant.CKEY;
//
//						try {
//							mac = CryptTool.md5Digest(mac);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}


        //29+1《类似session的字段》个参数；
        final String paramsStr = "MERCHANTID="+merchantId
                +"&SUBMERCHANTID="+""
                +"&MERCHANTPWD="+"534231"
                +"&ORDERSEQ="+orderseq
                +"&ORDERAMOUNT="+"0.01"
                +"&ORDERTIME="+orderTime
                +"&ORDERVALIDITYTIME="+ordervalidityTime
                +"&PRODUCTDESC="+"Test"
                +"&CUSTOMERID="+"12345678901"
                +"&PRODUCTAMOUNT="+"0.01"
                +"&ATTACHAMOUNT=" +"0"
                +"&CURTYPE="+ "RMB"
                +"&BACKMERCHANTURL=" +"http://127.0.0.1:8040/wapBgNotice.action"
                +"&ATTACH=" +""
                +"&PRODUCTID=" +"04"
                +"&USERIP=" +"192.168.11.130"
                +"&DIVDETAILS=" +""

                +"&ACCOUNTID=" +""
                +"&BUSITYPE=" +"04"
                +"&ORDERREQTRANSEQ=" +orderTranseq
                +"&SERVICE=" +"mobile.security.pay"
                +"&SIGNTYPE=" + "MD5"

                +"&SIGN="+sign
                +"&SUBJECT="+"商品测试"
                +"&SWTICHACC="+"true"
                +"&SESSIONKEY="+"asdfasdfahskfjalsdfkajsdfljasdlfjsjfkj"
                +"&OTHERFLOW="+"01"
                +"ACCESSTOKEN"+"lajsfsdjfaljdsflajdsfjalkjslaajdlsjfaldjf";


//						SIGN//上传后台参数值；
//		APPID//无此参数；
//		APPENV//无此参数；
//						NOTIFYURL //BACKMERCHANTURL后台通知地址；
//						ORDERAMT //ORDERAMOUNT 支付订单金额；
//						SUBJECT  //商品简称；
//		SWTICHACC//大厅需要，本地判断，与后台没关系；true表示大厅进入可切换账户；
//		EXTERNTOKEN//无此参数；
//	    SDKVERSIONCODE//sdk中固定写死；
//						        SESSIONKEY//大厅中使用；

        Log.i("GCCC","&&格式字符串："+ paramsStr);





        if (!mIsH5Payment) {
            final Hashtable<String, String> paramsHashtable2 = paramsHashtable;
            // 增加下单流程
            Log.i("GCC", "Android收银台下单");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String orderResult = PayUtil.order(paramsStr);
                    Log.d("GCCCCCCCC","rinidaye"+orderResult);
                    if (orderResult != null
                            && "00".equals((orderResult.split("&"))[0])) {
                    Message msg = new Message();
                    msg.what = ORDER_SUCCESS;
                    msg.obj = paramsStr;
                    mBestPayHandler.sendMessage(msg);
                    } else {
                        mBestPayHandler.sendEmptyMessage(ORDER_FAIL);
                    }
                }
            }).start();
        } else {
            Log.i("GCCCCC", "调用H5收银台");
            task.pay(paramsStr);
        }



    }

    private final Handler mBestPayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ORDER_FAIL:
                    Toast.makeText(PayActivity.this, "下单失败", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case ORDER_SUCCESS:
                    getUIUtils().loading();
                    String aa = (String) msg.obj;
                    Log.d("GCCCCC","zenmejinzhelilaile"+aa);
                    task.pay( (String) msg.obj);
//                    String bb = "SERVICE=mobile.security.pay&MERCHANTID=043101180050000&MERCHANTPWD=534231&SUBMERCHANTID=&BACKMERCHANTURL=http://127.0.0.1:8080/abc&SIGNTYPE=MD5&MAC=&ORDERSEQ=1498555365921&ORDERREQTRNSEQ=&ORDERTIME=20170628133129&ORDERVALIDITYTIME=&ORDERAMOUNT=0.01&CURTYPE=RMB&PRODUCTID=04&PRODUCTDESC=test&PRODUCTAMOUNT=0.01&ATTACHAMOUNT=0&ATTACH=&DIVDETAILS=&CUSTOMERID=&USERIP=127.0.0.1&BUSITYPE=04&ACCOUNTID=&ORDERREQTRANSEQ=1498555365921&SIGN=538EDB362A14A9A67A71A19058417204&SUBJECT=ttt&SWTICHACC=";
//                    task.pay(bb);
//                    Plugin.yzfClientRecharge(SettingActivity.this, "");
                    break;
                default:
                    break;
            }
        }
    };

}
