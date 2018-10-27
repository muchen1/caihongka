package com.rainbowcard.client.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2018-3-5.
 */
public class FreeWashTicketEntranceActivity extends MyBaseActivity{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.right_layout)
    LinearLayout rightLayout;
    @InjectView(R.id.right_title)
    TextView rightTv;

    @InjectView(R.id.trial_btn)
    Button trialBtn;

    String token;

    public double lat;
    public double lng;
    public String cityId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_wash_entrance);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(FreeWashTicketEntranceActivity.this, true);
        UIUtils.setMeizuStatusBarDarkIcon(FreeWashTicketEntranceActivity.this, true);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(FreeWashTicketEntranceActivity.this, Constants.USERINFO, Constants.UID));
        cityId = getIntent().getStringExtra(Constants.KEY_CITY_ID);
        lat = getIntent().getDoubleExtra(Constants.KEY_LAT,0.0);
        lng = getIntent().getDoubleExtra(Constants.KEY_LNG,0.0);
        initView();
    }

    void initView(){
        mHeadControlPanel.setMiddleTitle(getString(R.string.free_wash_ticket_entrance));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249, 249, 249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FreeWashTicketEntranceActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        rightLayout.setVisibility(View.VISIBLE);
        rightTv.setVisibility(View.VISIBLE);
        rightTv.setText(getString(R.string.purchase_record));
        rightTv.setTextColor(getResources().getColor(R.color.gray_text_context_3));
        rightTv.setTextSize(12);

        rightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FreeWashTicketEntranceActivity.this,FinanceRecordActivity.class);
                startActivity(intent);
            }
        });

        trialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FreeWashTicketEntranceActivity.this, FreeWashTrialActivity.class);
                startActivity(intent);
            }
        });



    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){

            Intent intent = new Intent(FreeWashTicketEntranceActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
