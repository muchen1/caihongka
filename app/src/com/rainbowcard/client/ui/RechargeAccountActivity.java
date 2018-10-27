package com.rainbowcard.client.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.common.PayActivity;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.CertificationModel;
import com.rainbowcard.client.model.GiveEntity;
import com.rainbowcard.client.model.PointsEntity;
import com.rainbowcard.client.model.PointsModel;
import com.rainbowcard.client.ui.adapter.PointsAdapter;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.widget.LoadingFrameLayout;
import com.rainbowcard.client.widget.MyGridView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017-8-29.
 */
public class RechargeAccountActivity extends MyBaseActivity {

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.fl_loading)
    LoadingFrameLayout mVContainer;
    @InjectView(R.id.recharge_layout)
    LinearLayout rechargeLayout;
    @InjectView(R.id.select_layout)
    RelativeLayout selectLayout;
    @InjectView(R.id.grid_view)
    MyGridView gridView;
    @InjectView(R.id.et_card)
    EditText cardEdit;
    @InjectView(R.id.price_edit)
    EditText priceEdit;
    @InjectView(R.id.next_step)
    Button nextStep;
    @InjectView(R.id.recharge_agreement)
    TextView rechargeAgreement;
    @InjectView(R.id.select_text)
    TextView selectTv;

    private int isReturnFreeWashOrder; //是否返回免费洗车券下单 为1返回

    PointsAdapter adapter;
    private int select = 100;
    private int defaultMoney = 100;

    //类型  0账户余额  1绑定卡片  2其它卡片
    private int ISTYPE = 0;

    String token;

    private String cityId;
    private float rebate;
    private int price = 100;

    List<PointsEntity> pointsEntityList = new ArrayList<PointsEntity>();
    ArrayList<GiveEntity> mGiveEntitys = new ArrayList<GiveEntity>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private int isCertification = 1;  //充值是否需要实名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_account);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(RechargeAccountActivity.this, true);
        UIUtils.setMeizuStatusBarDarkIcon(RechargeAccountActivity.this, true);
        cityId = getIntent().getStringExtra(Constants.KEY_CITY_ID);
        isReturnFreeWashOrder = getIntent().getIntExtra(Constants.KEY_IS_RETURN_FREE_WACH_ORDER,0);
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
        entity.points = 20;
        entity.price = 100;
        pointsEntityList.add(entity);
        PointsEntity entity2 = new PointsEntity();
        entity2.points = 50;
        entity2.price = 300;
        pointsEntityList.add(entity2);
        PointsEntity entity3 = new PointsEntity();
        entity3.points = 100;
        entity3.price = 500;
        pointsEntityList.add(entity3);
        PointsEntity entity4 = new PointsEntity();
        entity4.points = 100;
        entity4.price = 1000;
        pointsEntityList.add(entity4);
        adapter.setmPointsEntitys(pointsEntityList);

        /*mGiveEntitys.clear();
        GiveEntity giveEntity1 = new GiveEntity();
        giveEntity1.min = 50;
        giveEntity1.max = 300;
        giveEntity1.rebate = 20;
        mGiveEntitys.add(giveEntity1);
        GiveEntity giveEntity2 = new GiveEntity();
        giveEntity2.min = 300;
        giveEntity2.max = 500;
        giveEntity2.rebate = 50;
        mGiveEntitys.add(giveEntity2);
        GiveEntity giveEntity3 = new GiveEntity();
        giveEntity3.min = 500;
        giveEntity3.max = 9000;
        giveEntity3.rebate = 100;
        mGiveEntitys.add(giveEntity3);*/
//        price = 100;
        getRechargePrice();
//        getPoints();
    }

    void initView() {
        mHeadControlPanel.setMiddleTitle(getString(R.string.account_recharge));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249, 249, 249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        selectLayout.setVisibility(View.GONE);
        adapter = new PointsAdapter(RechargeAccountActivity.this);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSeclection(position);
                select = adapter.getItem(position).price;
//                priceEdit.setText(String.valueOf(adapter.getItem(position).price));
//                priceEdit.setSelection(priceEdit.getText().toString().length());
                priceEdit.setText("");
                priceEdit.setCursorVisible(false);
                price = adapter.getItem(position).price;
                for (int i = 0;i < mGiveEntitys.size();i++){
                    if(mGiveEntitys.get(i).min <= price && price < mGiveEntitys.get(i).max){
                        rebate = price * mGiveEntitys.get(i).rebate / 100f;
                        break;
                    }else {
                        rebate = 0;
                    }
                }
            }
        });


        priceEdit.setCursorVisible(false);
        Spannable span = new SpannableString(getString(R.string.recharge_agreement));
//        span.setSpan(new AbsoluteSizeSpan(58), 16, 22, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.stroke)), 15, 21, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            span.setSpan(new BackgroundColorSpan(Color.YELLOW), 11, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        rechargeAgreement.setText(span);
        rechargeAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RechargeAccountActivity.this, AgreementActivity.class);
                intent.putExtra(Constants.KEY_AGREEMENT_TYPE, 1);
                startActivity(intent);
            }
        });

        priceEdit.addTextChangedListener(new MagicTextLengthWatcher(7));
        priceEdit.setSelection(priceEdit.getText().toString().length());
        priceEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priceEdit.setCursorVisible(true);
                priceEdit.setSelection(priceEdit.getText().toString().length());
            }
        });
//        refreshData(Integer.valueOf(priceEdit.getText().toString()));
        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSave()) {
//                    if(isCertification == 0){
//                        goPay();
//                    }else {
//
//                    }
                    getCertification(price+"");
                }
            }
        });
    }

    void goPay(){
        Intent intent = new Intent(RechargeAccountActivity.this, PayActivity.class);
        intent.putExtra(Constants.KEY_RAINBOW_TYPE, Constants.RAINBOW_RECHARGE_ACCOUNT);
        intent.putExtra(Constants.KEY_PRICE, price);
        intent.putExtra(Constants.KEY_REBATE,rebate);
        if(rebate > 0){
            intent.putExtra(Constants.KEY_IS_DISCOUNT,false);
        }else {
            intent.putExtra(Constants.KEY_IS_DISCOUNT,true);
        }
//                    if(mGiveEntitys.isEmpty()){
//                        intent.putExtra(Constants.KEY_IS_DISCOUNT,true);
//                    }else {
//                        intent.putExtra(Constants.KEY_IS_DISCOUNT,false);
//                    }
        intent.putExtra(Constants.KEY_IS_TYPE, ISTYPE);
        intent.putExtra(Constants.KEY_CITY_ID, cityId);
        intent.putExtra(Constants.KEY_IS_RETURN_FREE_WACH_ORDER,isReturnFreeWashOrder);
        startActivity(intent);
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
            if (s != null && !s.equals("")) {
                int a = 0;
                try {
                    a = Integer.parseInt(s.toString());
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
        rebate = 0;
        if (select != a) {
            if (a == pointsEntityList.get(0).price) {
                adapter.setSeclection(0);
            } else if (a == pointsEntityList.get(1).price) {
                adapter.setSeclection(1);
            } else if (a == pointsEntityList.get(2).price) {
                adapter.setSeclection(2);
            } else if (a == pointsEntityList.get(3).price) {
                adapter.setSeclection(3);
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
            } else if (a == pointsEntityList.get(3).price) {
                adapter.setSeclection(3);
            } else {
                if(a != 0)
                    adapter.setSeclection(-1);
            }
        }
        if(a != 0)
            price = a;
        for (int i = 0;i < mGiveEntitys.size();i++){
            if(mGiveEntitys.get(i).min <= price && price < mGiveEntitys.get(i).max){
                rebate = price * mGiveEntitys.get(i).rebate / 100f;
            }
        }
        nextStep.setBackgroundResource(R.drawable.query_select_item);
        nextStep.setEnabled(true);
//        priceEdit.setSelection(priceEdit.getText().toString().length());

    }

    //充值是否需要认证
    private void getCertification(String rechargeMoney){

        Log.d("GCCCCCCCCC",rechargeMoney);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(RechargeAccountActivity.this, Constants.USERINFO, Constants.UID));

        Log.d("GCCCCCCCCCC???????token=",token);
        withBtwVolley().load(API.API_QUERY_CERTIFICATION)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("recharge_money",rechargeMoney)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<CertificationModel>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onResponse(CertificationModel resp) {
                        if(resp.data.needCertification == 1){
                            showDialog("温馨提示","为了保障您账户的资金安全,\n请先进行实名认证。");
                        }else {
                            goPay();
                        }
                    }

                    @Override
                    public void onBtwError(BtwRespError<CertificationModel> error) {
                        Toast.makeText(RechargeAccountActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(RechargeAccountActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(CertificationModel.class);
    }

    //获取可充值金额数
    void getRechargePrice() {
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(RechargeAccountActivity.this, Constants.USERINFO, Constants.UID));
        Log.d("GCCCCCCCCCC???????token=",token);
        withBtwVolley().load(API.API_GET_RECHARGE_PRICE)
                .method(Request.Method.GET)
                .setHeader("Authorization", token)
                .setHeader("Accept", API.VERSION)
                .setParam("city_id", cityId)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<PointsModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(PointsModel resp) {
                        if (resp.data != null && !resp.data.isEmpty()) {
                            mGiveEntitys = resp.data;
//                            adapter.setPointsEntitys(pointsEntityList,mGiveEntitys);
                        }else {
//                            adapter.setPointsEntitys(pointsEntityList,new ArrayList<GiveEntity>());
                        }
                        adapter.setmGiveEntitys(resp.data);
                        adapter.setRate(1);
                        refershUi();
                    }

                    @Override
                    public void onBtwError(BtwRespError<PointsModel> error) {
                        Toast.makeText(RechargeAccountActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(RechargeAccountActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
//                        getMyCard();
                    }
                }).excute(PointsModel.class);
    }


    void refershUi() {
//        priceEdit.setText(String.valueOf(defaultMoney));
//        price = defaultMoney;
//        priceEdit.setText("");
//        adapter.setSeclection(0);
        for (int i = 0;i < mGiveEntitys.size();i++){
            if(mGiveEntitys.get(i).min <= price && price < mGiveEntitys.get(i).max){
                rebate = price * mGiveEntitys.get(i).rebate / 100f;
                break;
            }else {
                rebate = 0;
            }
        }
//        nextStep.setVisibility(View.VISIBLE);
    }

    private boolean isSave() {
        if (price < 50) {
            Toast.makeText(RechargeAccountActivity.this, R.string.recharge_error,
                    Toast.LENGTH_SHORT).show();
            priceEdit.setText("50");
            price = 50;
            for (int i = 0;i < mGiveEntitys.size();i++){
                if(mGiveEntitys.get(i).min <= 50 && 50 < mGiveEntitys.get(i).max){
                    rebate = 50 * mGiveEntitys.get(i).rebate / 100;
                }else {
                    rebate = 0;
                }
            }
            return false;
        }

        return true;
    }

    private void showDialog(String title, String tip) {
        final Dialog dialog = new Dialog(RechargeAccountActivity.this,R.style.loading_dialog);
        dialog.setContentView(R.layout.ui_ios_alert_dialog);
        Window dialogWindow = dialog.getWindow();
        Display display = getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = -80;
        lp.width = (int)(display.getWidth() * 0.90);
        dialogWindow.setAttributes(lp);

        TextView alertTitle = (TextView) dialog.findViewById(R.id.alert_title);
        TextView alertTip = (TextView) dialog.findViewById(R.id.alert_tip);

        alertTitle.setText(title);
        alertTip.setText(tip);

        dialog.findViewById(R.id.alert_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextStep.setText("继续充值");
                Intent intent = new Intent(RechargeAccountActivity.this,AuthenticationActivity.class);
                startActivity(intent);
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
