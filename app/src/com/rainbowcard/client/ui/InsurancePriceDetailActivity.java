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
import com.rainbowcard.client.model.InsuranceModelServerProxy;
import com.rainbowcard.client.model.InsurancePriceDetailModel;
import com.rainbowcard.client.model.InsurancePriceModel;
import com.rainbowcard.client.ui.adapter.InsurancePriceDetailAdapter;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.squareup.picasso.Picasso;

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

    private int mCompanyId;

    private InsurancePriceDetailModel mPriceDetailModel;

    private InsurancePriceDetailAdapter mPriceDetailAdapter;


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

        mHeaderTitle.setVisibility(View.VISIBLE);
        mHeaderTitle.setText(R.string.insurance_price_detail_title_text);
        mHeaderTitle.setTextColor(getResources().getColor(R.color.vpi__background_holo_dark));
        mHeadControlPanel.setMyBackgroundColor(getResources().getColor(R.color.white));
        mBackBtn.setVisibility(View.VISIBLE);
        mBackBtn.setOnClickListener(this);

        Picasso.with(this)
                .load(mPriceDetailModel.companyIcon)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.detail_default)
                .error(R.drawable.detail_default).into(mInsuranceCompanyIcon);

        mBottomPriceNew.setText(mPriceDetailModel.priceNew);
        mBottomPriceOld.setText(mPriceDetailModel.priceOld);

        mInpriceHeaderArrow.setOnClickListener(this);
        mInpriceHeaderEdit.setOnClickListener(this);
        mBottomNext.setOnClickListener(this);

        mInpriceList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initData() {
        mPriceDetailAdapter = new InsurancePriceDetailAdapter(this, mPriceDetailModel.priceDatas);
        mInpriceList.setAdapter(mPriceDetailAdapter);
    }

    private void parseIntent(Intent intent) {
        if (intent != null) {
            mCompanyId = intent.getIntExtra("companyId", -1);
        }
        if (mCompanyId == -1) {
            finish();
        }
        mPriceDetailModel = InsuranceModelServerProxy.getInstance().getInsurancePriceDetailModel(mCompanyId);
        if (mPriceDetailModel == null) {
            finish();
        }
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
            case R.id.nav_back:
                // 点击back
                finish();
            default:
                break;
        }
    }
}
