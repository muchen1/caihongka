package com.rainbowcard.client.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.View;
import android.view.Window;
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
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.CertificationModel;
import com.rainbowcard.client.model.UserMoneyModel;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;

import java.text.DecimalFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017-8-26.
 */
public class MyWalletActivity extends MyBaseActivity {

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.balance)
    TextView balanceTv;
    @InjectView(R.id.recharge_balance)
    TextView rechargeBalanceTv;
    @InjectView(R.id.return_money)
    TextView returnMoneyTv;
    @InjectView(R.id.freeze_money)
    TextView freezeMoneyTv;
    @InjectView(R.id.query_icon)
    ImageView queryIcon;
    @InjectView(R.id.recharge_btn)
    Button rechargeBtn;
    @InjectView(R.id.drawings_btn)
    Button drawingsBtn;

    String token;
    String cityId;
    private int isCertification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(MyWalletActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(MyWalletActivity.this,true);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(MyWalletActivity.this, Constants.USERINFO, Constants.UID));
        cityId = getIntent().getStringExtra(Constants.KEY_CITY_ID);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserMoney();
//        getCertification();
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.my_wallet));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        queryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        rechargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyWalletActivity.this,RechargeAccountActivity.class);
                intent.putExtra(Constants.KEY_CITY_ID,cityId);
                startActivity(intent);
            }
        });

        drawingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCertification == 0){
                    Intent intent = new Intent(MyWalletActivity.this,WithdrawDepositActivity.class);
                    startActivity(intent);
//                    UIUtils.toast("去提现");
                }else {
                    showDialog("温馨提示","为了保障您账户的资金安全,\n请先进行实名认证。");
                }
            }
        });
    }

    private void getUserMoney(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(MyWalletActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(API.API_GET_USER_MONEY)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<UserMoneyModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(UserMoneyModel resp) {
                        refreshUI(resp.data);
                    }

                    @Override
                    public void onBtwError(BtwRespError<UserMoneyModel> error) {
                        Toast.makeText(MyWalletActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(MyWalletActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(UserMoneyModel.class);
    }
    private void getCertification(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(MyWalletActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(API.API_QUERY_CERTIFICATION)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("recharge_money","")
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<CertificationModel>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onResponse(CertificationModel resp) {
                        isCertification = resp.data.needCertification;
                    }

                    @Override
                    public void onBtwError(BtwRespError<CertificationModel> error) {
                        Toast.makeText(MyWalletActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(MyWalletActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(CertificationModel.class);
    }

    private void refreshUI(UserMoneyModel.UserMoney userMoney){
        setRainbowBalance(userMoney.totalMoney);
        rechargeBalanceTv.setText(new
                DecimalFormat("##,###,###,###,##0.00").format(userMoney.realMoney));
        returnMoneyTv.setText(new
                DecimalFormat("##,###,###,###,##0.00").format(userMoney.giveaway));
        freezeMoneyTv.setText(new
                DecimalFormat("##,###,###,###,##0.00").format(userMoney.lockMoney));
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(MyWalletActivity.this,R.style.loading_dialog);
        dialog.setContentView(R.layout.ui_query_dialog);
        Window dialogWindow = dialog.getWindow();
        Display display = getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = -80;
        lp.width = (int)(display.getWidth());
        dialogWindow.setAttributes(lp);

        dialog.findViewById(R.id.query_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void showDialog(String title, String tip) {
        final Dialog dialog = new Dialog(MyWalletActivity.this,R.style.loading_dialog);
        dialog.setContentView(R.layout.ui_ios_alert_dialog);
        Window dialogWindow = dialog.getWindow();
        Display display = getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = -80;
        lp.width = (int)(display.getWidth() * 0.90);
        dialogWindow.setAttributes(lp);

        TextView alertTitle = (TextView) dialog.findViewById(R.id.alert_title);
        TextView alertTip = (TextView) dialog.findViewById(R.id.alert_tip);

        alertTitle.setText(title);
        alertTip.setText(tip);

        dialog.findViewById(R.id.alert_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyWalletActivity.this,AuthenticationActivity.class);
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

    private Handler handler = new Handler();

    private int currentShowBalance = 0;

    private void setRainbowBalance(float rainbowBalance) {
        final float rainbowPointBalance = rainbowBalance;
        final int stepInMillis = 50;// refresh per 50 milliseconds
        final float max_steps = 2000 / stepInMillis;// reach to its final
        // value in at most 2
        // seconds
        final float delta;
        if(rainbowPointBalance > 1000){
            delta = Math.max(100, rainbowPointBalance / max_steps);// calculate
        }else {
            delta = Math.max(10, rainbowPointBalance / max_steps);// calculate
        }
//        final float delta = Math.max(100, rainbowPointBalance / max_steps);// calculate
        // the
        // delta
        // value
        currentShowBalance = 0;
        handler.post(new Runnable() {
            @Override
            public void run() {
                currentShowBalance += delta;
                if (currentShowBalance < rainbowPointBalance) {
                    balanceTv.setText(new
                            DecimalFormat("##,###,###,###,##0.00").format(currentShowBalance));
                    handler.postDelayed(this, stepInMillis);
                } else {
                    balanceTv.setText(new
                            DecimalFormat("##,###,###,###,##0.00").format(rainbowPointBalance));
                }

            }

        });
    }
}
