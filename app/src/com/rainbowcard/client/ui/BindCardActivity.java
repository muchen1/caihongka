package com.rainbowcard.client.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.base.YHApplication;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.StringUtil;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;

import java.net.URLEncoder;
import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017-1-10.
 */
public class BindCardActivity extends MyBaseActivity{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;

    @InjectView(R.id.edit_card)
    EditText cardEdit;
    @InjectView(R.id.edit_password)
    EditText passwordEdit;
    @InjectView(R.id.edit_name)
    EditText nameEdit;
//    @InjectView(R.id.edit_verification)
//    EditText verificationEdit;
//    @InjectView(R.id.verification_img)
//    ImageView verificationImg;
    @InjectView(R.id.accomplish)
    Button accomplishBtn;
    @InjectView(R.id.no_card)
    TextView noCard;
    @InjectView(R.id.service_tel)
    TextView serviceTel;

    String token;

    String cardNum;
    String password;
    String name;
//    String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_card);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(BindCardActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(BindCardActivity.this,true);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(BindCardActivity.this, Constants.USERINFO, Constants.UID));
        initView();
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.bind_card));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        verificationImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getVerificationCode();
//            }
//        });
        accomplishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNext()){
                    submit();
                }
            }
        });

        noCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BindCardActivity.this,GetRainbowCardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        serviceTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        UIUtils.showConfirmDialog(BindCardActivity.this,serviceTel.getText().toString());
                    }
                }, 300);
            }
        });
    }

    private boolean isNext(){
        cardNum = cardEdit.getText().toString();
        password = passwordEdit.getText().toString();
        name = nameEdit.getText().toString();
//        verificationCode = verificationEdit.getText().toString();
        if (TextUtils.isEmpty(cardNum)) {
            UIUtils.toast("卡号为空");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            UIUtils.toast("密码为空");
            return false;
        }
        if (TextUtils.isEmpty(name)) {
            UIUtils.toast("姓名为空");
            return false;
        }
        if (password.length() != 6) {
            UIUtils.toast("密码长度不正确");
            return false;
        }
//        if (TextUtils.isEmpty(verificationCode)) {
//            UIUtils.toast("验证码不能为空");
//            return false;
//        }
//        if (verificationCode.length() < 6) {
//            UIUtils.toast("验证码长度不能少于6位");
//            return false;
//        }
        return true;
    }

    //获取手机验证码
    void getVerificationCode(){

    }

    //绑定卡片提交
    void submit(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(BindCardActivity.this, Constants.USERINFO, Constants.UID));

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("card_number",cardNum);
        parameters.put("password",password);
        parameters.put("user_name",URLEncoder.encode(name));

        withBtwVolley().load(API.API_BIND_CARD)
                .method(Request.Method.POST)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("card_number", cardNum)
                .setParam("password",password)
                .setParam("user_name",name)
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
                        UIUtils.toast("绑定成功");
                        Intent intent = new Intent(BindCardActivity.this,MyCardActivity.class);
                        intent.putExtra(Constants.KEY_USER_PHONE,MyConfig.getSharePreStr(BindCardActivity.this, Constants.USERINFO, Constants.USER_PHONE));
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onBtwError(BtwRespError<String> error) {
                        Toast.makeText(BindCardActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(BindCardActivity.this, R.string.network_error,
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
