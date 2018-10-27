package com.rainbowcard.client.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.rainbowcard.client.model.FinanceOrderModel;
import com.rainbowcard.client.model.UserBaseModel;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.StringUtil;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;

import java.text.DecimalFormat;
import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2018-3-7.
 */
public class FreeWashOrderActivity extends MyBaseActivity{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;

    @InjectView(R.id.tv_balance)
    TextView balanceTv;
    @InjectView(R.id.tv_money)
    TextView moneyTv;
    @InjectView(R.id.tv_deadline)
    TextView deadlineTv;
    @InjectView(R.id.tv_free_wash)
    TextView freeWashTv;
    @InjectView(R.id.tv_date)
    TextView dateTv;
    @InjectView(R.id.affirm_pay)
    Button affirmPayBtn;

    String token;
    float totalMoney ;

    FinanceOrderModel.FinanceOrderEntity financeOrderEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_wash_order);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(FreeWashOrderActivity.this, true);
        UIUtils.setMeizuStatusBarDarkIcon(FreeWashOrderActivity.this, true);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(FreeWashOrderActivity.this, Constants.USERINFO, Constants.UID));
        financeOrderEntity = (FinanceOrderModel.FinanceOrderEntity) getIntent().getSerializableExtra(Constants.KEY_FINANCE_ORDER_ENTITY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserBase();
    }

    void initView(){
        mHeadControlPanel.setMiddleTitle(getString(R.string.pay));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249, 249, 249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        moneyTv.setText(String.format(getString(R.string.price),new
                DecimalFormat("##,###,###,###,##0.00").format(financeOrderEntity.money)));
        deadlineTv.setText(String.format(getString(R.string.period_text),financeOrderEntity.lockDays));
        freeWashTv.setText(String.format(getString(R.string.discount_count),financeOrderEntity.financeCoupon));
        dateTv.setText(financeOrderEntity.returnTime);

        affirmPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(totalMoney < financeOrderEntity.money){
                    showDialog("温馨提示","您的余额不足");
                }else {
                    pay(financeOrderEntity.tradeNo);
                }
            }
        });

    }

    void refreshUI(UserBaseModel.UserBaseEntity userBaseEntity){
        initView();
        totalMoney = userBaseEntity.realMoney;

        balanceTv.setText(String.format(getString(R.string.price),new
                DecimalFormat("##,###,###,###,##0.00").format(totalMoney)));

    }

    //获取用户免洗券、可提金额等基本信息
    void getUserBase() {
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(FreeWashOrderActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(API.API_GET_USER_BASE)
                .method(Request.Method.GET)
                .setHeader("Authorization", token)
                .setHeader("Accept", API.VERSION)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<UserBaseModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(UserBaseModel resp) {
                        refreshUI(resp.data);
                    }

                    @Override
                    public void onBtwError(BtwRespError<UserBaseModel> error) {
                        Toast.makeText(FreeWashOrderActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(FreeWashOrderActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
//                        getMyCard();
                    }
                }).excute(UserBaseModel.class);
    }

    //购买理财，支付
    void pay(String tradeNo) {
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(FreeWashOrderActivity.this, Constants.USERINFO, Constants.UID));

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("trade_no",tradeNo);

        withBtwVolley().load(API.API_FINANCE_PAY)
                .method(Request.Method.POST)
                .setHeader("Authorization", token)
                .setHeader("Accept", API.VERSION)
                .setParam("trade_no",tradeNo)
                .setParam("paramSign", StringUtil.createParameterSign(parameters))
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<FinanceOrderModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(FinanceOrderModel resp) {
                        Intent intent = new Intent(FreeWashOrderActivity.this,FinancePayStatusActivity.class);
                        intent.putExtra(Constants.KEY_FINANCE_ORDER_ENTITY,resp.data);
                        startActivity(intent);
                    }

                    @Override
                    public void onBtwError(BtwRespError<FinanceOrderModel> error) {
                        Toast.makeText(FreeWashOrderActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(FreeWashOrderActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
//                        getMyCard();
                    }
                }).excute(FinanceOrderModel.class);
    }

    private void showDialog(String title, String tip) {
        final Dialog dialog = new Dialog(FreeWashOrderActivity.this,R.style.loading_dialog);
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

        alertTitle.setText(title);
        alertTip.setText(tip);
        alertOk.setText("去充值");
        alertOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FreeWashOrderActivity.this,RechargeAccountActivity.class);
                intent.putExtra(Constants.KEY_IS_RETURN_FREE_WACH_ORDER,1);
                startActivity(intent);
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
