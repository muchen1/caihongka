package com.rainbowcard.client.ui;

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
import com.rainbowcard.client.utils.StringUtil;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.utils.Validation;
import com.rainbowcard.client.widget.HeadControlPanel;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2018-3-1.
 */
public class RemittanceInfoActivity extends MyBaseActivity{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;

    @InjectView(R.id.edit_remittance_price)
    EditText remittancePriceEdit;
    @InjectView(R.id.edit_remittance_number)
    EditText remittanceNumberEdit;
    @InjectView(R.id.edit_remark)
    EditText remarkEdit;
    @InjectView(R.id.edit_remittance_name)
    EditText remittanceNameEdit;

    @InjectView(R.id.accomplish)
    Button accomplishBtn;

    String token;

    String remittancePrice;
    String remittanceNumber;
    String remittanceName;
    String remark;
    String tradeNo;
    String cityId;
    int price;

    private int isReturnFreeWashOrder;  //判断充值成功后是否返回免费洗车券下单   为1返回

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remittance_info);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(RemittanceInfoActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(RemittanceInfoActivity.this,true);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(RemittanceInfoActivity.this, Constants.USERINFO, Constants.UID));
        tradeNo = getIntent().getStringExtra(Constants.KEY_TRADE_NO);
        cityId = getIntent().getStringExtra(Constants.KEY_CITY_ID);
        price = getIntent().getIntExtra(Constants.KEY_ACTIVITY_MONEY,0);
        isReturnFreeWashOrder = getIntent().getIntExtra(Constants.KEY_IS_RETURN_FREE_WACH_ORDER,0);
        initView();
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.remittance_info));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        accomplishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNext()){
                    submitRemittanceInfo();
                }
            }
        });

        remittancePriceEdit.setText(String.valueOf(price));

    }

    private boolean isNext(){
        remittancePrice = remittancePriceEdit.getText().toString();
        remittanceNumber = remittanceNumberEdit.getText().toString();
        remark = remarkEdit.getText().toString();
        remittanceName = remittanceNameEdit.getText().toString();
        if (TextUtils.isEmpty(remittancePrice)) {
            UIUtils.toast("汇款金额不能为空");
            return false;
        }
        if(remittancePrice.equals("0")){
            UIUtils.toast("汇款金额不能为零");
            return false;
        }
        if (TextUtils.isEmpty(remittanceNumber)) {
            UIUtils.toast("汇款银行卡号不能为空");
            return false;
        }
        if(TextUtils.isEmpty(remittanceName)){
            UIUtils.toast("汇款人不能为空");
            return false;
        }
        return true;
    }


    //提交汇款信息
    void submitRemittanceInfo(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(RemittanceInfoActivity.this, Constants.USERINFO, Constants.UID));

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("trade_no",tradeNo);
        parameters.put("pay_money",remittancePrice);
        parameters.put("pay_account",remittanceNumber);
        parameters.put("pay_name", URLEncoder.encode(remittanceName));
        parameters.put("pay_bak",URLEncoder.encode(remark));

        withBtwVolley().load(API.API_SUBMIT_REMITTANCE_INFO)
                .method(Request.Method.POST)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("trade_no",tradeNo)
                .setParam("pay_money",remittancePrice)
                .setParam("pay_account",remittanceNumber)
                .setParam("pay_name",remittanceName)
                .setParam("pay_bak",remark)
                .setParam("paramSign", StringUtil.createParameterSign(parameters))
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<String>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(String resp) {
                        Intent intent = new Intent(RemittanceInfoActivity.this,RemittanceStatusActivity.class);
                        intent.putExtra(Constants.KEY_CITY_ID,cityId);
                        intent.putExtra(Constants.KEY_IS_REMITTANCE_STATUS,1);
                        intent.putExtra(Constants.KEY_IS_RETURN_FREE_WACH_ORDER,isReturnFreeWashOrder);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onBtwError(BtwRespError<String> error) {
                        Toast.makeText(RemittanceInfoActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(RemittanceInfoActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute();
    }
}
