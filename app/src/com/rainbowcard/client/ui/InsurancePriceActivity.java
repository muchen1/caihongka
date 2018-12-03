package com.rainbowcard.client.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
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
import com.rainbowcard.client.model.InsuranceChoiceModel;
import com.rainbowcard.client.model.InsuranceModelServerProxy;
import com.rainbowcard.client.model.InsurancePriceModel;
import com.rainbowcard.client.ui.adapter.InsurancePriceListAdapter;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    private HashMap<String, String> mInsuranceChoiceKey = new HashMap<>();

    private StringBuilder mSelectInsuranceTextBuilder = new StringBuilder();

    private int mSelectInsuranceNum = 0;

    private String priceurl = "http://118.24.202.95:5000/bx/api/getBXPrice";


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
        // 发送price请求，这个请求只发送一个，等该请求返回之后，再发送几个请求来请求价格
        sendPriceRequest();
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
        // 如果打开了交强险的开关
        if (mInsuranceChoiceModel.jqxSwitch) {
            mSelectInsuranceTextBuilder.append("交强险，车船险，");
            mSelectInsuranceNum += 2;
            mInsuranceChoiceKey.put("ForceTax", "1");
        }
        if (mInsuranceChoiceModel.syxSwitch) {
            // 遍历选择的险种信息，为mInsuranceChoiceKey;mSelectInsuranceTextBuilder赋值
            for (int i = 0; i < mInsuranceChoiceModel.data.size(); i++) {
                InsuranceChoiceModel.ChildItemEntity entity = mInsuranceChoiceModel.data.get(i);
                // 主险种是选中状态
                if (entity.insuranceStatus && entity.insuranceStatusTextKey != 0) {
                    mInsuranceChoiceKey.put(entity.insuranceKey, entity.insuranceStatusTextKey + "");
                    mSelectInsuranceTextBuilder.append(entity.insuranceName);
                    if (entity.insuranceStatusTextKey > 5) {
                        mSelectInsuranceTextBuilder.append("(")
                                .append(entity.insuranceAllPrice.get(entity.insuranceStatusTextKey))
                                .append(")");
                    }
                    mSelectInsuranceTextBuilder.append("，");
                    mSelectInsuranceNum++;
                }
                // 不计免赔是否选中状态
                if (entity.insuranceBjmpStatus == 1) {
                    mInsuranceChoiceKey.put(entity.insuranceBjmpKey, "1");
                    mSelectInsuranceTextBuilder.append(entity.insuranceName).append("(不计免赔)").append("，");
                    mSelectInsuranceNum++;
                }
            }
        }

        mItemChoiceInsuranceNum.setText(String.format(getString(R.string.insurance_price_choice_num_format),
                mSelectInsuranceNum));

        String temp = mSelectInsuranceTextBuilder.toString();
        if (!TextUtils.isEmpty(temp)) {
            mItemChoiceInsuranceText.setText(temp.substring(0, temp.lastIndexOf("，")));
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

    private void sendPriceRequest() {
        String token = String.format(getString(R.string.token),
                MyConfig.getSharePreStr(InsurancePriceActivity.this,
                        Constants.USERINFO, Constants.UID));
        HashMap<String, String> params = new HashMap<>();
        params.putAll(mInsuranceChoiceKey);
        params.put("LicenseNo", mInsurancePriceModel.userInfoEntity.licenseNo);
        params.put("CarOwnersName", mInsurancePriceModel.userInfoEntity.licenseOwner);
        params.put("CarVin", mInsurancePriceModel.userInfoEntity.CarVin);
        params.put("EngineNo", mInsurancePriceModel.userInfoEntity.engineNo);
        params.put("HolderIdCard", mInsurancePriceModel.userInfoEntity.holderIdCard);
        // todo daipeng传入什么
        params.put("HolderIdName", mInsurancePriceModel.userInfoEntity.postedName);
        params.put("HolderIdType", mInsurancePriceModel.userInfoEntity.holderIdType + "");
        // todo daipeng传入什么
        params.put("IdCard", mInsurancePriceModel.userInfoEntity.credentislasNum);
        params.put("InsuredIdCard", mInsurancePriceModel.userInfoEntity.insuredIdCard);
        params.put("InsuredIdType", mInsurancePriceModel.userInfoEntity.insuredIdType + "");
        params.put("InsuredName", mInsurancePriceModel.userInfoEntity.insuredName);
        params.put("InsuredIdCard", mInsurancePriceModel.userInfoEntity.insuredIdCard);
        params.put("MoldName", mInsurancePriceModel.userInfoEntity.modleName);
        params.put("OwnerIdCardType", mInsurancePriceModel.userInfoEntity.idType + "");
        params.put("RegisterDate", mInsurancePriceModel.userInfoEntity.registerDate);
        params.put("QuoteGroup", "4");
        params.put("city", "1");

        withBtwVolley().load(priceurl)
                .method(Request.Method.POST)
                .setParams(params)
                .setHeader("Authorization", token)
                .setHeader("Accept", API.VERSION)
                .setRetrys(0)
                .setResponseHandler(new BtwVolley.ResponseHandler<String>() {
                    @Override
                    public void onBtwError(BtwRespError<String> error) {
                        Log.e("daipeng", "onBtwError==" + error.errorMessage);
                        Log.e("daipeng", "onBtwError==" + error.errorCode);
                        Log.e("daipeng", "onBtwError==" + error.result);
                        Toast.makeText(InsurancePriceActivity.this, "服务异常，请稍后重试", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(InsurancePriceActivity.this, "请检查网络配置", Toast.LENGTH_LONG);

                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }

                    @Override
                    public void onResponse(String resp) {

                    }

                    @Override
                    public void onStart() {
                    }
                }).excute();
    }
}
