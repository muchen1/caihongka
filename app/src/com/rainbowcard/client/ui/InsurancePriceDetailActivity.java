package com.rainbowcard.client.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class InsurancePriceDetailActivity extends MyBaseActivity implements View.OnClickListener {
    @InjectView(R.id.nav_back)
    RelativeLayout mBackBtn;
    @InjectView(R.id.midle_title)
    TextView mHeaderTitle;
    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.inprice_detail_header_companyicon)
    ImageView mInsuranceCompanyIcon;
    @InjectView(R.id.inprice_detail_header_arrow)
    ImageView mInpriceHeaderArrow;
    @InjectView(R.id.inprice_detail_header_text)
    TextView mInpriceHeaderEdit;

    @InjectView(R.id.inprice_bottom_price_new)
    TextView mBottomPriceNew;
    @InjectView(R.id.inprice_bottom_price_old)
    TextView mBottomPriceOld;
    @InjectView(R.id.inprice_bottom_price_next)
    TextView mBottomNext;

    @InjectView(R.id.inprice_list)
    RecyclerView mInpriceList;

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
        mInpriceHeaderArrow.setOnClickListener(this);
        mInpriceHeaderEdit.setOnClickListener(this);
        mBottomNext.setOnClickListener(this);

        mInpriceList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initData() {

    }

    private void parseIntent(Intent intent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 点击调整保险项目，跳转回去
            case R.id.inprice_detail_header_arrow:
            case R.id.inprice_detail_header_text:
                break;
            // 点击下一步
            case R.id.inprice_bottom_price_next:
                break;
            default:
                break;
        }
    }
}
