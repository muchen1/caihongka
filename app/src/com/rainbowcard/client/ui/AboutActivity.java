package com.rainbowcard.client.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.rainbowcard.client.R;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.common.utils.DLog;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2016/4/28.
 */
public class AboutActivity extends MyBaseActivity {

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.help_layout)
    RelativeLayout helpLayout;
    @InjectView(R.id.feedback_layout)
    RelativeLayout feedbackLayout;
    @InjectView(R.id.code)
    TextView codeTv;
    @InjectView(R.id.agreement)
    TextView agreement;

    private PackageInfo mVersionInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(AboutActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(AboutActivity.this,true);
        initView();
    }
    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.about));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getVersioInfo();
        agreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        helpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        feedbackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutActivity.this,FeedBackActivity.class);
                startActivity(intent);
            }
        });

    }
    private void getVersioInfo() {
        try {
            mVersionInfo = this.getPackageManager().getPackageInfo(this.getPackageName(),0);
            String versionName = mVersionInfo.versionName;
            codeTv.setText(String.format(getString(R.string.versions),versionName));
        }catch (PackageManager.NameNotFoundException e){
            DLog.e(Log.getStackTraceString(e));
        }

    }
}
