package com.rainbowcard.client.ui;

import android.annotation.SuppressLint;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.utils.ASEUtil;
import com.rainbowcard.client.utils.StringUtil;
import com.rainbowcard.client.utils.TimeUtil;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.utils.Validation;
import com.rainbowcard.client.widget.HeadControlPanel;

import java.net.URLEncoder;
import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017-1-10.
 */
public class FindPasswordActivity extends MyBaseActivity{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.edit_phone)
    EditText phoneEdit;
    @InjectView(R.id.edit_verification)
    EditText verificationEdit;
    @InjectView(R.id.edit_affirm)
    EditText affirmEdit;
    @InjectView(R.id.verification_btn)
    Button verificationBtn;
    @InjectView(R.id.accomplish)
    Button accomplishBtn;
    @InjectView(R.id.voice_verify)
    TextView voiceVerify;

    String phone;
    String verification;
    String affirmPw;

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
        setContentView(R.layout.activity_find_password);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(FindPasswordActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(FindPasswordActivity.this,true);
        initView();
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.find_password));
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
                    getVerification(1);
                }
            }
        });

        accomplishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNext()){
                    submit();
                }
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
                    getVerification(2);
                }
            }
        });
    }

    private boolean toSendVerify() {
        phone = phoneEdit.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            UIUtils.toast("手机号不能为空");
            return false;
        }
        if (!Validation.isMobile(phone)) {
            UIUtils.toast("手机号格式不正确");
            return false;
        }
        return true;
    }

    private boolean isNext(){
        phone = phoneEdit.getText().toString();
        verification = verificationEdit.getText().toString();
        affirmPw = affirmEdit.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            UIUtils.toast("手机号码不能为空");
            return false;
        }
        if (!Validation.isMobile(phone)) {
            UIUtils.toast("手机号格式不正确");
            return false;
        }
        if (TextUtils.isEmpty(verification)) {
            UIUtils.toast("验证码不能为空");
            return false;
        }
        if (verification.length() != 4) {
            UIUtils.toast("验证码长度应为4位");
            return false;
        }
        if (TextUtils.isEmpty(affirmPw)) {
            UIUtils.toast("确认密码不能为空");
            return false;
        }
        if (affirmPw.length() < 6) {
            UIUtils.toast("确认密码长度不能少于6位");
            return false;
        }
        return true;
    }

    //获取验证码
    void getVerification(final int verifyType){

        long timestamp = System.currentTimeMillis() / 1000;
        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("phone",phone);
        parameters.put("type",verifyType);
        parameters.put("timestamp",timestamp);
        String sign = StringUtil.createSign(parameters);
        parameters.put("sign",sign);

        withBtwVolley().load(API.API_GET_FIND_PASSWORD_CODE)
                .method(Request.Method.POST)
                .setHeader("Accept", API.VERSION)
                .setParam("phone", phone)
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
                        Toast.makeText(FindPasswordActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(FindPasswordActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute();
    }

    //提交修改密码
    void submit(){
        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("phone",phone);
        parameters.put("sms_code",verification);
        parameters.put("password",URLEncoder.encode(ASEUtil.Encrypt(affirmPw,API.ENCRYPT_KEY)));
        parameters.put("encrypt",1);
        final String aa = StringUtil.createSign(parameters);
        String sign = StringUtil.createSign(parameters);
        parameters.put("sign",sign);

        withBtwVolley().load(API.API_FIND_PASSWORD)
                .method(Request.Method.POST)
                .setHeader("Accept", API.VERSION)
                .setParam("phone", phone)
                .setParam("sms_code",verification)
                .setParam("password", ASEUtil.Encrypt(affirmPw,API.ENCRYPT_KEY))
                .setParam("encrypt",1)
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
                        UIUtils.toast("密码修改成功");
                        finish();
                    }

                    @Override
                    public void onBtwError(BtwRespError<String> error) {
                        Toast.makeText(FindPasswordActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(FindPasswordActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute();
    }
}
