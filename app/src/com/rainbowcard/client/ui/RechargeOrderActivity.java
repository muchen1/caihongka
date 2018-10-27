package com.rainbowcard.client.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.common.PayActivity;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.OrderModel;
import com.rainbowcard.client.model.RecordModel;
import com.rainbowcard.client.ui.adapter.MyTypeStatusAdapter;
import com.rainbowcard.client.ui.adapter.RechargeOrderListAdapter;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.widget.HorizontalListView;
import com.rainbowcard.client.widget.LoadingFrameLayout;
import com.rainbowcard.client.widget.ScrollToFooterLoadMoreListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017-3-23.
 */
public class RechargeOrderActivity extends MyBaseActivity implements RechargeOrderListAdapter.SetOrderListener{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.lv_type_status)
    HorizontalListView mLvTypeStatus;
    @InjectView(R.id.v_frame)
    LoadingFrameLayout mFlLoading;
    @InjectView(R.id.v_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.lv_order)
    ScrollToFooterLoadMoreListView mOrderLv;

    MyTypeStatusAdapter mMyTypeStatusAdapter;
    RechargeOrderListAdapter adapter;

    String token;

    public int status = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recharge_record);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(RechargeOrderActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(RechargeOrderActivity.this,true);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(RechargeOrderActivity.this, Constants.USERINFO, Constants.UID));
        status = getIntent().getIntExtra(Constants.KEY_RECHARGE_OR_CARD,0);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getOrder();
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        if(status == 1) {
            mHeadControlPanel.setMiddleTitle("领卡订单");
        }else {
            mHeadControlPanel.setMiddleTitle("充值记录");
        }
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mMyTypeStatusAdapter = new MyTypeStatusAdapter(this);
        mMyTypeStatusAdapter.setMPosition(0);
        List<String> list = new ArrayList<String>();
        list.add("充值订单");
        list.add("领卡订单");
        mMyTypeStatusAdapter.setmGetTypeList(list);
        mLvTypeStatus.setAdapter(mMyTypeStatusAdapter);
        mLvTypeStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMyTypeStatusAdapter.setMPosition(position);
                adapter.setMPosition(position);
                if(position == 0){
                    status = 2;
                }else {
                    status = 1;
                }
                getOrder();
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.app_blue, R.color.app_blue, R.color.app_blue, R.color.app_blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOrder();
            }
        });
        mFlLoading.setRetryButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOrder();
            }
        });
        adapter = new RechargeOrderListAdapter(this,this);
        mOrderLv.setAdapter(adapter);
        if(status == 1) {
            adapter.setMPosition(1);
        }
        mOrderLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getOrderDetail(adapter.getRecordEntitys().get(position).tradeNo,false);
            }
        });
        loadMore(false);
    }

    private void loadMore(boolean toLoad){
        if(toLoad){
            mOrderLv.setOnScrollToRefreshListener(new ScrollToFooterLoadMoreListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                    getOrder();
                }
            });
        }else {
            mOrderLv.setOnScrollToRefreshListener(new ScrollToFooterLoadMoreListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                    mOrderLv.refreshComplete();
                }
            });
        }
    }

    //获取充值订单列表
    void getOrder(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(RechargeOrderActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(API.API_GET_USER_RECHARGE)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION2D1)
                .setParam("status","")
                .setParam("type",status)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<RecordModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(RecordModel resp) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if(resp.data != null && !resp.data.isEmpty()) {
                            mFlLoading.showLoadingView(false);
                            adapter.setRecordEntitys(resp.data);
                        }else {
                            mFlLoading.showError(getString(R.string.no_data),false);
                        }


                    }

                    @Override
                    public void onBtwError(BtwRespError<RecordModel> error) {
                        Toast.makeText(RechargeOrderActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(RechargeOrderActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
//                        getOrder();
                    }
                }).excute(RecordModel.class);
    }
    //获取订单详情
    void getOrderDetail(String tradeNo,final boolean isPay){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(RechargeOrderActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(String.format(getString(R.string.url),API.API_GET_USER_RECHARGE_DETAIL,tradeNo))
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", "application/vnd.caihongka.v2.1.0+json")
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<OrderModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(OrderModel resp) {
                        Intent intent;
                        if(isPay){
                            intent = new Intent(RechargeOrderActivity.this, PayActivity.class);
                            if(status == 2) {
                                if (resp.data.cardTypeText.equals("账户余额")) {
                                    intent.putExtra(Constants.KEY_RAINBOW_TYPE, Constants.RAINBOW_RECHARGE_ACCOUNT);
                                } else {
                                    intent.putExtra(Constants.KEY_RAINBOW_TYPE, Constants.RAINBOW_RECHARGE);
                                }
                            }else {
                                intent.putExtra(Constants.KEY_RAINBOW_TYPE,Constants.RAINBOW_BUY);
                            }
                            intent.putExtra(Constants.KEY_ORDER_MODEL,resp.data);
                        }else {
                            intent = new Intent(RechargeOrderActivity.this,RechargeOrderDetailActivity.class);
                            intent.putExtra(Constants.KEY_ORDER_MODEL,resp.data);

                        }
                        startActivity(intent);
                    }

                    @Override
                    public void onBtwError(BtwRespError<OrderModel> error) {
                        Toast.makeText(RechargeOrderActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(RechargeOrderActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
//                        getOrder();
                    }
                }).excute(OrderModel.class);
    }

    private void showDialog(String title, String tip,final String tradeNo) {
        final Dialog dialog = new Dialog(RechargeOrderActivity.this,R.style.loading_dialog);
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
        alertOk.setText("继续");
        alertCancle.setText("关闭");
        dialog.findViewById(R.id.alert_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrder(tradeNo);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.alert_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //取消订单
    void cancelOrder(String tradeNo){
        withBtwVolley().load(String.format(getString(R.string.url),API.API_DELETE_ORDER,tradeNo))
                .method(Request.Method.POST)
                .setHeader("Authorization",token)
                .setHeader("Accept", "application/vnd.caihongka.v2.1.0+json")
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
                        UIUtils.toast("订单已取消");
                        getOrder();
                    }

                    @Override
                    public void onBtwError(BtwRespError<String> error) {
                        Toast.makeText(RechargeOrderActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(RechargeOrderActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute();
    }

    @Override
    public void pay(int position) {
        getOrderDetail(adapter.getRecordEntitys().get(position).tradeNo,true);
    }

    @Override
    public void cancel(int position) {
        showDialog("温馨提示","订单取消将无法恢复",adapter.getRecordEntitys().get(position).tradeNo);
    }
}
