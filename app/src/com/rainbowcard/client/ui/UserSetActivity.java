package com.rainbowcard.client.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017-1-10.
 */
public class UserSetActivity extends MyBaseActivity{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.change_password_layout)
    RelativeLayout changePassword;
    @InjectView(R.id.change_phone_layout)
    RelativeLayout changePhone;
    @InjectView(R.id.phone_num)
    TextView phoneNum;

    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(UserSetActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(UserSetActivity.this,true);
        phone = getIntent().getStringExtra(Constants.KEY_USER_PHONE);
        initView();
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.user_setting));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserSetActivity.this,ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        changePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserSetActivity.this,ChangePhoneActivity.class);
                startActivityForResult(intent,Constants.REQUEST_CHANGE_PHONE);
            }
        });
        phoneNum.setText(phone);
//        phoneNum.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CHANGE_PHONE && data != null) {
            String phone = data.getStringExtra(Constants.KEY_PHONE);
            phoneNum.setText(phone);
        }
    }
}
