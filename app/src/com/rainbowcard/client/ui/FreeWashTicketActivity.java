package com.rainbowcard.client.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.UserBaseModel;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.StringUtil;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;

import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2018-3-5.
 */
public class FreeWashTicketActivity extends MyBaseActivity{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.ticket_icon)
    ImageView ticketIcon;
    @InjectView(R.id.tv_title)
    TextView titleTv;
    @InjectView(R.id.freeze_count)
    TextView freezeCount;
    @InjectView(R.id.sell_count)
    TextView sellCountTv;
    @InjectView(R.id.use_btn)
    Button useBtn;
    @InjectView(R.id.sell_btn)
    Button sellBtn;
    @InjectView(R.id.no_ticket_layout)
    RelativeLayout noTicketLayout;
    @InjectView(R.id.get_btn)
    Button getBtn;
    @InjectView(R.id.invite_btn)
    Button inviteBtn;
    @InjectView(R.id.top_layout)
    RelativeLayout topLayout;
    EditText sellEdit;

    String token;

    int sellCount;   //可出售最大数
    int totalCount;  //总的免洗券张数
    int sum;//出售的张数

    public double lat;
    public double lng;
    public String cityId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_wash_ticket);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(FreeWashTicketActivity.this, true);
        UIUtils.setMeizuStatusBarDarkIcon(FreeWashTicketActivity.this, true);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(FreeWashTicketActivity.this, Constants.USERINFO, Constants.UID));
        cityId = getIntent().getStringExtra(Constants.KEY_CITY_ID);
        lat = getIntent().getDoubleExtra(Constants.KEY_LAT,0.0);
        lng = getIntent().getDoubleExtra(Constants.KEY_LNG,0.0);
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserBase();
    }

    void initView(){

        mHeadControlPanel.setMiddleTitle(getString(R.string.free_wash_ticket));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249, 249, 249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        useBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( totalCount >= 1) {
                    Intent intent = new Intent(FreeWashTicketActivity.this, BranchActivity.class);
                    intent.putExtra(Constants.KEY_CITY_ID, cityId);
                    intent.putExtra(Constants.KEY_LAT, lat);
                    intent.putExtra(Constants.KEY_LNG, lng);
                    intent.putExtra(Constants.KEY_TYPE, 1);
                    intent.putExtra(Constants.KEY_IS_FREE, true);
                    startActivity(intent);
                }else {
                    UIUtils.toast("免费洗车券不足1张");
                }
            }
        });

        noTicketLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sellCount >= 1) {
                    showDialog();
                }else {
                    UIUtils.toast("可出售免费券不足1张");
                }
            }
        });

        topLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FreeWashTicketActivity.this,FreeWashDetailActivity.class);
                startActivity(intent);
            }
        });

        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FreeWashTicketActivity.this,FreeWashTicketEntranceActivity.class);
                startActivity(intent);
            }
        });

        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FreeWashTicketActivity.this,InviteActivity.class);
                startActivity(intent);
            }
        });


    }

    void refreshUI(UserBaseModel.UserBaseEntity userBaseEntity){
        if(userBaseEntity != null) {

            if(userBaseEntity.totalCoupon == 0){
                noTicketLayout.setVisibility(View.VISIBLE);
            }else {
                noTicketLayout.setVisibility(View.GONE);
                titleTv.setText(String.format(getString(R.string.count_text2), StringUtil.subZeroAndDot(String.valueOf(userBaseEntity.totalCoupon))));
                freezeCount.setText(String.format(getString(R.string.discount_count), StringUtil.subZeroAndDot(String.valueOf(userBaseEntity.financeLockCoupon))));
                sellCountTv.setText(String.format(getString(R.string.discount_count) ,StringUtil.subZeroAndDot(String.valueOf(userBaseEntity.financeCoupon))));
                totalCount = (int) userBaseEntity.totalCoupon;
                sellCount = (int) userBaseEntity.financeCoupon;
            }
        }

    }


    private void showDialog() {
        final Dialog dialog = new Dialog(FreeWashTicketActivity.this,R.style.loading_dialog);
        dialog.setContentView(R.layout.ui_sell_dialog);
        Window dialogWindow = dialog.getWindow();
        Display display = getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = -80;
        lp.width = (int)(display.getWidth());
        dialogWindow.setAttributes(lp);

        TextView sellAll = (TextView) dialog.findViewById(R.id.sell_all);
        sellEdit = (EditText) dialog.findViewById(R.id.edit_sell);
        Button confirmSell = (Button) dialog.findViewById(R.id.confirm_sell);

        sellAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellEdit.setText(sellCount + "");
            }
        });

        sellEdit.setHint(String.format(getString(R.string.sell_count),sellCount));
        confirmSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(sellEdit.getText())){
                    UIUtils.toast("请输入出售券的张数");
                }else {
                    if(Integer.valueOf(sellEdit.getText().toString()) > sellCount){
                        UIUtils.toast("出售券数超限");
                        sellEdit.setText(sellCount+"");
                    }else {
                        sellCoupon(sellEdit.getText().toString());
                        dialog.dismiss();
                    }
                }
            }
        });


        dialog.findViewById(R.id.query_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    //出售免洗券
    void sellCoupon(String count){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(FreeWashTicketActivity.this, Constants.USERINFO, Constants.UID));

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("num",count);

        withBtwVolley().load(API.API_SELL_COUPON)
                .method(Request.Method.POST)
                .setHeader("Authorization", token)
                .setHeader("Accept", API.VERSION)
                .setParam("num",count)
                .setParam("paramSign",StringUtil.createParameterSign(parameters))
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<String>() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onResponse(String resp) {
                        getUserBase();
                       UIUtils.toast("出售成功");
                    }

                    @Override
                    public void onBtwError(BtwRespError<String> error) {
                        Toast.makeText(FreeWashTicketActivity.this, error.errorMessage,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(FreeWashTicketActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute();
    }

    //获取用户免洗券、可提金额等基本信息
    void getUserBase() {
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(FreeWashTicketActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(API.API_GET_USER_BASE)
                .method(Request.Method.GET)
                .setHeader("Authorization", token)
                .setHeader("Accept", API.VERSION)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<UserBaseModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(UserBaseModel resp) {
                        refreshUI(resp.data);
                    }

                    @Override
                    public void onBtwError(BtwRespError<UserBaseModel> error) {
                        Toast.makeText(FreeWashTicketActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(FreeWashTicketActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
//                        getMyCard();
                    }
                }).excute(UserBaseModel.class);
    }
}
