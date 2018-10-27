package com.rainbowcard.client.ui;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
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
 * Created by gc on 2016/4/28.
 */
public class FeedBackActivity extends MyBaseActivity {

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.edit_feedback)
    EditText FeedbackEt;
    @InjectView(R.id.confirm)
    Button confirmBtn;

    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.inject(this);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(FeedBackActivity.this, Constants.USERINFO, Constants.UID));
        UIUtils.setMiuiStatusBarDarkMode(FeedBackActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(FeedBackActivity.this,true);
        initView();
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.feedback));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        FeedbackEt.addTextChangedListener(new MagicTextLengthWatcher(400));
        FeedbackEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (TextUtils.isEmpty(FeedbackEt.getText().toString())) {
                        UIUtils.toast("反馈内容不能为空");
                    } else {
                        commit(FeedbackEt.getText().toString());
                    }
                }
                return false;
            }
        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(FeedbackEt.getText().toString())) {
                    UIUtils.toast("反馈内容不能为空");
                } else {
                    commit(FeedbackEt.getText().toString());
                }
            }
        });
    }

    void commit(String feedback){

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("type",API.API_TYPE);
        parameters.put("version",YHApplication.instance().getVersionName());
//        parameters.put("content", URLEncoder.encode(feedback));
        parameters.put("content", URLEncoder.encode(feedback));

        withBtwVolley().load(API.API_COMMIT_FEEDBACK)
                .method(Request.Method.POST)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("type", API.API_TYPE)
                .setParam("version", YHApplication.instance().getVersionName())
                .setParam("content",feedback)
                .setParam("paramSign", StringUtil.createParameterSign(parameters))
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<String>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onResponse(String resp) {
                        getUIUtils().dismissLoading();
                        showDialog("温馨提示","反馈提交成功");
//                        UIUtils.toast("反馈提交成功");
                    }

                    @Override
                    public void onBtwError(BtwRespError<String> error) {
                        Toast.makeText(FeedBackActivity.this, error.errorMessage,
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(FeedBackActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute();
    }

    class MagicTextLengthWatcher implements TextWatcher {

        private int maxLength; // 儲存最大的字串長度
        private int currentEnd = 0; // 儲存目前字串改變的結束位置，例如：abcdefg變成abcd1234efg，變化的結束位置就在索引8

        public MagicTextLengthWatcher(final int maxLength) {
            setMaxLength(maxLength);
        }

        public final void setMaxLength(final int maxLength) {
            if (maxLength >= 0) {
                this.maxLength = maxLength;
            } else {
                this.maxLength = 0;
            }
        }

        public int getMaxLength() {
            return this.maxLength;
        }

        @Override
        public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

        }

        @Override
        public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
            currentEnd = start + count; // 取得變化的結束位置
        }

        @Override
        public void afterTextChanged(final Editable s) {
            while (calculateLength(s) > maxLength) { // 若變化後的長度超過最大長度
                // 刪除最後變化的字元
                currentEnd--;
                s.delete(currentEnd, currentEnd + 1);
            }
        }

        /**
         * 計算字串的長度
         *
         * @param c
         *            傳入字串
         *
         * @return 傳回字串長度
         */
        protected int calculateLength(final CharSequence c) {
            int len = 0;
            final int l = c.length();
            for (int i = 0; i < l; i++) {
                final char tmp = c.charAt(i);
                if (tmp >= 0x20 && tmp <= 0x7E) {
                    // 字元值 32~126 是 ASCII 半形字元的範圍
                    len++;
                } else {
                    // 非半形字元
                    len += 2;
                }
            }
            return len;
        }

    }
    private void showDialog(String title,String tip) {
        final Dialog dialog = new Dialog(FeedBackActivity.this,R.style.loading_dialog);
        dialog.setContentView(R.layout.ui_alert_dialog);
        Window dialogWindow = dialog.getWindow();
        Display display = getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = -80;
        lp.width = (int)(display.getWidth() * 0.95);
        dialogWindow.setAttributes(lp);

        TextView alertTitle = (TextView) dialog.findViewById(R.id.alert_title);
        TextView alertTip = (TextView) dialog.findViewById(R.id.alert_tip);
        TextView alertOk = (TextView) dialog.findViewById(R.id.alert_ok);
        TextView alertCancle = (TextView) dialog.findViewById(R.id.alert_cancel);
        alertTitle.setText(title);
        alertTip.setText(tip);
        alertOk.setText("确定");
        alertCancle.setText("退出反馈");
        alertCancle.setVisibility(View.GONE);
        dialog.findViewById(R.id.alert_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.findViewById(R.id.alert_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }
}
