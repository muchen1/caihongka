package com.rainbowcard.client.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.model.InsuranceChoiceModel;
import com.rainbowcard.client.model.InsuranceModelServerProxy;
import com.rainbowcard.client.model.InsurancePriceModel;
import com.rainbowcard.client.ui.adapter.InsuranceChoiceAdapter;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;

import butterknife.ButterKnife;
import butterknife.InjectView;
import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;

/**
 * 险种选择 页面
 */
public class InsuranceChoiceActivity extends MyBaseActivity implements View.OnClickListener {

    @InjectView(R.id.listview_insurance_choice)
    RecyclerView mListviewInsuranceChoice;
    @InjectView(R.id.nav_back)
    RelativeLayout mBackBtn;
    @InjectView(R.id.midle_title)
    TextView mHeaderTitle;
    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.bt_insurance_get_price)
    TextView mBtGetPrice;

    private InsuranceChoiceModel mInsuranceChoiceModel;

    private InsuranceChoiceAdapter mListAdapter;

    private String mCarNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_insurance_choice);
        parseIntent(getIntent());
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(InsuranceChoiceActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(InsuranceChoiceActivity.this,true);
        initView();
        initData();
    }

    private void initView() {
        mHeaderTitle.setVisibility(View.VISIBLE);
        mHeaderTitle.setText(R.string.insurance_choice_text);
        mHeaderTitle.setTextColor(getResources().getColor(R.color.vpi__background_holo_dark));
        mHeadControlPanel.setMyBackgroundColor(getResources().getColor(R.color.white));
        mBackBtn.setVisibility(View.VISIBLE);
        mBackBtn.setOnClickListener(this);
        mBtGetPrice.setOnClickListener(this);
        mListviewInsuranceChoice.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initData() {
        mInsuranceChoiceModel = InsuranceModelServerProxy.getInstance().getInsuranceChoiceModel(mCarNum);
        if (mInsuranceChoiceModel != null) {
            mListAdapter = new InsuranceChoiceAdapter(InsuranceChoiceActivity.this,
                    mInsuranceChoiceModel.data);
            mListviewInsuranceChoice.setAdapter(mListAdapter);
        }

    }

    private void parseIntent(Intent intent) {
        if (intent != null) {
            mCarNum = intent.getStringExtra("carNum");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_back:
                finish();
                break;
            case R.id.bt_insurance_get_price:
                // 跳转到报价页面
                break;
        }
    }
}
