package com.rainbowcard.client.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.loopeer.cardstack.CardStackView;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.CardEntity;
import com.rainbowcard.client.model.CardModel;
import com.rainbowcard.client.ui.adapter.MyCardListAdapter;
import com.rainbowcard.client.ui.adapter.TestStackAdapter;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.widget.LoadingFrameLayout;
import com.rainbowcard.client.widget.MyListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017-1-10.
 */
public class CardActivity extends MyBaseActivity implements CardStackView.ItemExpendListener{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.right_layout)
    LinearLayout rightLayout;
    @InjectView(R.id.right_title)
    TextView rightTv;
    @InjectView(R.id.v_frame)
    LoadingFrameLayout mFlLoading;
//    @InjectView(R.id.v_refresh)
//    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.stackview_main)
    CardStackView mStackView;
//    @InjectView(R.id.card_listview)
//    MyListView cardLv;

    String token;
    public static CardActivity instance = null;

    MyCardListAdapter adapter;
    private TestStackAdapter mTestStackAdapter;

    public static Integer[] TEST_DATAS = new Integer[]{
            R.color.color_1,
            R.color.color_2,
            R.color.color_3
    };

    public static CardActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        instance = this;
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(CardActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(CardActivity.this,true);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(CardActivity.this, Constants.USERINFO, Constants.UID));
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMyCard();
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.my_card));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rightLayout.setVisibility(View.VISIBLE);
        rightTv.setVisibility(View.VISIBLE);
        rightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardActivity.this,BindCardActivity.class);
                startActivity(intent);
                finish();
            }
        });

//        mSwipeRefreshLayout.setColorSchemeResources(R.color.app_blue, R.color.app_blue, R.color.app_blue, R.color.app_blue);
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                getMyCard();
//            }
//        });
        mFlLoading.setRetryButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyCard();
            }
        });
        adapter = new MyCardListAdapter(this);
//        cardLv.setAdapter(adapter);
        mStackView.setItemExpendListener(this);
        mTestStackAdapter = new TestStackAdapter(this);
        mStackView.setAdapter(mTestStackAdapter);

//        new Handler().postDelayed(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        mTestStackAdapter.updateData(Arrays.asList(TEST_DATAS));
//                    }
//                }
//                , 200
//        );
    }


    //获取我的卡片
    public void getMyCard(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(CardActivity.this, Constants.USERINFO, Constants.UID));
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
//                        mSwipeRefreshLayout.setRefreshing(false);
                        if(resp.data != null && !resp.data.isEmpty()) {
                            adapter.setCardEntitys(resp.data);
                            mTestStackAdapter.updateData(resp.data);
                            mTestStackAdapter.updateData(Arrays.asList(new Integer[resp.data.size()]));
                        }else {
                            mFlLoading.showError(getString(R.string.no_data),false);
                            MyConfig.putSharePre(CardActivity.this, Constants.USERINFO, Constants.DEFAULT_NO, "");
                        }

                    }

                    @Override
                    public void onBtwError(BtwRespError<CardModel> error) {
                        Toast.makeText(CardActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(CardActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken(1);
//                        getMyCard();
                    }
                }).excute(CardModel.class);
    }

    @Override
    public void onItemExpend(boolean expend) {

    }
}
