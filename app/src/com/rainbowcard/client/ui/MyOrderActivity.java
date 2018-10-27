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
import android.widget.LinearLayout;
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
import com.rainbowcard.client.model.CardModel;
import com.rainbowcard.client.model.OrderModel;
import com.rainbowcard.client.model.ShopOrderEntity;
import com.rainbowcard.client.model.ShopOrderModel;
import com.rainbowcard.client.ui.adapter.MyTypeStatusAdapter;
import com.rainbowcard.client.ui.adapter.ShopOrderListAdapter;
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
 * Created by gc on 2017-1-10.
 */
public class MyOrderActivity extends MyBaseActivity implements ShopOrderListAdapter.SetOrderListener{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.right_layout)
    LinearLayout rightLayout;
    @InjectView(R.id.right_title)
    TextView rightTv;
    @InjectView(R.id.lv_type_status)
    HorizontalListView mLvTypeStatus;
    @InjectView(R.id.v_frame)
    LoadingFrameLayout mFlLoading;
    @InjectView(R.id.v_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.lv_order)
    ScrollToFooterLoadMoreListView mOrderLv;

    MyTypeStatusAdapter mMyTypeStatusAdapter;
    ShopOrderListAdapter adapter;

    String token;

    public int status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(MyOrderActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(MyOrderActivity.this,true);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(MyOrderActivity.this, Constants.USERINFO, Constants.UID));
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getOrder();
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.my_order));
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
        rightTv.setText(getString(R.string.recharge_order));
        rightTv.setTextSize(12);
        rightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyOrderActivity.this,RechargeOrderActivity.class);
                intent.putExtra(Constants.KEY_RECHARGE_OR_CARD,1);
                startActivity(intent);
            }
        });


        mMyTypeStatusAdapter = new MyTypeStatusAdapter(this);
        mMyTypeStatusAdapter.setMPosition(0);
        List<String> list = new ArrayList<String>();
        list.add("全部");
        list.add("待支付");
        list.add("待使用");
        list.add("待评价");
        mMyTypeStatusAdapter.setmGetTypeList(list);
        mLvTypeStatus.setAdapter(mMyTypeStatusAdapter);
        mLvTypeStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMyTypeStatusAdapter.setMPosition(position);
                switch (position){
                    case 0:
                        status = 0;
                        break;
                    case 1:
                        status = 8;
                        break;
                    case 2:
                        status = 1;
                        break;
                    case 3:
                        status = 2;
                        break;
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
        adapter = new ShopOrderListAdapter(MyOrderActivity.this,MyOrderActivity.this);
        mOrderLv.setAdapter(adapter);
        mOrderLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(MyOrderActivity.this,OrderDetailActivity.class);
//                intent.putExtra(Constants.KEY_SHOP_ORDER,adapter.getShopOrderEntitys().get(position));
//                startActivity(intent);
                getOrderDetail(adapter.getShopOrderEntitys().get(position).tradeNo,false);
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

    //获取订单列表
    void getOrder(){
        withBtwVolley().load(API.API_GET_MY_PAYMENT)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION2D1)
                .setParam("status",status)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<ShopOrderModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(ShopOrderModel resp) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if(resp.data != null && !resp.data.isEmpty()) {
                            mFlLoading.showLoadingView(false);
                            adapter.setShopOrderEntitys(resp.data);
                        }else {
                            mFlLoading.showError(getString(R.string.no_data),false);
                        }


                    }

                    @Override
                    public void onBtwError(BtwRespError<ShopOrderModel> error) {
                        Toast.makeText(MyOrderActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(MyOrderActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
//                        getOrder();
                    }
                }).excute(ShopOrderModel.class);
    }

    //获取订单详情
    void getOrderDetail(String tradeNo,final boolean isPay){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(MyOrderActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(API.API_GET_PAYMENT_DETAIL)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("trade_no",tradeNo)
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
                            intent = new Intent(MyOrderActivity.this, ShopOrderActivity.class);
                            intent.putExtra(Constants.KEY_ORDER_MODEL,resp.data);
                        }else {
                            intent = new Intent(MyOrderActivity.this,OrderDetailActivity.class);
                            intent.putExtra(Constants.KEY_ORDER_MODEL,resp.data);

                        }
                        startActivity(intent);
                    }

                    @Override
                    public void onBtwError(BtwRespError<OrderModel> error) {
                        Toast.makeText(MyOrderActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(MyOrderActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(OrderModel.class);
    }

    @Override
    public void pay(int position) {
        getOrderDetail(adapter.getShopOrderEntitys().get(position).tradeNo,true);
    }

    @Override
    public void cancel(int position) {
        showDialog("温馨提示","订单取消将无法恢复",adapter.getShopOrderEntitys().get(position).tradeNo);
    }

    @Override
    public void comment(int position) {
        Intent intent = new Intent(MyOrderActivity.this, CommentActivity.class);
        intent.putExtra(Constants.KEY_SHOP_IMG,adapter.getShopOrderEntitys().get(position).shopImg);
        intent.putExtra(Constants.KEY_TRADE_NO,adapter.getShopOrderEntitys().get(position).tradeNo);
        startActivity(intent);
    }

    private void showDialog(String title, String tip,final String tradeNo) {
        final Dialog dialog = new Dialog(MyOrderActivity.this,R.style.loading_dialog);
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
        withBtwVolley().load(String.format(getString(R.string.url), API.API_DELETE_PAYMENT,tradeNo))
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
                        Toast.makeText(MyOrderActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(MyOrderActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute();
    }
}
