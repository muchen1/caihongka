package com.rainbowcard.client.ui;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.utils.Validation;
import com.rainbowcard.client.widget.HeadControlPanel;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2018-3-5.
 */
public class BankTransferActivity extends MyBaseActivity{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;

    @InjectView(R.id.account_info)
    TextView accountInfo;
    @InjectView(R.id.copy_account)
    Button copyAccount;
    @InjectView(R.id.bank_info)
    TextView bankInfo;
    @InjectView(R.id.copy_bank)
    Button copyBank;
    @InjectView(R.id.number_info)
    TextView numberInfo;
    @InjectView(R.id.copy_number)
    Button copyNumber;

    @InjectView(R.id.accomplish)
    Button accomplishBtn;

    String token;

    String cityId;
    String tradeNo;
    int price;

    private int isReturnFreeWashOrder;  //判断充值成功后是否返回免费洗车券下单  为1返回
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_transfer);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(BankTransferActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(BankTransferActivity.this,true);
        tradeNo = getIntent().getStringExtra(Constants.KEY_TRADE_NO);
        cityId = getIntent().getStringExtra(Constants.KEY_CITY_ID);
        price = getIntent().getIntExtra(Constants.KEY_ACTIVITY_MONEY,0);
        isReturnFreeWashOrder = getIntent().getIntExtra(Constants.KEY_IS_RETURN_FREE_WACH_ORDER,0);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(BankTransferActivity.this, Constants.USERINFO, Constants.UID));
        initView();
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.bank_transfer));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        copyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(accountInfo.getText());
                UIUtils.toast("复制成功");
            }
        });
        copyBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(bankInfo.getText());
                UIUtils.toast("复制成功");
            }
        });
        copyNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(numberInfo.getText());
                UIUtils.toast("复制成功");
            }
        });

        accomplishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BankTransferActivity.this,RemittanceInfoActivity.class);
                intent.putExtra(Constants.KEY_CITY_ID,cityId);
                intent.putExtra(Constants.KEY_TRADE_NO,tradeNo);
                intent.putExtra(Constants.KEY_ACTIVITY_MONEY,price);
                intent.putExtra(Constants.KEY_IS_RETURN_FREE_WACH_ORDER,isReturnFreeWashOrder);
                startActivity(intent);
            }
        });

    }

}
