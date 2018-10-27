package com.rainbowcard.client.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
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
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.common.PayActivity;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.CardEntity;
import com.rainbowcard.client.model.CardInfoModel;
import com.rainbowcard.client.model.CardModel;
import com.rainbowcard.client.model.GiveEntity;
import com.rainbowcard.client.model.OrderModel;
import com.rainbowcard.client.model.PointsEntity;
import com.rainbowcard.client.ui.adapter.PointsAdapter;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.PrefsManager;
import com.rainbowcard.client.utils.StringUtil;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.utils.Validation;
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
 * Created by gc on 2016-10-25.
 */
public class RechargeRainbowCardActivity extends MyBaseActivity {

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.fl_loading)
    LoadingFrameLayout mVContainer;
    @InjectView(R.id.info_layout)
    LinearLayout infoLayout;
    @InjectView(R.id.recharge_layout)
    LinearLayout rechargeLayout;
    @InjectView(R.id.et_card)
    EditText cardEdit;
    @InjectView(R.id.et_name)
    EditText nameEdit;
    @InjectView(R.id.et_phone)
    EditText phoneEdit;
    @InjectView(R.id.grid_view)
    MyGridView gridView;
    @InjectView(R.id.price_edit)
    EditText priceEdit;
    @InjectView(R.id.next_step)
    Button nextStep;

    @InjectView(R.id.card_layout)
    RelativeLayout cardLayout;
    @InjectView(R.id.select_layout)
    RelativeLayout selectLayout;
    @InjectView(R.id.select_text)
    TextView selectTv;

    PointsAdapter adapter;
    private int select = 100;
    private int unitPoints = 1;
    private int defaultMoney = 100;
    private double userRate;

    private boolean isShow = false;
    //类型  0账户余额  1绑定卡片  2其它卡片
    private int  ISTYPE = 0;

    String token;

    private String card;
    private String name;
    private String phone;
    String lastCard;

    private int price = 100;

    String defaultNo;

    List<CardEntity> cardEntities = new ArrayList<CardEntity>();

    private Handler handler = new Handler();
    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private Runnable delayRun = new Runnable() {

        @Override
        public void run() {
            //在这里调用服务器的卡查询接口，获取数据
            queryCard(cardEdit.getText().toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_account);
        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(RechargeRainbowCardActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(RechargeRainbowCardActivity.this,true);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(RechargeRainbowCardActivity.this, Constants.USERINFO, Constants.UID));
        defaultNo = MyConfig.getSharePreStr(RechargeRainbowCardActivity.this, Constants.USERINFO, Constants.DEFAULT_NO);
        initView();
        getMyCard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<PointsEntity> pointsEntityList = new ArrayList<PointsEntity>();
        PointsEntity entity = new PointsEntity();
        entity.points = 100;
        entity.price = 100;
        pointsEntityList.add(entity);
        PointsEntity entity2 = new PointsEntity();
        entity2.points = 300;
        entity2.price = 300;
        pointsEntityList.add(entity2);
        PointsEntity entity3 = new PointsEntity();
        entity3.points = 500;
        entity3.price = 500;
        pointsEntityList.add(entity3);
        PointsEntity entity4 = new PointsEntity();
        entity4.points = 1000;
        entity4.price = 1000;
        pointsEntityList.add(entity4);
        adapter.setPointsEntitys(pointsEntityList,new ArrayList<GiveEntity>());
//        refershUi();
//        getPoints();
    }

    void initView(){
        mHeadControlPanel.setMiddleTitle(getString(R.string.rainbow_card_recharge));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        adapter = new PointsAdapter(RechargeRainbowCardActivity.this);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSeclection(position);
                select = adapter.getItem(position).price;
                priceEdit.setText("");
//                priceEdit.setText(String.valueOf(adapter.getItem(position).price));
//                priceEdit.setSelection(priceEdit.getText().toString().length());
                price = adapter.getItem(position).price;
//                if(userRate == 0){
//                    points.setText(String.format(getString(R.string.pay_points),adapter.getItem(position).points));
//                }else {
//                    points.setText(String.format(getString(R.string.pay_give_points), adapter.getItem(position).points, (int) (adapter.getItem(position).points * userRate)));
//                }
            }
        });

        //选择充值账户默认显示，注掉不显示
        /*if(TextUtils.isEmpty(defaultNo)){
            selectTv.setText("账户余额");
            ISTYPE = 0;
        }else {
            if(Validation.isMobile(defaultNo)){
                selectTv.setText("账户余额");
                ISTYPE = 0;
            }else {
                selectTv.setText(String.format(getString(R.string.rainbow_no),defaultNo));
                ISTYPE = 1;
            }
        }*/

        card = getIntent().getStringExtra(Constants.KEY_CARD);
        lastCard = PrefsManager.getInstance(this).getLastCard();

        selectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDialog();
            }
        });

        cardEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(delayRun!=null){
                    //每次editText有变化的时候，则移除上次发出的延迟线程
                    handler.removeCallbacks(delayRun);
                }
                //延迟800ms，如果不再输入字符，则执行该线程的run方法
                if(s.toString().length() >= 6) {
                    card = s.toString();
                    handler.postDelayed(delayRun, 800);
                }else {
                    infoLayout.setVisibility(View.GONE);
//                    rechargeLayout.setVisibility(View.GONE);
//                    nextStep.setVisibility(View.GONE);
                }


            }
        });

        priceEdit.addTextChangedListener(new MagicTextLengthWatcher(5));
        priceEdit.setSelection(priceEdit.getText().toString().length());
        priceEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priceEdit.setSelection(priceEdit.getText().toString().length());
            }
        });
//        refreshData(Integer.valueOf(priceEdit.getText().toString()));
        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSave()) {
//                    if (!TextUtils.isEmpty(priceEdit.getText().toString()) && Integer.valueOf(priceEdit.getText().toString()) > 0) {
//                        if(ISTYPE == 0){
//                            rechargeAccount(Integer.valueOf(priceEdit.getText().toString()));
//                        }else {
//                            recharge(Integer.valueOf(priceEdit.getText().toString()));
//                        }
//                    } else {
//                        UIUtils.toast("请选择充值金额");
//                    }
//                    nextStep.setBackgroundResource(R.drawable.button_darkgray);
//                    nextStep.setEnabled(false);

                    Intent intent = new Intent(RechargeRainbowCardActivity.this, PayActivity.class);
                    if(ISTYPE == 0) {
                        intent.putExtra(Constants.KEY_RAINBOW_TYPE, Constants.RAINBOW_RECHARGE_ACCOUNT);
                    }else {
                        intent.putExtra(Constants.KEY_RAINBOW_TYPE, Constants.RAINBOW_RECHARGE);
                    }
                    intent.putExtra(Constants.KEY_CARD,card);
                    intent.putExtra(Constants.KEY_PRICE,price);
                    intent.putExtra(Constants.KEY_NAME,name);
                    intent.putExtra(Constants.KEY_PHONE,phone);
                    intent.putExtra(Constants.KEY_IS_SHOW,isShow);
                    intent.putExtra(Constants.KEY_IS_TYPE,ISTYPE);
                    intent.putExtra(Constants.KEY_IS_DISCOUNT,true);
                    startActivity(intent);

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

        int flag_text=0;
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
         * @param c
         *            傳入字串
         *
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

    void refreshData(int a){
        if(select != a) {
            if(a == 100) {
                adapter.setSeclection(0);
            }else if(a == 300){
                adapter.setSeclection(1);
            }else if(a == 500){
                adapter.setSeclection(2);
            }else if(a == 1000){
                adapter.setSeclection(3);
            } else {
                if(a != 0)
                    adapter.setSeclection(-1);
            }
        }else {
            if(a == 100) {
                adapter.setSeclection(0);
            }else if(a == 300){
                adapter.setSeclection(1);
            }else if(a == 500){
                adapter.setSeclection(2);
            }else if(a == 1000){
                adapter.setSeclection(3);
            } else {
                if(a != 0)
                    adapter.setSeclection(-1);
            }
        }
        nextStep.setBackgroundResource(R.drawable.query_select_item);
        nextStep.setEnabled(true);
//        priceEdit.setSelection(priceEdit.getText().toString().length());
        price = a;
//        if(userRate == 0){
//            points.setText(String.format(getString(R.string.pay_points),a * unitPoints));
//        }else {
//            points.setText(String.format(getString(R.string.pay_give_points), a * unitPoints, (int) (a * unitPoints * userRate)));
//        }
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
                            if(resp.data.size() == 1){
                                selectTv.setText(String.format(getString(R.string.rainbow_no),cardEntities.get(0).num));
                                cardLayout.setVisibility(View.GONE);
                                infoLayout.setVisibility(View.GONE);
                                ISTYPE = 1;
                                isShow = false;
                                nextStep.setBackgroundResource(R.drawable.query_select_item);
                                nextStep.setEnabled(true);
                            }
                        }
                    }

                    @Override
                    public void onBtwError(BtwRespError<CardModel> error) {
                        Toast.makeText(RechargeRainbowCardActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(RechargeRainbowCardActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                       refreshToken();
//                        getMyCard();
                    }
                }).excute(CardModel.class);
    }

    //查询卡是否存在
    void queryCard(final String num){
        withBtwVolley().load(API.API_RAINBOW_INFO)
                .method(Request.Method.GET)
                .setHeader("Accept", API.VERSION)
                .setParam("card_number",num)
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<CardInfoModel>() {
                    @Override
                    public void onStart() {
                        mVContainer.showLoading();
                    }

                    @Override
                    public void onFinish() {
                        mVContainer.showLoadingView(false);
                    }

                    @Override
                    public void onResponse(CardInfoModel resp) {
//                        rechargeLayout.setVisibility(View.VISIBLE);
//                        nextStep.setVisibility(View.VISIBLE);
                        isShow = resp.data.show;
                        if(resp.data.show){
//                            infoLayout.setVisibility(View.VISIBLE);
                        }else {
                            name = resp.data.name;
                            phone = resp.data.phone;
                        }
                        PrefsManager.getInstance(RechargeRainbowCardActivity.this)
                                .setLastCard(num);
                    }

                    @Override
                    public void onBtwError(BtwRespError<CardInfoModel> error) {
                        UIUtils.toast(error.errorMessage);
                        infoLayout.setVisibility(View.GONE);
//                        rechargeLayout.setVisibility(View.GONE);
//                        nextStep.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        UIUtils.toast(getString(R.string.network_error));
                        infoLayout.setVisibility(View.GONE);
//                        rechargeLayout.setVisibility(View.GONE);
//                        nextStep.setVisibility(View.GONE);
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(CardInfoModel.class);
    }

    void loadDialog(){
        Dialog dialog = UIUtils.alertButtonListBottom(RechargeRainbowCardActivity.this,"",true);
        Window dialogWindow = dialog.getWindow();
        Display display = getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int)(display.getWidth());
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.BOTTOM);
        //隐藏账户余额充值，只充值卡片，账户充值放入钱包
        /*UIUtils.addButtonToButtonListBottom(dialog, "账户余额", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTv.setText("账户余额");
                cardLayout.setVisibility(View.GONE);
                infoLayout.setVisibility(View.GONE);
                ISTYPE = 0;
                isShow = false;
                nextStep.setBackgroundResource(R.drawable.query_select_item);
                nextStep.setEnabled(true);
            }
        });*/

        for (int i = 0;i<cardEntities.size();i++){
            final int a = i;
            UIUtils.addButtonToButtonListBottom(dialog, "彩虹卡 "+cardEntities.get(i).num, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectTv.setText(String.format(getString(R.string.rainbow_no),cardEntities.get(a).num));
                    cardLayout.setVisibility(View.GONE);
                    infoLayout.setVisibility(View.GONE);
                    ISTYPE = 1;
                    isShow = false;
                    nextStep.setBackgroundResource(R.drawable.query_select_item);
                    nextStep.setEnabled(true);
                }
            });
        }
        UIUtils.addButtonToButtonListBottom(dialog, "其它卡片", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTv.setText("其它卡片");
                cardLayout.setVisibility(View.VISIBLE);
                ISTYPE = 2;
                isShow = true;
                nextStep.setBackgroundResource(R.drawable.query_select_item);
                nextStep.setEnabled(true);
               /* if(!TextUtils.isEmpty(card)){
                    cardEdit.setText(card);
                    queryCard(card);
                }else {
                    if(!TextUtils.isEmpty(lastCard)){
                        cardEdit.setText(lastCard);
                        queryCard(lastCard);
                    }
                }*/
            }
        });

    }

    void refershUi(){
        price = 100;
//        priceEdit.setText(String.valueOf(defaultMoney));
//        nextStep.setVisibility(View.VISIBLE);
//        if(userRate == 0){
//            points.setText(String.format(getString(R.string.pay_points),defaultMoney * unitPoints));
//        }else {
//            points.setText(String.format(getString(R.string.pay_give_points), defaultMoney * unitPoints, (int) (defaultMoney * unitPoints * userRate)));
//        }
    }

    private boolean isSave(){
        if(ISTYPE == 2) {
            card = cardEdit.getText().toString();
        }else {
            card = selectTv.getText().toString().replace("彩虹卡","").trim();
        }
        if(TextUtils.isEmpty(card)){
            Toast.makeText(RechargeRainbowCardActivity.this, R.string.card_null,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        //不显示用户信息
        /*if(isShow) {
            name = nameEdit.getText().toString();
            phone = phoneEdit.getText().toString();
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(RechargeRainbowCardActivity.this, R.string.name_null,
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!Validation.isNameStr(name)) {
                Toast.makeText(RechargeRainbowCardActivity.this, R.string.name_illegal,
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(RechargeRainbowCardActivity.this, R.string.number_null,
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!Validation.isMobile(phone)) {
                Toast.makeText(RechargeRainbowCardActivity.this, R.string.number_illegal,
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }*/
        if(price < 50){
            Toast.makeText(RechargeRainbowCardActivity.this, R.string.recharge_error,
                    Toast.LENGTH_SHORT).show();
            priceEdit.setText("50");
            price = 50;
            return false;
        }
        if(selectTv.getText().toString().equals("请选择充值卡片")){
            Toast.makeText(RechargeRainbowCardActivity.this, R.string.select_card,
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    //充值下单
    void recharge(final int price){

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("card_number",card);
        parameters.put("money",price);

        BtwVolley btwVolley = withBtwVolley().load(API.API_RAINBOWCARD_RECHARGE)
                .method(Request.Method.POST)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("card_number",card)
                .setParam("money",price)
                .setParam("paramSign", StringUtil.createParameterSign(parameters))
                .setRetrys(0)
                .setUIComponent(this)
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
                        nextStep.setBackgroundResource(R.drawable.query_select_item);
                        nextStep.setEnabled(true);
                        Intent intent = new Intent(RechargeRainbowCardActivity.this, PayActivity.class);
                        /*Bundle bundle = new Bundle();
                        bundle.putString(Constants.KEY_CARD,card);
                        bundle.putString(Constants.KEY_NAME,name);
                        bundle.putString(Constants.KEY_PHONE,phone);
                        bundle.putString(Constants.KEY_RECHARGE_NO,resp.data.tradeNo);
                        bundle.putInt(Constants.KEY_PRICE,price);
                        bundle.putInt(Constants.KEY_RAINBOW_TYPE,Constants.RAINBOW_RECHARGE);
                        intent.putExtras(bundle);*/
                        intent.putExtra(Constants.KEY_ORDER_MODEL,resp.data);
                        intent.putExtra(Constants.KEY_RAINBOW_TYPE,Constants.RAINBOW_RECHARGE);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onBtwError(BtwRespError<OrderModel> error) {
                        UIUtils.toast(error.errorMessage);
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        UIUtils.toast(getString(R.string.network_error));
                    }

                    @Override
                    public void onRefreToken() {
                       refreshToken();
//                        recharge(price);
                    }
                });
        if(isShow){
            btwVolley.setParam("user_name",name);
            btwVolley.setParam("phone",phone);
        }
        btwVolley.excute(OrderModel.class);
    }

    //账户充值
    void rechargeAccount(final int price){

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("phone",card);
        parameters.put("money",price);

        withBtwVolley().load(API.API_RECHARGE_ACCOUNT)
                .method(Request.Method.POST)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("phone",card)
                .setParam("money",price)
                .setParam("paramSign",StringUtil.createParameterSign(parameters))
                .setRetrys(0)
                .setUIComponent(this)
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
                        nextStep.setBackgroundResource(R.drawable.query_select_item);
                        nextStep.setEnabled(true);
                        Intent intent = new Intent(RechargeRainbowCardActivity.this, PayActivity.class);
                        intent.putExtra(Constants.KEY_ORDER_MODEL,resp.data);
                        intent.putExtra(Constants.KEY_RAINBOW_TYPE,Constants.RAINBOW_RECHARGE_ACCOUNT);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onBtwError(BtwRespError<OrderModel> error) {
                        UIUtils.toast(error.errorMessage);
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        UIUtils.toast(getString(R.string.network_error));
                    }

                    @Override
                    public void onRefreToken() {
                       refreshToken();
//                        rechargeAccount(price);
                    }
                }).excute(OrderModel.class);
    }
}
