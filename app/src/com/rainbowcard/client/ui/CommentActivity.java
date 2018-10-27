package com.rainbowcard.client.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.rainbowcard.client.utils.DensityUtil;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.StringUtil;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.FlowLayout;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.squareup.picasso.Picasso;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017-1-16.
 */
public class CommentActivity extends MyBaseActivity{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.shop_icon)
    ImageView shopIcon;
    @InjectView(R.id.rc_rate)
    RatingBar ratingBar;
    @InjectView(R.id.service_grade)
    TextView serviceGrade;
    @InjectView(R.id.flow_layout)
    FlowLayout flowLayout;
    @InjectView(R.id.edit_comment)
    EditText commentEt;
    @InjectView(R.id.submit)
    Button submitBtn;

    String token;


    float star = 5.0f;
    String shopImg;
    String tradeNo;

    List<String> tagList = new ArrayList<String>();

    String[] tags = {"服务态度好","清洗的很干净","师傅很专业","值得信赖","工作流程规范","尽职尽责","效率高","技术娴熟","工作认真"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(CommentActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(CommentActivity.this,true);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(CommentActivity.this, Constants.USERINFO, Constants.UID));
        shopImg = getIntent().getStringExtra(Constants.KEY_SHOP_IMG);
        tradeNo = getIntent().getStringExtra(Constants.KEY_TRADE_NO);
        initView();
    }

    void initView(){
        mHeadControlPanel.setMiddleTitle(getString(R.string.order_comment));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        flowLayout.removeAllViews();
        LayoutInflater mInflater = LayoutInflater.from(this);
        for (int i = 0;i<tags.length;i++){
            final TextView tv = (TextView) mInflater.inflate(R.layout.item_tag,
                    flowLayout, false);
            tv.setText(tags[i]);
            final boolean[] isClick = {false,false,false,false,false,false,false,false,false};
            final int finalI = i;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    String edit;
                    isClick[finalI] = !isClick[finalI];
                    if(isClick[finalI]){
                        tv.setBackgroundResource(R.drawable.tagstyle_select_orange);
                        tv.setTextColor(getResources().getColor(R.color.money_color));
//                        edit = commentEt.getText().toString() + tv.getText().toString() + " ";
                        tagList.add(tv.getText().toString());
                    }else {
                        tv.setBackgroundResource(R.drawable.tag_select_item);
                        tv.setTextColor(getResources().getColor(R.color.app_black));
//                        edit = commentEt.getText().toString().replaceAll( tv.getText().toString(),"");
                        for(int i = 0;i < tagList.size();i++){
                            if(tagList.get(i).contains(tv.getText().toString())){
                                tagList.remove(i);
                            }
                        }
                    }
//                    commentEt.setText(edit);
                    commentEt.setSelection(commentEt.getText().length());
                }
            });

            flowLayout.addView(tv);
        }


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                star = ratingBar.getRating();
                if(star<1.5){
                    if(star < 0.5){
                        UIUtils.toast("洗车不容易，给个0.5分吧");
                        ratingBar.setRating(0.5f);
                    }
                    serviceGrade.setText("服务很差");
                }else if(star >=1.5 && star<2.5){
                    serviceGrade.setText("服务稍差");
                }else if(star >= 2.5 && star < 3.5){
                    serviceGrade.setText("服务一般");
                }else if(star >=3.5 && star <4.5){
                    serviceGrade.setText("服务良好");
                }else {
                    serviceGrade.setText("服务极好");
                }
            }
        });

//        commentEt.addTextChangedListener(new MagicTextLengthWatcher(400));
//        commentEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    if (TextUtils.isEmpty(commentEt.getText().toString())) {
//                        UIUtils.toast("反馈内容不能为空");
//                    } else {
//                        commit(commentEt.getText().toString());
//                    }
//                }
//                return false;
//            }
//        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(commentEt.getText().toString()) && tagList.isEmpty()) {
                    UIUtils.toast("反馈内容不能为空");
                } else {
                    commit(tagList.toString().replace("[","").replace("]",",") + commentEt.getText().toString());
                }
            }
        });

        if(TextUtils.isEmpty(shopImg)){
            shopIcon.setImageResource(R.drawable.order_default);
        }else {
            Picasso.with(CommentActivity.this)
                    .load(String.format(getString(R.string.img_url),shopImg))
                    .resize(DensityUtil.dip2px(CommentActivity.this,70),DensityUtil.dip2px(CommentActivity.this,70))
                    .centerCrop()
                    .placeholder(R.drawable.order_default)
                    .error(R.drawable.order_default).into(shopIcon);
        }
    }

    void commit(String feedback){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(CommentActivity.this, Constants.USERINFO, Constants.UID));

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("content", URLEncoder.encode(feedback));
        parameters.put("star",star);
        parameters.put("trade_no",tradeNo);

        withBtwVolley().load(API.API_SUBMIT_USER_COMMENT)
                .method(Request.Method.POST)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("content",feedback)
                .setParam("star",star)
                .setParam("trade_no",tradeNo)
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
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(String resp) {
//                        showDialog("温馨提示","评论提交成功");
                        UIUtils.toast("评论提交成功");
                        Intent intent = new Intent();
                        intent.putExtra(Constants.KEY_IS_COMMENT, 1);
                        setResult(Constants.REQUEST_ORDER, intent);
                        finish();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                        commit(commentEt.getText().toString());
                    }

                    @Override
                    public void onBtwError(BtwRespError<String> error) {
                        Toast.makeText(CommentActivity.this, error.errorMessage,
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(CommentActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }
                }).excute();
    }

    private void showDialog(String title,String tip) {
        final Dialog dialog = new Dialog(CommentActivity.this,R.style.loading_dialog);
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
        alertCancle.setText("退出评论");
        dialog.findViewById(R.id.alert_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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
}
