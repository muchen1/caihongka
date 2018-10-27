package com.rainbowcard.client.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
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
import com.rainbowcard.client.model.BankInfoModel;
import com.rainbowcard.client.model.CashInfoModel;
import com.rainbowcard.client.model.CashRecordModel;
import com.rainbowcard.client.model.UserBaseModel;
import com.rainbowcard.client.ui.adapter.CashRecordListAdapter;
import com.rainbowcard.client.ui.adapter.MyTypeStatusAdapter;
import com.rainbowcard.client.utils.DensityUtil;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.StringUtil;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.widget.HorizontalListView;
import com.rainbowcard.client.widget.LoadingFrameLayout;
import com.rainbowcard.client.widget.ScrollToFooterLoadMoreListView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2018-3-6.
 */
public class WithdrawDepositActivity extends MyBaseActivity{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.lv_type_status)
    HorizontalListView mLvTypeStatus;

    @InjectView(R.id.withdraw_layout)
    RelativeLayout withdrawLayout;
    @InjectView(R.id.bank_icon)
    ImageView bankIcon;
    @InjectView(R.id.tv_bank)
    TextView bankTv;
    @InjectView(R.id.all_withdraw)
    TextView allWithdraw;
    @InjectView(R.id.edit_text)
    TextView editTv;
    @InjectView(R.id.price_edit)
    EditText priceEdit;
    @InjectView(R.id.tv_error)
    TextView errorTv;
    @InjectView(R.id.submit_btn)
    Button submitBtn;
    @InjectView(R.id.tv_hint)
    TextView hintTv;
    @InjectView(R.id.tv_faq)
    TextView faqTv;

    @InjectView(R.id.v_frame)
    LoadingFrameLayout mFlLoading;
    @InjectView(R.id.v_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.lv_cash_record)
    ScrollToFooterLoadMoreListView mCashRecordLv;

    MyTypeStatusAdapter mMyTypeStatusAdapter;
    CashRecordListAdapter cashRecordListAdapter;

    String token;
    public int status = 0;
    public float maxExtractable;  //最大可提现金额
    public float price ; //提现金额

    private String bankId;

    private boolean isRecord = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_deposit);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(WithdrawDepositActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(WithdrawDepositActivity.this,true);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(WithdrawDepositActivity.this, Constants.USERINFO, Constants.UID));
        status = getIntent().getIntExtra(Constants.MESSAGE_TYPE,0);
        isRecord = getIntent().getBooleanExtra(Constants.KEY_IS_RECORD,false);
        cashRecordListAdapter = new CashRecordListAdapter(WithdrawDepositActivity.this);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserBankInfo();
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.cash_text));
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
        List<String> list = new ArrayList<String>();
        list.add("申请退款");
        list.add("退款记录");
        mMyTypeStatusAdapter.setmGetTypeList(list);

        mLvTypeStatus.setAdapter(mMyTypeStatusAdapter);
        if(isRecord){
            mMyTypeStatusAdapter.setMPosition(1);
            withdrawLayout.setVisibility(View.GONE);
            getWithdrawalRecord();
        }else {
            mMyTypeStatusAdapter.setMPosition(status);
        }
        mLvTypeStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMyTypeStatusAdapter.setMPosition(position);
                status = position;
                switch (position){
                    case 0:
                        withdrawLayout.setVisibility(View.VISIBLE);
                        mFlLoading.setVisibility(View.GONE);
                        break;
                    case 1:
                        withdrawLayout.setVisibility(View.GONE);
                        mFlLoading.setVisibility(View.VISIBLE);
                        getWithdrawalRecord();
                        break;
                }
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.app_blue, R.color.app_blue, R.color.app_blue, R.color.app_blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(status == 1) {
                    getWithdrawalRecord();
                }
            }
        });
        mFlLoading.setRetryButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWithdrawalRecord();
            }
        });

        mCashRecordLv.setAdapter(cashRecordListAdapter);

        mCashRecordLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        loadMore(false);

        priceEdit.setCursorVisible(false);
        priceEdit.addTextChangedListener(new MagicTextLengthWatcher(10));
        priceEdit.setSelection(priceEdit.getText().toString().length());
        priceEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priceEdit.setCursorVisible(true);
                priceEdit.setSelection(priceEdit.getText().toString().length());
            }
        });

        Spannable span = new SpannableString(hintTv.getText());
        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.money_color)), 2,7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        hintTv.setText(span);

        faqTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WithdrawDepositActivity.this,FaqActivity.class);
                startActivity(intent);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(price < 10){
                    UIUtils.toast("退款最低额度为10元");
                }else {
                    getWashInfo(price);
                }
            }
        });
    }

    void refreshUI(BankInfoModel.BankInfoEntity bankInfoEntity){

        bankId = bankInfoEntity.bankId;

        Picasso.with(WithdrawDepositActivity.this).load(String.format(getString(R.string.img_url),bankInfoEntity.bankImg))
                .resize(DensityUtil.dip2px(WithdrawDepositActivity.this,24),DensityUtil.dip2px(WithdrawDepositActivity.this,24))
                .centerCrop()
                .placeholder(R.drawable.banner_detail)
                .error(R.drawable.banner_detail).into(bankIcon);
        bankTv.setText(String.format(getString(R.string.service_type),bankInfoEntity.bankName,bankInfoEntity.bankCard));

        allWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priceEdit.setText(StringUtil.subZeroAndDot(String.valueOf(maxExtractable)));
            }
        });

        editTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WithdrawDepositActivity.this,ModificationBankActivity.class);
                startActivity(intent);
            }
        });

    }

    private void loadMore(boolean toLoad){
        if(toLoad){
            mCashRecordLv.setOnScrollToRefreshListener(new ScrollToFooterLoadMoreListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                    getWithdrawalRecord();
                }
            });
        }else {
            mCashRecordLv.setOnScrollToRefreshListener(new ScrollToFooterLoadMoreListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                    mCashRecordLv.refreshComplete();
                }
            });
        }
    }

    class MagicTextLengthWatcher implements TextWatcher {
        boolean flag = true;
        private int maxLength; // 儲存最大的字串長度
        private int currentEnd = 0; // 儲存目前字串改變的結束位置，例如：abcdefg變成abcd1234efg，變化的結束位置就在索引8

        private int selectionStart;
        private int selectionEnd;

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

            selectionStart = priceEdit.getSelectionStart();
            selectionEnd = priceEdit.getSelectionEnd();

            if (!TextUtils.isEmpty(priceEdit.getText().toString()) && !StringUtil.isOnlyPointNumber(priceEdit.getText().toString())){
                //删除多余输入的字（不会显示出来）
                s.delete(selectionStart - 1, selectionEnd);
                priceEdit.setText(s);
                priceEdit.setSelection(s.toString().length());
            }

            while (calculateLength(s) > maxLength) { // 若變化後的長度超過最大長度
                // 刪除最後變化的字元
                currentEnd--;
                s.delete(currentEnd, currentEnd + 1);
            }

            if (s != null && !s.equals("")) {

                float a = 0;
                try {
                    a = Float.parseFloat(s.toString());
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

    void refreshData(float a) {
        if(a > maxExtractable){
//            errorTv.setVisibility(View.VISIBLE);
            errorTv.setText("输入金额超过可退款金额");
            errorTv.setTextColor(getResources().getColor(R.color.no_aotunym));
            submitBtn.setBackgroundResource(R.drawable.button_darkgray);
            submitBtn.setTextColor(getResources().getColor(R.color.dialog_line_color));
            submitBtn.setEnabled(false);
        }else {
            if(a == 0){
                submitBtn.setBackgroundResource(R.drawable.button_darkgray);
                submitBtn.setTextColor(getResources().getColor(R.color.dialog_line_color));
                submitBtn.setEnabled(false);
            }else {
                submitBtn.setBackgroundResource(R.drawable.query_select_item);
                submitBtn.setTextColor(getResources().getColor(R.color.white));
                submitBtn.setEnabled(true);
            }
//            errorTv.setVisibility(View.GONE);
            errorTv.setText("退款最低额度为10元");
            errorTv.setTextColor(getResources().getColor(R.color.money_color));
        }

        if(a != 0){
            price = a;
        }
    }

    //获取用户免洗券、可提金额等基本信息
    void getUserBase() {
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(WithdrawDepositActivity.this, Constants.USERINFO, Constants.UID));
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

                        maxExtractable = resp.data.realMoney;
                        SpannableString ss = new SpannableString(String.format(getString(R.string.extractable_text), StringUtil.subZeroAndDot(String.valueOf(resp.data.realMoney))));//定义hint的值
                        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(12,true);//设置字体大小 true表示单位是sp
                        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        priceEdit.setHint(new SpannedString(ss));
                    }

                    @Override
                    public void onBtwError(BtwRespError<UserBaseModel> error) {
                        Toast.makeText(WithdrawDepositActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(WithdrawDepositActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(UserBaseModel.class);
    }


    //获取免费券状态
    void getWithdrawalRecord(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(WithdrawDepositActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(API.API_GET_WITHDRAWAL_RECORD_LIST)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<CashRecordModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(CashRecordModel resp) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if(resp.data != null && !resp.data.isEmpty()) {
                            mFlLoading.showLoadingView(false);
                            cashRecordListAdapter.setCashRecordEntitys(resp.data);
                        }else {
                            mFlLoading.showError(getString(R.string.no_data),false);
                        }


                    }

                    @Override
                    public void onBtwError(BtwRespError<CashRecordModel> error) {
                        Toast.makeText(WithdrawDepositActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(WithdrawDepositActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(CashRecordModel.class);
    }
    //获取用户银行信息
    void getUserBankInfo(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(WithdrawDepositActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(API.API_GET_USER_BANK_INFO)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<BankInfoModel>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onResponse(BankInfoModel resp) {
                        getUserBase();
                        if(resp.data != null) {
                            refreshUI(resp.data);
                        }
                    }

                    @Override
                    public void onBtwError(BtwRespError<BankInfoModel> error) {
                        Toast.makeText(WithdrawDepositActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(WithdrawDepositActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(BankInfoModel.class);
    }

    //获取提现详情
    void getWashInfo(final float money){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(WithdrawDepositActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(API.API_GET_WASH_INFO)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("money",money)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<CashInfoModel>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onResponse(CashInfoModel resp) {
                        if(resp.data != null) {
                            if(resp.data.chargesMoney == 0){
                                submitCash(resp.data.money);
                            }else {
                                if(resp.data.maxCashMoney == 0){
                                    showDialog(resp.data.money,resp.data.exceedCashMoney,resp.data.chargesMoney);
                                }else {
                                    showDialog(resp.data.chargesMoney,resp.data.maxCashMoney);
                                }
                            }
                        }
                    }

                    @Override
                    public void onBtwError(BtwRespError<CashInfoModel> error) {
                        Toast.makeText(WithdrawDepositActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(WithdrawDepositActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(CashInfoModel.class);
    }

    //提交提现
    void submitCash(float money) {
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(WithdrawDepositActivity.this, Constants.USERINFO, Constants.UID));

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("money",money);
        parameters.put("bank_id",bankId);

        withBtwVolley().load(API.API_SUBMIT_CASH)
                .method(Request.Method.POST)
                .setHeader("Authorization", token)
                .setHeader("Accept", API.VERSION)
                .setParam("money",money)
                .setParam("bank_id",bankId)
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
                        Intent intent = new Intent(WithdrawDepositActivity.this,RemittanceStatusActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onBtwError(BtwRespError<String> error) {
                        Toast.makeText(WithdrawDepositActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(WithdrawDepositActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute();
    }

    private void showDialog(final float cashMoney, float exceedMoney, float chargesMoney) {
        final Dialog dialog = new Dialog(WithdrawDepositActivity.this,R.style.loading_dialog);
        dialog.setContentView(R.layout.ui_charge_hint_dialog);
        Window dialogWindow = dialog.getWindow();
        Display display = getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = -80;
        lp.width = display.getWidth();
        dialogWindow.setAttributes(lp);

        TextView cashMoneyTv = (TextView) dialog.findViewById(R.id.cash_money);
        TextView exceedMoneyTv = (TextView) dialog.findViewById(R.id.exceed_money);
        TextView chargesMoneyTv = (TextView) dialog.findViewById(R.id.charges_money);

        cashMoneyTv.setText(String.format(getString(R.string.price),StringUtil.subZeroAndDot(String.valueOf(cashMoney))));
        exceedMoneyTv.setText(String.format(getString(R.string.price),StringUtil.subZeroAndDot(String.valueOf(exceedMoney))));
        chargesMoneyTv.setText(String.format(getString(R.string.price),StringUtil.subZeroAndDot(String.valueOf(chargesMoney))));


        dialog.findViewById(R.id.alert_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCash(cashMoney);
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

    private void showDialog(float chargesMoney, final float maxCashMoney) {
        final Dialog dialog = new Dialog(WithdrawDepositActivity.this,R.style.loading_dialog);
        dialog.setContentView(R.layout.ui__not_funds_alert_dialog);
        Window dialogWindow = dialog.getWindow();
        Display display = getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = -80;
        lp.width = (int)(display.getWidth() * 0.90);
        dialogWindow.setAttributes(lp);

        TextView hintTv = (TextView) dialog.findViewById(R.id.hint_text);
        hintTv.setText(String.format(getString(R.string.cash_hint),StringUtil.subZeroAndDot(String.valueOf(chargesMoney)),StringUtil.subZeroAndDot(String.valueOf(maxCashMoney))));

        dialog.findViewById(R.id.alert_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCash(maxCashMoney);
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
}
