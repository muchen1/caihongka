package com.rainbowcard.client.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class InsurancePriceDetailActivity extends MyBaseActivity {
    @InjectView(R.id.nav_back)
    RelativeLayout mBackBtn;
    @InjectView(R.id.midle_title)
    TextView mHeaderTitle;
    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_insurance_price_detail);
        parseIntent(getIntent());
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(InsurancePriceDetailActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(InsurancePriceDetailActivity.this,true);
        initView();
        initData();
    }

    private void initView() {

    }

    private void initData() {

    }

    private void parseIntent(Intent intent) {

    }
}
