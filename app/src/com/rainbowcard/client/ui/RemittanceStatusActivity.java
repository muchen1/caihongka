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
 * Created by gc on 2018-3-5.
 */
public class RemittanceStatusActivity extends MyBaseActivity {

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;

    @InjectView(R.id.tv_pay_status)
    TextView payStatusTv;
    @InjectView(R.id.state_text)
    TextView stateTv;
    @InjectView(R.id.info)
    TextView infoTv;

    @InjectView(R.id.go_wallet_btn)
    Button goWalletBtn;
    @InjectView(R.id.recharge_btn)
    Button rechargeBtn;

    private String cityId;

    private int  isRemittanceStatus;

    private int isReturnFreeWashOrder;    //判断充值成功后是否返回免费洗车券订单   为1返回

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remittance_status);
        ButterKnife.inject(this);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        UIUtils.setMiuiStatusBarDarkMode(RemittanceStatusActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(RemittanceStatusActivity.this,true);
        cityId = getIntent().getStringExtra(Constants.KEY_CITY_ID);
        isRemittanceStatus = getIntent().getIntExtra(Constants.KEY_IS_REMITTANCE_STATUS,0);
        isReturnFreeWashOrder = getIntent().getIntExtra(Constants.KEY_IS_RETURN_FREE_WACH_ORDER,0);
        initView();
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isReturnFreeWashOrder == 1){
                    Intent intent = new Intent(RemittanceStatusActivity.this,FreeWashOrderActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        });


        if(isRemittanceStatus == 1){
            mHeadControlPanel.setMiddleTitle(getString(R.string.recharge_result));
            payStatusTv.setText("提交成功");
            stateTv.setText("银行汇款信息提交成功");
            infoTv.setText("请等待工作人员核实，核实无误后您的\n汇款金额将在1个工作日内充值到您的彩虹卡账户中");
            rechargeBtn.setText("继续充值");
        }else {
            mHeadControlPanel.setMiddleTitle(getString(R.string.withdraw_result));
            payStatusTv.setText("申请退款成功");
            stateTv.setText("您的退款申请已提交成功");
            infoTv.setText("请等待工作人员审核，\n审核无误后您的退款金额将在下一个工作日到账");
            rechargeBtn.setText("退款记录");
        }

        if(isReturnFreeWashOrder == 1){
            goWalletBtn.setText("继续下单");
        }else {
            goWalletBtn.setText("完成");
        }

        goWalletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(isReturnFreeWashOrder == 1){
                    intent = new Intent(RemittanceStatusActivity.this,FreeWashOrderActivity.class);
                    startActivity(intent);
                }else {
                    intent = new Intent(RemittanceStatusActivity.this,MainActivity.class);
                    intent.putExtra(Constants.KEY_CITY_ID,cityId);
                    startActivity(intent);
                    finish();
                }
            }
        });
        rechargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(isRemittanceStatus == 1) {
                    intent = new Intent(RemittanceStatusActivity.this, RechargeAccountActivity.class);
                    intent.putExtra(Constants.KEY_CITY_ID, cityId);
                    startActivity(intent);
                }else {
                    intent = new Intent(RemittanceStatusActivity.this,WithdrawDepositActivity.class);
                    intent.putExtra(Constants.KEY_IS_RECORD,true);
                    startActivity(intent);
                }
                finish();
            }
        });
    }

}
