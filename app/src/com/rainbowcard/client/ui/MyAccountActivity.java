package com.rainbowcard.client.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.rainbowcard.client.model.CardEntity;
import com.rainbowcard.client.model.CardModel;
import com.rainbowcard.client.model.UserModel;
import com.rainbowcard.client.ui.adapter.CardListAdapter;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.StringUtil;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.MyListView;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017-1-9.
 */
public class MyAccountActivity extends MyBaseActivity{

    @InjectView(R.id.nav_back)
    RelativeLayout navBack;
    @InjectView(R.id.right_layout)
    LinearLayout rightLayout;
    @InjectView(R.id.account)
    TextView accountTv;
    @InjectView(R.id.balance)
    TextView balanceTv;
    @InjectView(R.id.account_set)
    TextView accountSet;
    @InjectView(R.id.recharge_btn)
    Button rechargeBtn;
    @InjectView(R.id.card_listview)
    MyListView cardLv;
    @InjectView(R.id.default_text)
    TextView defaultTv;
    @InjectView(R.id.no_card)
    TextView noCard;

    CardListAdapter adapter;
    List<CardEntity> cardEntities = new ArrayList<CardEntity>();

    String token;

    String phone;
    String defaultAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.inject(this);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(MyAccountActivity.this, Constants.USERINFO, Constants.UID));
        phone = getIntent().getStringExtra(Constants.KEY_USER_PHONE);
        defaultAccount = MyConfig.getSharePreStr(MyAccountActivity.this, Constants.USERINFO, Constants.DEFAULT_NO);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(MyAccountActivity.this, Constants.USERINFO, Constants.UID));
        defaultAccount = MyConfig.getSharePreStr(MyAccountActivity.this, Constants.USERINFO, Constants.DEFAULT_NO);
        getUserInfo();
        getMyCard();

    }

    void initView(){
        adapter = new CardListAdapter(this);
        cardLv.setAdapter(adapter);
        navBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this,RechargeRecordActivity.class);
                startActivity(intent);
            }
        });

        noCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this,BindCardActivity.class);
                startActivity(intent);
            }
        });

        accountSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDialog();
            }
        });
        rechargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this,RechargeRainbowCardActivity.class);
                startActivity(intent);
            }
        });

    }

    void loadDialog(){
        Dialog dialog = UIUtils.alertButtonListBottom(MyAccountActivity.this,"",true);
        Window dialogWindow = dialog.getWindow();
        Display display = getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int)(display.getWidth());
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.BOTTOM);
        UIUtils.addButtonToButtonListBottom(dialog, "账户余额", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setDefault(phone,10000);
            }
        });

        for (int i = 0;i<cardEntities.size();i++){
            final int a = i;
            UIUtils.addButtonToButtonListBottom(dialog, "彩虹卡 "+cardEntities.get(i).num, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setDefault(cardEntities.get(a).num,a);
                }
            });
        }

    }

    //获取用户信息
    void getUserInfo(){
        withBtwVolley().load(API.API_GET_USER_INFO)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<UserModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(UserModel resp) {
                        balanceTv.setText(String.format(getString(R.string.balance_text),resp.data.money));
                    }

                    @Override
                    public void onBtwError(BtwRespError<UserModel> error) {
                        Toast.makeText(MyAccountActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(MyAccountActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
//                        getUserInfo();
                    }
                }).excute(UserModel.class);
    }

    //获取我的卡片
    void getMyCard(){
        withBtwVolley().load(API.API_GET_MY_CARD)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<CardModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(CardModel resp) {
                        if(resp.data != null && !resp.data.isEmpty()) {
                            cardEntities = resp.data;
                            adapter.setCardEntitys(resp.data);
                            if(!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(defaultAccount)) {
                                if (phone.equals(defaultAccount)){
                                    defaultTv.setVisibility(View.VISIBLE);
                                    adapter.setCurrentItem(-1);
                                }else {
                                    defaultTv.setVisibility(View.GONE);
                                    for (int i =0;i<cardEntities.size();i++){
                                        if(defaultAccount.equals(cardEntities.get(i).num)){
                                            adapter.setCurrentItem(i);
                                        }
                                    }
                                }
                            }
                            noCard.setVisibility(View.GONE);
                        }else {
                            noCard.setVisibility(View.VISIBLE);
                            MyConfig.putSharePre(MyAccountActivity.this, Constants.USERINFO, Constants.DEFAULT_NO, "");
                        }
                    }

                    @Override
                    public void onBtwError(BtwRespError<CardModel> error) {
                        Toast.makeText(MyAccountActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(MyAccountActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
//                        getMyCard();
                    }
                }).excute(CardModel.class);
    }

    //设置默认账户
    void setDefault(final String account,final int NumType){

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("account", URLEncoder.encode(account));

        withBtwVolley().load(API.API_SET_DEFAULT_ACCOUNT)
                .method(Request.Method.POST)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("account",account)
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
                        if(NumType == 10000){
                            defaultTv.setVisibility(View.VISIBLE);
                            adapter.setCurrentItem(-1);
                        }else {
                            adapter.setCurrentItem(NumType);
                            defaultTv.setVisibility(View.GONE);
                        }
                        MyConfig.putSharePre(MyAccountActivity.this, Constants.USERINFO, Constants.DEFAULT_NO, account);

                    }

                    @Override
                    public void onBtwError(BtwRespError<String> error) {
                        Toast.makeText(MyAccountActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(MyAccountActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
//                        setDefault(account);
                    }
                }).excute();
    }
}
