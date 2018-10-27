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
import com.rainbowcard.client.model.AutonymModel;
import com.rainbowcard.client.model.BankInfoModel;
import com.rainbowcard.client.model.BankModel;
import com.rainbowcard.client.utils.DensityUtil;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.StringUtil;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.utils.Validation;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.squareup.picasso.Picasso;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2018-3-1.
 */
public class ModificationBankActivity extends MyBaseActivity{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;

    @InjectView(R.id.edit_card)
    EditText cardEdit;
    @InjectView(R.id.edit_phone)
    EditText phoneEdit;
    @InjectView(R.id.bank_text)
    TextView bankText;

    @InjectView(R.id.title_text)
    TextView titleTv;
    @InjectView(R.id.identity_card_text)
    TextView identivyCardTv;

    @InjectView(R.id.bank_spinner)
    Spinner bankSpinner;

    @InjectView(R.id.accomplish)
    Button accomplishBtn;

    String token;
    String autonymId;

    ArrayList<BankModel.BankEntity> bankList = new ArrayList<BankModel.BankEntity>();
    String cardNum;
    String bank;
    String bankId;
    String phone;
//    String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modification_bank);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(ModificationBankActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(ModificationBankActivity.this,true);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(ModificationBankActivity.this, Constants.USERINFO, Constants.UID));
        phone = MyConfig.getSharePreStr(ModificationBankActivity.this, Constants.USERINFO, Constants.PHONE);
        getBankList();
        getUserAutonymInfo();
        initView();
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.modification_bank));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(!TextUtils.isEmpty(phone)){
            phoneEdit.setText(phone);
        }

        accomplishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNext()){
                    submit();
                }
            }
        });

    }

    void refreshUI(AutonymModel.AutonymEntity autonymEntity){
        autonymId = autonymEntity.id;
        titleTv.setText(autonymEntity.realName);
        identivyCardTv.setText(autonymEntity.idCard);
    }

    void refreshSpinner(ArrayList <BankModel.BankEntity> mBankEntitys){
        ArrayAdapter<BankModel.BankEntity> adapter = new ArrayAdapter<BankModel.BankEntity>(this, R.layout.spinner, mBankEntitys) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.select_order_type_spinner_item, parent, false);
                }
                TextView label = (TextView) convertView.findViewById(R.id.order_type);
                label.setText(getItem(position).bankName);
                label.setTextColor(Color.BLACK);
                return convertView;
            }
        };
        bankSpinner.setAdapter(adapter);
        bankSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bankText.setText(bankList.get(position).bankName);
                bank = bankList.get(position).bankName;
                bankId = bankList.get(position).id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean isNext(){
        cardNum = cardEdit.getText().toString();
        phone = phoneEdit.getText().toString();
//        verificationCode = verificationEdit.getText().toString();
        if (TextUtils.isEmpty(cardNum)) {
            UIUtils.toast("银行卡号为空");
            return false;
        }
        if(TextUtils.isEmpty(phone)){
            UIUtils.toast("预留手机号为空");
            return false;
        }
        if(TextUtils.isEmpty(bank) || "请选择银行类型".equals(bank)){
            UIUtils.toast("请选择银行类型");
            return false;
        }
        if(!Validation.isMobile(phone)){
            UIUtils.toast("预留手机号不正确");
            return false;
        }
        return true;
    }


    //获取银行列表
    void getBankList(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(ModificationBankActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(API.API_GET_BANK_LIST)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<BankModel>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onResponse(BankModel resp) {
                        if(resp.data != null && !resp.data.isEmpty()){
                            bankList.clear();
                            bankList.addAll(resp.data);
                            BankModel bankmodel = new BankModel();
                            BankModel.BankEntity bankEntity = bankmodel.new BankEntity();
                            bankEntity.bankName = "请选择银行类型";
                            bankEntity.id = "0";
                            bankList.add(0,bankEntity);
                            refreshSpinner(bankList);
                        }
                    }

                    @Override
                    public void onBtwError(BtwRespError<BankModel> error) {
                        Toast.makeText(ModificationBankActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(ModificationBankActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(BankModel.class);
    }

    //获取用户认证信息
    void getUserAutonymInfo(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(ModificationBankActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(API.API_GET_USER_AUTONYM_INFO)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<AutonymModel>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onResponse(AutonymModel resp) {
                        if(resp.data != null) {
                            refreshUI(resp.data);
                        }
                    }

                    @Override
                    public void onBtwError(BtwRespError<AutonymModel> error) {
                        Toast.makeText(ModificationBankActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(ModificationBankActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(AutonymModel.class);
    }


    //提交修改银行卡
    void submit(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(ModificationBankActivity.this, Constants.USERINFO, Constants.UID));

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("bank_id",bankId);
        parameters.put("bank_name",URLEncoder.encode(bank));
        parameters.put("bank_card",cardNum);
        parameters.put("mobile",phone);

        withBtwVolley().load(String.format(getString(R.string.url),API.API_MODIFICATION_BANK_CARD,autonymId))
                .method(Request.Method.POST)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("bank_id",bankId)
                .setParam("bank_name",bank)
                .setParam("bank_card",cardNum)
                .setParam("mobile",phone)
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
                        UIUtils.toast("修改成功");
                        finish();
                    }

                    @Override
                    public void onBtwError(BtwRespError<String> error) {
                        Toast.makeText(ModificationBankActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(ModificationBankActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
//                        submit();
                    }
                }).excute();
    }
}
