package com.rainbowcard.client.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.FinanceOrderModel;
import com.rainbowcard.client.model.FinancePeriodModel;
import com.rainbowcard.client.model.PointsEntity;
import com.rainbowcard.client.ui.adapter.PeriodAdapter;
import com.rainbowcard.client.ui.adapter.PointsAdapter;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.StringUtil;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.widget.LoadingFrameLayout;
import com.rainbowcard.client.widget.MyGridView;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2018-3-6.
 */
public class FreeWashTrialActivity extends MyBaseActivity {

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.fl_loading)
    LoadingFrameLayout mVContainer;

    @InjectView(R.id.price_grid)
    MyGridView priceGrid;
    @InjectView(R.id.period_grid)
    MyGridView periodGrid;

    @InjectView(R.id.price_edit)
    EditText priceEdit;
    @InjectView(R.id.affirm_trial)
    Button affirmTrial;

    PointsAdapter adapter;
    PeriodAdapter periodAdapter;

    private int select = 10000;

    String token;

    private int price = 10000;
    private int day = 90;

    List<PointsEntity> pointsEntityList = new ArrayList<PointsEntity>();
    ArrayList<FinancePeriodModel.FinancePeriodEntity> mFinancePeriodEntitys = new ArrayList<FinancePeriodModel.FinancePeriodEntity>();
    double mAdd;
    int financeCouponMoney;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_wash_trial);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(FreeWashTrialActivity.this, true);
        UIUtils.setMeizuStatusBarDarkIcon(FreeWashTrialActivity.this, true);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(FreeWashTrialActivity.this, Constants.USERINFO, Constants.UID));
        initView();
//        getRechargePrice();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    @Override
    protected void onResume() {
        super.onResume();
        pointsEntityList.clear();
        PointsEntity entity = new PointsEntity();
        entity.price = 5000;
        pointsEntityList.add(entity);
        PointsEntity entity2 = new PointsEntity();
        entity2.price = 10000;
        pointsEntityList.add(entity2);
        PointsEntity entity3 = new PointsEntity();
        entity3.price = 100000;
        pointsEntityList.add(entity3);
        adapter.setmPointsEntitys(pointsEntityList);
        getFormula();
    }

    void initView() {
        mHeadControlPanel.setMiddleTitle(getString(R.string.free_wash_trial));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249, 249, 249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        adapter = new PointsAdapter(FreeWashTrialActivity.this);
        adapter.setSeclection(1);
        priceGrid.setAdapter(adapter);
        priceGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSeclection(position);
                select = adapter.getItem(position).price;
//                priceEdit.setText(String.valueOf(adapter.getItem(position).price));
//                priceEdit.setSelection(priceEdit.getText().toString().length());
                priceEdit.setText("");
                priceEdit.setCursorVisible(false);
                price = adapter.getItem(position).price;
                adapter.setSeclection(position);
                periodAdapter.setFinancePeriodEntitys(mFinancePeriodEntitys,mAdd,financeCouponMoney,price);
            }
        });

        periodAdapter = new PeriodAdapter(FreeWashTrialActivity.this);
        periodAdapter.setSeclection(1);
        periodGrid.setAdapter(periodAdapter);
        periodGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                periodAdapter.setSeclection(position);
                day = periodAdapter.getItem(position).day;
            }
        });



        priceEdit.setCursorVisible(false);

        priceEdit.addTextChangedListener(new MagicTextLengthWatcher(9));
        priceEdit.setSelection(priceEdit.getText().toString().length());
        priceEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priceEdit.setCursorVisible(true);
                priceEdit.setSelection(priceEdit.getText().toString().length());
            }
        });
//        refreshData(Integer.valueOf(priceEdit.getText().toString()));
        affirmTrial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSave()) {
                    financeOrder(price,day);
                }
            }
        });
    }

    class MagicTextLengthWatcher implements TextWatcher {
        boolean flag = true;
        private int maxLength; // 儲存最大的字串長度
        private int currentEnd = 0; // 儲存目前字串改變的結束位置，例如：abcdefg變成abcd1234efg，變化的結束位置就在索引8

        public MagicTextLengthWatcher(final int maxLength) {
            setMaxLength(maxLength);
        }

        public final void setMaxLength(final int maxLength) {
            if (maxLength >= 0) {
                this.maxLength = maxLength;
            } else {
                this.maxLength = 0;
            }
        }

        public int getMaxLength() {
            return this.maxLength;
        }

        @Override
        public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

        }

        int flag_text = 0;

        @Override
        public void onTextChanged(CharSequence s, final int start, final int before, final int count) {
            currentEnd = start + count; // 取得變化的結束位置
        }

        @Override
        public void afterTextChanged(final Editable s) {

            while (calculateLength(s) > maxLength) { // 若變化後的長度超過最大長度
                // 刪除最後變化的字元
                currentEnd--;
                s.delete(currentEnd, currentEnd + 1);
            }
            if (!StringUtil.touzi_ed_values22.equals(priceEdit.getText().toString().trim().replaceAll(",",""))) {
                priceEdit.setText(StringUtil.addComma(priceEdit.getText().toString().trim().replaceAll(",", ""), priceEdit));
                priceEdit.setSelection(StringUtil.addComma(priceEdit.getText().toString().trim().replaceAll(",", ""), priceEdit).length());
            }
            if (s != null && !s.equals("")) {
                int a = 0;
                try {
                    a = Integer.parseInt(s.toString().replace(",",""));
                } catch (NumberFormatException e) {
                    // TODO Auto-generated catch block
                    a = 0;
                }
                refreshData(a);
                return;
            }

        }

        /**
         * 計算字串的長度
         *
         * @param c 傳入字串
         * @return 傳回字串長度
         */
        protected int calculateLength(final CharSequence c) {
            int len = 0;
            final int l = c.length();
            for (int i = 0; i < l; i++) {
                final char tmp = c.charAt(i);
                if (tmp >= 0x20 && tmp <= 0x7E) {
                    // 字元值 32~126 是 ASCII 半形字元的範圍
                    len++;
                } else {
                    // 非半形字元
                    len += 2;
                }
            }
            return len;
        }

    }

    void refreshData(int a) {
        Log.d("GCCCCCC","???????<><><><><>"+a);
        if (select != a) {
            if (a == pointsEntityList.get(0).price) {
                adapter.setSeclection(0);
            } else if (a == pointsEntityList.get(1).price) {
                adapter.setSeclection(1);
            } else if (a == pointsEntityList.get(2).price) {
                adapter.setSeclection(2);
            } else {
                if(a != 0)
                    adapter.setSeclection(-1);
            }
        } else {
            if (a == pointsEntityList.get(0).price) {
                adapter.setSeclection(0);
            } else if (a == pointsEntityList.get(1).price) {
                adapter.setSeclection(1);
            } else if (a == pointsEntityList.get(2).price) {
                adapter.setSeclection(2);
            } else {
                if(a != 0)
                    adapter.setSeclection(-1);
            }
        }
        if(a != 0) {
            price = a;
            periodAdapter.setFinancePeriodEntitys(mFinancePeriodEntitys, mAdd, financeCouponMoney, price);
        }
        if(a == 0){
            price = 10000;
            adapter.setSeclection(1);
            periodAdapter.setFinancePeriodEntitys(mFinancePeriodEntitys, mAdd, financeCouponMoney, price);
        }
        affirmTrial.setBackgroundResource(R.drawable.query_select_item);
        affirmTrial.setEnabled(true);
//        priceEdit.setSelection(priceEdit.getText().toString().length());

    }

    //理财下单
    void financeOrder(int money,int days) {
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(FreeWashTrialActivity.this, Constants.USERINFO, Constants.UID));

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("money",money);
        parameters.put("day",days);

        withBtwVolley().load(API.API_FINANCE_ORDER)
                .method(Request.Method.POST)
                .setHeader("Authorization", token)
                .setHeader("Accept", API.VERSION)
                .setParam("money",money)
                .setParam("day",days)
                .setParam("paramSign",StringUtil.createParameterSign(parameters))
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<FinanceOrderModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(FinanceOrderModel resp) {
                        Intent intent = new Intent(FreeWashTrialActivity.this,FreeWashOrderActivity.class);
                        intent.putExtra(Constants.KEY_FINANCE_ORDER_ENTITY,resp.data);
                        startActivity(intent);
                    }

                    @Override
                    public void onBtwError(BtwRespError<FinanceOrderModel> error) {
                        Toast.makeText(FreeWashTrialActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(FreeWashTrialActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(FinanceOrderModel.class);
    }



    private boolean isSave() {
        if (price < 100) {
            Toast.makeText(FreeWashTrialActivity.this, R.string.recharge_error1,
                    Toast.LENGTH_SHORT).show();
            priceEdit.setText("100");
            price = 100;
            return false;
        }

        return true;
    }

    //获取金融计算公式
    void getFormula() {
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(FreeWashTrialActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(API.API_GET_FORMULA)
                .method(Request.Method.GET)
                .setHeader("Authorization", token)
                .setHeader("Accept", API.VERSION)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<FinancePeriodModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(FinancePeriodModel resp) {
                        if(resp.data != null && !resp.data.goods.isEmpty()){
                            mFinancePeriodEntitys.clear();
                            mFinancePeriodEntitys.addAll(resp.data.goods);
                            financeCouponMoney = resp.data.financeCouponMoney;
                            mAdd = resp.data.add;
                            periodAdapter.setFinancePeriodEntitys(resp.data.goods,mAdd,financeCouponMoney,price);
                        }else {
                            UIUtils.toast("没有获得理财信息");
                        }
//                        refershUi();
                    }

                    @Override
                    public void onBtwError(BtwRespError<FinancePeriodModel> error) {
                        Toast.makeText(FreeWashTrialActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(FreeWashTrialActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
//                        getMyCard();
                    }
                }).excute(FinancePeriodModel.class);
    }




}
