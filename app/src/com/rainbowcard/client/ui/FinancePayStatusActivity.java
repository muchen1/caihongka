package com.rainbowcard.client.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.model.FinanceOrderModel;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;


import java.text.DecimalFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2018-3-8.
 */
public class FinancePayStatusActivity extends MyBaseActivity {

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;

    @InjectView(R.id.tv_money)
    TextView moneyTv;
    @InjectView(R.id.tv_deadline)
    TextView deadlineTv;
    @InjectView(R.id.tv_free_wash)
    TextView freeWashTv;
    @InjectView(R.id.tv_date)
    TextView dateTv;

    @InjectView(R.id.participation)
    TextView participation;

    @InjectView(R.id.hint_text)
    TextView hintText;

    @InjectView(R.id.return_btn)
    Button returnBtn;
    @InjectView(R.id.continue_btn)
    Button continueBtn;

    FinanceOrderModel.FinanceOrderEntity financeOrderEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance_order_succeed);
        ButterKnife.inject(this);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        UIUtils.setMiuiStatusBarDarkMode(FinancePayStatusActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(FinancePayStatusActivity.this,true);

        financeOrderEntity = (FinanceOrderModel.FinanceOrderEntity) getIntent().getSerializableExtra(Constants.KEY_FINANCE_ORDER_ENTITY);

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
                finish();
            }
        });


        moneyTv.setText(String.format(getString(R.string.price),new
                DecimalFormat("##,###,###,###,##0.00").format(financeOrderEntity.money)));
        deadlineTv.setText(String.format(getString(R.string.period_text),financeOrderEntity.lockDays));
        freeWashTv.setText(String.format(getString(R.string.discount_count),financeOrderEntity.financeCoupon));
        dateTv.setText(financeOrderEntity.returnTime);


        hintText.setText(String.format(getString(R.string.hint_text2),financeOrderEntity.financeCoupon));
        Spannable span = new SpannableString(hintText.getText());
        span.setSpan(new AbsoluteSizeSpan(58), 0,financeOrderEntity.financeCoupon.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.money_color)), 0,financeOrderEntity.financeCoupon.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            span.setSpan(new BackgroundColorSpan(Color.YELLOW), 11, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        hintText.setText(span);

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FinancePayStatusActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FinancePayStatusActivity.this,FreeWashTrialActivity.class);
                startActivity(intent);
                finish();
            }
        });

        participation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FinancePayStatusActivity.this,InviteActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
