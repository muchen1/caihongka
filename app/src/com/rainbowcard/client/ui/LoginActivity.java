package com.rainbowcard.client.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.rainbowcard.client.model.TokenModel;
import com.rainbowcard.client.utils.ASEUtil;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.PhoneSignUtil;
import com.rainbowcard.client.utils.StringUtil;
import com.rainbowcard.client.utils.TimeUtil;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.utils.Validation;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.renn.rennsdk.codec.Encoder;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017-1-10.
 */
public class LoginActivity extends MyBaseActivity{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;

    @InjectView(R.id.verification_login)
    LinearLayout verificationLogin;
    @InjectView(R.id.v_edit_phone)
    EditText vPhoneEdit;
    @InjectView(R.id.edit_verification)
    EditText verificationEdit;
    @InjectView(R.id.verification_btn)
    Button verificationBtn;

    @InjectView(R.id.password_login)
    LinearLayout passwordLogin;
    @InjectView(R.id.p_edit_phone)
    EditText pPhoneEdit;
    @InjectView(R.id.edit_password)
    EditText passwordEdit;

    @InjectView(R.id.find_password)
    TextView findPassword;
    @InjectView(R.id.login_btn)
    Button loginBtn;
    @InjectView(R.id.regist_btn)
    TextView registBtn;
    @InjectView(R.id.login_text)
    TextView loginText;
    @InjectView(R.id.voice_verify)
    TextView voiceVerify;

    boolean isPassword = false;

    String phoneNum;
    String verificationCode;
    String password;

    public static long GET_CODE_TIME;
    private final static int MSG_COUNT = 99;
    private final static int YYMSG_COUNT = 199;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @SuppressWarnings("deprecation")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_COUNT) {
                long x = 60 - (System.currentTimeMillis() - GET_CODE_TIME)
                        / TimeUtil.SECOND;
                if (x > 0) {
                    if(verificationBtn != null) {
                        verificationBtn.setText(x + "s");
                        verificationBtn.setTextColor(getResources().getColor(R.color.gray));
                        verificationBtn.setBackgroundResource(R.drawable.bg_edittext_regist);
                        verificationBtn.setEnabled(false);
                        voiceVerify.setEnabled(false);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(MSG_COUNT);
                            }
                        }, 1000);
                    }
                } else {
                    if(verificationBtn != null) {
                        verificationBtn.setText("获取验证码");
                        verificationBtn.setTextColor(getResources().getColor(R.color.white));
                        verificationBtn.setBackgroundResource(R.drawable.query_select_item);
                        verificationBtn.setEnabled(true);
                        voiceVerify.setEnabled(true);
                    }
                }
            }
            if (msg.what == YYMSG_COUNT) {
                long x = 60 - (System.currentTimeMillis() - GET_CODE_TIME)
                        / TimeUtil.SECOND;
                if (x > 0) {
                    if(verificationBtn != null) {
                        verificationBtn.setEnabled(false);
                        voiceVerify.setEnabled(false);
                        voiceVerify.setText("语音验证码已发送，请注意接听（" + x + "s）");
                        voiceVerify.setTextColor(getResources().getColor(R.color.gray));
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(YYMSG_COUNT);
                            }
                        }, 1000);
                    }
                } else {
                    if(verificationBtn != null) {
                        verificationBtn.setEnabled(true);
                        voiceVerify.setEnabled(true);
                        voiceVerify.setText("收不到短信？点此采用电话语音接收");
                        Spannable span2 = new SpannableString(voiceVerify.getText());
                        span2.setSpan(null, 6, voiceVerify.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        span2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.stroke)), 6, voiceVerify.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        voiceVerify.setText(span2);
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(LoginActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(LoginActivity.this,true);
        initView();
//        Log.d("GCCCCCCCC??>>>", ASEUtil.Encrypt("123","caihong_9527_Key"));
    }

    void initView(){
        phoneNum = MyConfig.getSharePreStr(LoginActivity.this, Constants.USERINFO, Constants.PHONE);
        if(!TextUtils.isEmpty(phoneNum)){
            pPhoneEdit.setText(phoneNum);
            vPhoneEdit.setText(phoneNum);
        }
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.user_login));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        verificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toSendVerify()) {
                    getVerificationCode(1);
                }
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPassword){
                    if(passwordIsNext()){
                        submit();
                    }
                }else {
                    if (isNext()) {
                        smsSubmit();
                    }
                }
            }
        });
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPassword){
                    verificationLogin.setVisibility(View.VISIBLE);
                    passwordLogin.setVisibility(View.INVISIBLE);
                    findPassword.setVisibility(View.INVISIBLE);
                    voiceVerify.setVisibility(View.VISIBLE);
                    loginText.setText("密码登录");
                }else {
                    verificationLogin.setVisibility(View.INVISIBLE);
                    passwordLogin.setVisibility(View.VISIBLE);
                    findPassword.setVisibility(View.VISIBLE);
                    voiceVerify.setVisibility(View.INVISIBLE);
                    loginText.setText("验证码登录");
                }
                isPassword = !isPassword;
            }
        });
        findPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,FindPasswordActivity.class);
                startActivity(intent);
            }
        });

        Spannable span2 = new SpannableString(voiceVerify.getText());
//        span.setSpan(new AbsoluteSizeSpan(58), 6, agreement.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span2.setSpan(null, 6, voiceVerify.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.stroke)), 6, voiceVerify.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            span.setSpan(new BackgroundColorSpan(Color.YELLOW), 11, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        voiceVerify.setText(span2);
        voiceVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toSendVerify()) {
                    getVerificationCode(2);
                }
            }
        });

        registBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegistActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean toSendVerify() {
        phoneNum = vPhoneEdit.getText().toString();
        if (TextUtils.isEmpty(phoneNum)) {
            UIUtils.toast("手机号不能为空");
            return false;
        }
        if (!Validation.isMobile(phoneNum)) {
            UIUtils.toast("手机号格式不正确");
            return false;
        }
        return true;
    }

    private boolean isNext(){
        phoneNum = vPhoneEdit.getText().toString();
        verificationCode = verificationEdit.getText().toString();
        if (TextUtils.isEmpty(phoneNum)) {
            UIUtils.toast(getString(R.string.number_null));
            return false;
        }
        if (!Validation.isMobile(phoneNum)) {
            UIUtils.toast(getString(R.string.number_illegal));
            return false;
        }
        if (TextUtils.isEmpty(verificationCode)) {
            UIUtils.toast("验证码不能为空");
            return false;
        }
        if (verificationCode.length() != 4) {
            UIUtils.toast("验证码长度应为4位");
            return false;
        }
        return true;
    }
    private boolean passwordIsNext(){
        phoneNum = pPhoneEdit.getText().toString();
        password = passwordEdit.getText().toString();
        if (TextUtils.isEmpty(phoneNum)) {
            UIUtils.toast(getString(R.string.number_null));
            return false;
        }
        if (!Validation.isMobile(phoneNum)) {
            UIUtils.toast(getString(R.string.number_illegal));
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            UIUtils.toast("密码不能为空");
            return false;
        }
        if (password.length() < 6) {
            UIUtils.toast("密码长度不能少于6位");
            return false;
        }
        return true;
    }

    //获取手机验证码
    void getVerificationCode(final int verifyType){

        long timestamp = System.currentTimeMillis() / 1000;
        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("phone",phoneNum);
        parameters.put("type",verifyType);
        parameters.put("timestamp",timestamp);
        String sign = StringUtil.createSign(parameters);
        parameters.put("sign",sign);


        withBtwVolley().load(API.API_GET_LOGIN_SMS)
                .method(Request.Method.POST)
                .setHeader("Accept", API.VERSION)
                .setParam("phone", phoneNum)
                .setParam("type",verifyType)
                .setParam("timestamp",timestamp)
                .setParam("sign", sign)
                .setParam("paramSign",StringUtil.createParameterSign(parameters))
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
                        GET_CODE_TIME = System.currentTimeMillis();
                        if(verifyType == 1) {
                            handler.sendEmptyMessage(MSG_COUNT);
                        }else {
                            handler.sendEmptyMessage(YYMSG_COUNT);
                        }
                    }

                    @Override
                    public void onBtwError(BtwRespError<String> error) {
                        Toast.makeText(LoginActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(LoginActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute();
    }

    //密码登陆提交
    void submit(){
//        String encodePassword = null;
//        try {
//            URLEncoder.encode(encodePassword = URLEncoder.encode(ASEUtil.Encrypt(password,API.ENCRYPT_KEY)));
//        ASEUtil.Encrypt(password,API.ENCRYPT_KEY).replace("/","\\/")
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        long timestamp = System.currentTimeMillis() / 1000;
        String encryptPassword = URLEncoder.encode(ASEUtil.Encrypt(password,API.ENCRYPT_KEY));
        String phoneSign = URLEncoder.encode(PhoneSignUtil.getDeviceId(LoginActivity.this));
        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("phone",phoneNum);
        parameters.put("timestamp",timestamp);
        parameters.put("password",encryptPassword);
        parameters.put("encrypt",1);
        parameters.put("device_id",phoneSign);
        String sign = StringUtil.createSign(parameters);
        parameters.put("sign",sign);

        withBtwVolley().load(API.API_PASSWORD_LOGIN)
                .method(Request.Method.POST)
                .setHeader("Accept", API.VERSION)
                .setParam("phone", phoneNum)
//                .setParam("password", URLEncoder.encode(ASEUtil.Encrypt(password,API.ENCRYPT_KEY)))
                .setParam("password", ASEUtil.Encrypt(password,API.ENCRYPT_KEY))
                .setParam("encrypt",1)
                .setParam("timestamp",timestamp)
                .setParam("device_id",PhoneSignUtil.getDeviceId(LoginActivity.this))
                .setParam("sign", sign)
                .setParam("paramSign",StringUtil.createParameterSign(parameters))
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<TokenModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(TokenModel resp) {
                        MyConfig.putSharePre(LoginActivity.this, Constants.USERINFO, Constants.UID, resp.data.token);
                        MyConfig.putSharePre(LoginActivity.this, Constants.USERINFO, Constants.PHONE, phoneNum);
                        addDeviceToken(String.format(getString(R.string.token), resp.data.token));
                    }

                    @Override
                    public void onBtwError(BtwRespError<TokenModel> error) {
                        Toast.makeText(LoginActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(LoginActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(TokenModel.class);
    }
    //短信验证登陆提交
    void smsSubmit(){

        long timestamp = System.currentTimeMillis() / 1000;
        String phoneSign = URLEncoder.encode(PhoneSignUtil.getDeviceId(LoginActivity.this));
        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("phone",phoneNum);
        parameters.put("timestamp",timestamp);
        parameters.put("sms_code",verificationCode);
        parameters.put("device_id",phoneSign);
        String sign = StringUtil.createSign(parameters);
        parameters.put("sign",sign);

        withBtwVolley().load(API.API_SMS_LOGIN)
                .method(Request.Method.POST)
                .setHeader("Accept", API.VERSION)
                .setParam("phone", phoneNum)
                .setParam("sms_code",verificationCode)
                .setParam("timestamp",timestamp)
                .setParam("device_id",PhoneSignUtil.getDeviceId(LoginActivity.this))
                .setParam("sign", sign)
                .setParam("paramSign",StringUtil.createParameterSign(parameters))
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<TokenModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(TokenModel resp) {
                        MyConfig.putSharePre(LoginActivity.this, Constants.USERINFO, Constants.UID, resp.data.token);
                        MyConfig.putSharePre(LoginActivity.this, Constants.USERINFO, Constants.PHONE, phoneNum);
                        addDeviceToken(String.format(getString(R.string.token), resp.data.token));

                        Log.d("GCCCCCCCCCC???????token=",resp.data.token);
//                        finish();
                    }

                    @Override
                    public void onBtwError(BtwRespError<TokenModel> error) {
                        Toast.makeText(LoginActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(LoginActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(TokenModel.class);
    }

    //添加设备token
    void addDeviceToken(String token){

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("appType",API.API_CLIENT_ID);
        parameters.put("deviceToken",YHApplication.instance().getDeviceToken());

        withBtwVolley().load(API.API_SUB_DEVICE_TOKEN)
                .method(Request.Method.POST)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION2D11)
                .setParam("appType", API.API_CLIENT_ID)
                .setParam("deviceToken", YHApplication.instance().getDeviceToken())
                .setParam("paramSign",StringUtil.createParameterSign(parameters))
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
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        finish();
                    }

                    @Override
                    public void onBtwError(BtwRespError<String> error) {
                        Toast.makeText(LoginActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(LoginActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute();
    }
}
