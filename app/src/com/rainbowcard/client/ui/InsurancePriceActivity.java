package com.rainbowcard.client.ui;

import android.content.Intent;
import android.os.Bundle;
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

/**
 * 保险报价页面
 */

public class InsurancePriceActivity extends MyBaseActivity implements View.OnClickListener {

    @InjectView(R.id.nav_back)
    RelativeLayout mBackBtn;
    @InjectView(R.id.midle_title)
    TextView mHeaderTitle;
    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.item_header_carnum)
    TextView mItemHeaderCarnum;
    @InjectView(R.id.item_header_carperson_value)
    TextView mItemHeaderCarperson;
    @InjectView(R.id.item_header_carperson_cartype)
    TextView mItemHeaderCartype;
    @InjectView(R.id.item_header_changebutton_arrow)
    ImageView mItemHeaderEditIcon;
    @InjectView(R.id.item_header_changebutton)
    TextView mItemHeaderEditText;
    @InjectView(R.id.item_date_jqx_text)
    TextView mItemDateJqxText;
    @InjectView(R.id.item_date_syx_text)
    TextView mItemDateSyxText;
    @InjectView(R.id.item_choice_insurance_num)
    TextView mItemChoiceInsuranceNum;
    @InjectView(R.id.item_choice_insurance_text)
    TextView mItemChoiceInsuranceText;
    @InjectView(R.id.item_choice_insurance_edit)
    ImageView mItemChoiceInsuranceEditBt;
    @InjectView(R.id.item_insurance_price_list)
    RecyclerView mItemInsruancePriceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_insurance_price);
        parseIntent(getIntent());
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(InsurancePriceActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(InsurancePriceActivity.this,true);
        initView();
        initData();
    }

    private void initView() {
        mItemHeaderEditIcon.setOnClickListener(this);
        mItemHeaderEditText.setOnClickListener(this);
        mItemChoiceInsuranceEditBt.setOnClickListener(this);
    }

    private void initData() {

    }

    private void parseIntent(Intent intent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_header_changebutton_arrow:
            case R.id.item_header_changebutton:
                // 点击编辑车辆信息，跳转回上一个页面
                break;
            case R.id.item_choice_insurance_edit:
                // 点击已选险种的编辑，跳转回上一个页面
                break;
            default:
                break;
        }
    }
}
