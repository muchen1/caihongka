package com.rainbowcard.client.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import com.rainbowcard.client.model.DiscountEntity;
import com.rainbowcard.client.model.DiscountModel;
import com.rainbowcard.client.ui.adapter.DiscountListAdapter;
import com.rainbowcard.client.ui.adapter.MyTypeStatusAdapter;
import com.rainbowcard.client.utils.KeyboardUtil;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.StringUtil;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.widget.HorizontalListView;
import com.rainbowcard.client.widget.LoadingFrameLayout;
import com.rainbowcard.client.widget.ScrollToFooterLoadMoreListView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017-1-10.
 */
public class MyDiscountActivity extends MyBaseActivity{

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
    @InjectView(R.id.lv_discount)
    ScrollToFooterLoadMoreListView mDiscountLv;
    @InjectView(R.id.discount_edit)
    EditText discountEt;
    @InjectView(R.id.add_discount)
    Button addDiscount;
    @InjectView(R.id.add_layout)
    RelativeLayout addLayout;

    @InjectView(R.id.relakey)
    RelativeLayout relakey;
    @InjectView(R.id.hideshow)
    TextView hideshow;

    MyTypeStatusAdapter mMyTypeStatusAdapter;
    DiscountListAdapter adapter;

    String token;

    public int status = 3;
    private String couponUsed;
    private String couponUnUsed;
    private String couponExpire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_discount);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(MyDiscountActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(MyDiscountActivity.this,true);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(MyDiscountActivity.this, Constants.USERINFO, Constants.UID));
        couponUsed = getIntent().getStringExtra(Constants.KEY_COUPON_USED);
        couponUnUsed = getIntent().getStringExtra(Constants.KEY_COUPON_UN_USED);
        couponExpire = getIntent().getStringExtra(Constants.KEY_COUPON_EXPIRE);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDiscount();
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.my_discount));
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
        list.add("未使用");
        list.add("已使用");
        list.add("已失效");
//        list.add(String.format(getString(R.string.coupon_un_used),couponUnUsed));
//        list.add(String.format(getString(R.string.coupon_used),couponUsed));
//        list.add(String.format(getString(R.string.coupon_expire),couponExpire));
        mMyTypeStatusAdapter.setmGetTypeList(list);
        mLvTypeStatus.setAdapter(mMyTypeStatusAdapter);
        mLvTypeStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMyTypeStatusAdapter.setMPosition(position);
                switch (position){
                    case 0:
                        status = 3;
                        addLayout.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        status = 4;
                        addLayout.setVisibility(View.GONE);
                        break;
                    case 2:
                        status = 9;
                        addLayout.setVisibility(View.GONE);
                        break;
                }
                adapter.setUserMode(status);
                relakey.setVisibility(View.GONE);
                getDiscount();
            }
        });

        hideshow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                relakey.setVisibility(View.GONE);

            }
        });
        // edit.setInputType(InputType.TYPE_NULL);
        hideSoftInputMethod(discountEt);
        /*discountEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relakey.setVisibility(View.VISIBLE);
                hideshow.setVisibility(View.VISIBLE);

                new KeyboardUtil(MyDiscountActivity.this, MyDiscountActivity.this, discountEt).showKeyboard();
            }
        });*/

        discountEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                relakey.setVisibility(View.VISIBLE);
                hideshow.setVisibility(View.VISIBLE);

                new KeyboardUtil(MyDiscountActivity.this, MyDiscountActivity.this, discountEt).showKeyboard();
                return false;
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.app_blue, R.color.app_blue, R.color.app_blue, R.color.app_blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDiscount();
            }
        });
        mFlLoading.setRetryButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDiscount();
            }
        });
        adapter = new DiscountListAdapter(MyDiscountActivity.this);
        mDiscountLv.setAdapter(adapter);
        mDiscountLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(MyDiscountActivity.this,OrderDetailActivity.class);
//                intent.putExtra(Constants.KEY_SHOP_ORDER,adapter.getDiscountEntitys().get(position));
//                startActivity(intent);
            }
        });
        loadMore(false);

        addDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(discountEt.getText().toString())){
                    UIUtils.toast("兑换码不能为空");
                }else {
                    addDiscount(discountEt.getText().toString().replace(" ",""));
                }
            }
        });
    }

    // 隐藏系统键盘
    public void hideSoftInputMethod(EditText ed) {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        int currentVersion = android.os.Build.VERSION.SDK_INT;
        String methodName = null;
        if (currentVersion >= 16) {
            // 4.2
            methodName = "setShowSoftInputOnFocus";
        } else if (currentVersion >= 14) {
            // 4.0
            methodName = "setSoftInputShownOnFocus";
        }

        if (methodName == null) {
            ed.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method setShowSoftInputOnFocus;
            try {
                setShowSoftInputOnFocus = cls.getMethod(methodName,
                        boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(ed, false);
            } catch (NoSuchMethodException e) {
                ed.setInputType(InputType.TYPE_NULL);
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void loadMore(boolean toLoad){
        if(toLoad){
            mDiscountLv.setOnScrollToRefreshListener(new ScrollToFooterLoadMoreListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                    getDiscount();
                }
            });
        }else {
            mDiscountLv.setOnScrollToRefreshListener(new ScrollToFooterLoadMoreListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                    mDiscountLv.refreshComplete();
                }
            });
        }
    }

    //获取优惠券
    void getDiscount(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(MyDiscountActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(API.API_GET_MY_DISCUNT)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("status",status)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<DiscountModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(DiscountModel resp) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if(resp.data != null && !resp.data.isEmpty()) {
                            mFlLoading.showLoadingView(false);
                            adapter.setDiscountEntitys(resp.data);
                        }else {
                            mFlLoading.showError(getString(R.string.no_data),false);
                        }


                    }

                    @Override
                    public void onBtwError(BtwRespError<DiscountModel> error) {
                        Toast.makeText(MyDiscountActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(MyDiscountActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
//                        getOrder();
                    }
                }).excute(DiscountModel.class);
    }

    //添加优惠券
    void addDiscount(String disount){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(MyDiscountActivity.this, Constants.USERINFO, Constants.UID));

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("sign",disount);

        withBtwVolley().load(API.API_ADD_DISCOUNT)
                .method(Request.Method.POST)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("sign",disount)
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
                        UIUtils.toast("添加成功");
                        getDiscount();
                    }

                    @Override
                    public void onBtwError(BtwRespError<String> error) {
                        Toast.makeText(MyDiscountActivity.this, error.errorMessage,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(MyDiscountActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute();
    }
}
