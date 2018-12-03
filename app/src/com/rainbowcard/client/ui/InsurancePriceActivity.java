package com.rainbowcard.client.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.model.InsuranceChoiceModel;
import com.rainbowcard.client.model.InsuranceModelServerProxy;
import com.rainbowcard.client.model.InsurancePriceModel;
import com.rainbowcard.client.ui.adapter.InsurancePriceListAdapter;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;

import java.util.HashMap;

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

    private String mCarNum;

    private InsurancePriceModel mInsurancePriceModel;

    private InsuranceChoiceModel mInsuranceChoiceModel;

    private InsurancePriceListAdapter mInsurancePriceListAdapter;

    private HashMap<String, Integer> mInsuranceChoiceKey = new HashMap<>();

    private StringBuilder mSelectInsuranceTextBuilder = new StringBuilder();

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
        mHeaderTitle.setVisibility(View.VISIBLE);
        mHeaderTitle.setText(R.string.insurance_price_title_text);
        mHeaderTitle.setTextColor(getResources().getColor(R.color.vpi__background_holo_dark));
        mHeadControlPanel.setMyBackgroundColor(getResources().getColor(R.color.white));
        mBackBtn.setVisibility(View.VISIBLE);
        mBackBtn.setOnClickListener(this);

        mItemHeaderEditIcon.setOnClickListener(this);
        mItemHeaderEditText.setOnClickListener(this);
        mItemChoiceInsuranceEditBt.setOnClickListener(this);
        mItemInsruancePriceList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initData() {
        mInsuranceChoiceModel = InsuranceModelServerProxy.getInstance().getInsuranceChoiceModel(mCarNum);
        mInsurancePriceModel = InsuranceModelServerProxy.getInstance().getInsurancePriceModel(mCarNum);
        if (mInsurancePriceModel != null) {
            mInsurancePriceListAdapter = new InsurancePriceListAdapter(this, mInsurancePriceModel.listData);
            mItemInsruancePriceList.setAdapter(mInsurancePriceListAdapter);
        }
        mItemHeaderCarnum.setText(mCarNum);
        mItemHeaderCarperson.setText(mInsurancePriceModel.userInfoEntity.insuredName);
        mItemHeaderCartype.setText(mInsurancePriceModel.userInfoEntity.modleName);

        mItemDateJqxText.setText(mInsurancePriceModel.userInfoEntity.nextForceStartDate);
        mItemDateSyxText.setText(mInsurancePriceModel.userInfoEntity.nextBusinessStartDate);
        // 遍历选择的险种信息，为mInsuranceChoiceKey;mSelectInsuranceTextBuilder赋值
        for (int i = 0; i < mInsuranceChoiceModel.data.size(); i++) {
            InsuranceChoiceModel.ChildItemEntity entity = mInsuranceChoiceModel.data.get(i);
            // 主险种是选中状态
            if (entity.insuranceStatus && entity.insuranceStatusTextKey != 0) {
                mInsuranceChoiceKey.put(entity.insuranceKey, entity.insuranceStatusTextKey);
                mSelectInsuranceTextBuilder.append(entity.insuranceName);
                if (entity.insuranceStatusTextKey > 5) {
                    mSelectInsuranceTextBuilder.append("(")
                            .append(entity.insuranceAllPrice.get(entity.insuranceStatusTextKey))
                            .append(")");
                }
                mSelectInsuranceTextBuilder.append("，");
            }
            // 不计免赔是否选中状态
            if (entity.insuranceBjmpStatus == 1) {
                mInsuranceChoiceKey.put(entity.insuranceBjmpKey, 1);
                mSelectInsuranceTextBuilder.append(entity.insuranceName).append("(不计免赔)").append("，");
            }
        }

        mItemChoiceInsuranceNum.setText(String.format(getString(R.string.insurance_price_choice_num_format),
                mInsuranceChoiceKey.size()));

        String temp = mSelectInsuranceTextBuilder.toString();
        mItemChoiceInsuranceText.setText(temp.substring(0, temp.lastIndexOf("，")));

    }

    private void parseIntent(Intent intent) {
        if (intent != null) {
            mCarNum = intent.getStringExtra("carNum");
        }
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
            case R.id.nav_back:
                // 点击back
                finish();
                break;
            default:
                break;
        }
    }
}
