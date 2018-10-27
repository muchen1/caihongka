package com.rainbowcard.client.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.BalanceModel;
import com.rainbowcard.client.utils.PrefsManager;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2016-10-25.
 */
public class QueryRainbowCardActivity extends MyBaseActivity {

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.et_card)
    EditText cardEdit;
    @InjectView(R.id.query_btn)
    Button queryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_rainbow);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(QueryRainbowCardActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(QueryRainbowCardActivity.this,true);
        initView();
    }

    void initView(){
        String lastCard = PrefsManager.getInstance(this).getLastCard();
        if(!TextUtils.isEmpty(lastCard)){
            cardEdit.setText(lastCard);
        }
        mHeadControlPanel.setMiddleTitle(getString(R.string.query_rainbow));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        queryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(cardEdit.getText().toString())){
                    UIUtils.toast("彩虹卡号为空");
                }else {
                    query(cardEdit.getText().toString());
                    queryBtn.setVisibility(View.INVISIBLE);
                    queryBtn.setBackgroundResource(R.drawable.button_darkgray);
                    queryBtn.setEnabled(false);
                }
            }
        });
    }

    void refreUI(BalanceModel.BalanceEntity entity){
        cardEdit.setFocusable(false);
        cardEdit.setFocusableInTouchMode(false);
        View view = ((ViewStub)findViewById(R.id.viewstub)).inflate();
        TextView balanceTv = (TextView) view.findViewById(R.id.tv_balance);
        balanceTv.setText(String.format(getString(R.string.balance_text),entity.balance));
        TextView numberTv = (TextView) view.findViewById(R.id.tv_number);
        numberTv.setText(String.format(getString(R.string.balance_count),entity.count));
        TextView payBtn = (TextView) view.findViewById(R.id.tv_pay);
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QueryRainbowCardActivity.this,RechargeRainbowCardActivity.class);
                intent.putExtra(Constants.KEY_CARD,cardEdit.getText().toString());
                startActivity(intent);
            }
        });
    }

    //查询彩虹卡余额
    void query(final String num){
        withBtwVolley().load(API.API_RAINBOW_QUERY)
                .setHeader("Accept", API.VERSION)
                .method(Request.Method.GET)
                .setParam("card_number",num)
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<BalanceModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(BalanceModel resp) {
                        PrefsManager.getInstance(QueryRainbowCardActivity.this)
                                .setLastCard(num);
                        refreUI(resp.data);
                    }

                    @Override
                    public void onBtwError(BtwRespError<BalanceModel> error) {
                        UIUtils.toast(error.errorMessage);
                        queryBtn.setVisibility(View.VISIBLE);
                        queryBtn.setBackgroundResource(R.drawable.query_select_item);
                        queryBtn.setEnabled(true);
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        UIUtils.toast(getString(R.string.network_error));
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(BalanceModel.class);
    }
}
