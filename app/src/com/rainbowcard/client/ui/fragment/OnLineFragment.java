package com.rainbowcard.client.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseFragment;
import com.rainbowcard.client.common.PayActivity;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.CityModel;
import com.rainbowcard.client.model.DistrictModel;
import com.rainbowcard.client.model.GiveEntity;
import com.rainbowcard.client.model.OrderModel;
import com.rainbowcard.client.model.PointsEntity;
import com.rainbowcard.client.model.ProvinceModel;
import com.rainbowcard.client.model.RechargePointsEntity;
import com.rainbowcard.client.service.XmlParserHandler;
import com.rainbowcard.client.ui.GetRainbowCardActivity;
import com.rainbowcard.client.ui.adapter.PointsAdapter;
import com.rainbowcard.client.utils.StringUtil;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.utils.Validation;
import com.rainbowcard.client.widget.MyGridView;
import com.rainbowcard.client.widget.wheel.OnWheelChangedListener;
import com.rainbowcard.client.widget.wheel.WheelView;
import com.rainbowcard.client.widget.wheel.adapters.ArrayWheelAdapter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2016-10-25.
 */
public class OnLineFragment extends MyBaseFragment implements OnWheelChangedListener {

    @InjectView(R.id.view)
    public View view;
    @InjectView(R.id.scrollView)
    public ScrollView mRootScrollView;
    @InjectView(R.id.edit_name)
    EditText nameEdit;
    @InjectView(R.id.edit_phone)
    EditText phoneEdit;
    @InjectView(R.id.edit_address)
    EditText addrEdit;
    @InjectView(R.id.select_layout)
    RelativeLayout selectBtn;
    @InjectView(R.id.wheel_layout)
    RelativeLayout wheelLayout;
    @InjectView(R.id.tv_city)
    TextView cityTv;
    @InjectView(R.id.accomplish)
    TextView accomplishBtn;
    @InjectView(R.id.cancel)
    TextView cancelBtn;
    @InjectView(R.id.id_province)
    WheelView mViewProvince;
    @InjectView(R.id.id_city)
    WheelView mViewCity;
    @InjectView(R.id.id_district)
    WheelView mViewDistrict;
    @InjectView(R.id.grid_view)
    MyGridView gridView;
    @InjectView(R.id.price_edit)
    EditText priceEdit;
    @InjectView(R.id.next_step)
    Button nextStep;

    PointsAdapter adapter;
    private int select;
    private int unitPoints = 1;
    private int defaultMoney = 100;
    private double userRate;

    private String name;
    private String phone;
    private String provinceCity;
    private String area;
    private String address;

    private int price = 100;

    /**
     * 所有省
     */
    protected String[] mProvinceDatas;
    /**
     * key - 省 value - 市
     */
    protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    /**
     * key - 市 values - 区
     */
    protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();

    /**
     * key - 区 values - 邮编
     */
    protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();

    /**
     * 当前省的名称
     */
    protected String mCurrentProviceName;
    /**
     * 当前市的名称
     */
    protected String mCurrentCityName;
    /**
     * 当前区的名称
     */
    protected String mCurrentDistrictName ="";

    /**
     * 当前区的邮政编码
     */
    protected String mCurrentZipCode ="";

    GetRainbowCardActivity instance;
    public static OnLineFragment newInstance(){
        OnLineFragment fragment = new OnLineFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_online,container,false);
        ButterKnife.inject(this,view);
        instance = (GetRainbowCardActivity) getActivity();
        getActivity().getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        refershUi();
    }

    @Override
    public void onResume() {
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

//        getPoints();
    }

    void initView(){
        adapter = new PointsAdapter(getActivity());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSeclection(position);
                select = adapter.getItem(position).price;
//                priceEdit.setText(String.valueOf(adapter.getItem(position).price));
//                priceEdit.setSelection(priceEdit.getText().toString().length());
                priceEdit.setText("");
                price = adapter.getItem(position).price;
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
        priceEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                new Handler().postDelayed(new Runnable() {
//                    public void run() {
//                        up();
//                    }
//                }, 500);
                up();
                instance.isFinish = false;
                return false;
            }
        });

        priceEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                view.setVisibility(View.GONE);
                mRootScrollView.fullScroll(ScrollView.FOCUS_UP);
                priceEdit.setCursorVisible(false);
                if(getActivity().getCurrentFocus() != null) {
                    ((InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });

        setUpListener();
        setUpData();
        addrEdit.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    addrEdit.setCursorVisible(true);// 再次点击显示光标
                    view.setVisibility(View.GONE);
                    mRootScrollView.fullScroll(ScrollView.FOCUS_UP);
                }
                return false;
            }
        });
        phoneEdit.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    phoneEdit.setCursorVisible(true);// 再次点击显示光标
                }
                return false;
            }
        });
        nameEdit.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    nameEdit.setCursorVisible(true);// 再次点击显示光标
                }
                return false;
            }
        });
        wheelLayout.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheelLayout.setVisibility(View.VISIBLE);
                nextStep.setVisibility(View.INVISIBLE);
                nameEdit.setCursorVisible(false);
                phoneEdit.setCursorVisible(false);
                addrEdit.setCursorVisible(false);
                if(getActivity().getCurrentFocus() != null) {
                    ((InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
        accomplishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityTv.setText(mCurrentProviceName + mCurrentCityName + mCurrentDistrictName);
                area = String.format(getString(R.string.area),mCurrentProviceName,mCurrentCityName,mCurrentDistrictName);
                wheelLayout.setVisibility(View.GONE);
                nextStep.setVisibility(View.VISIBLE);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheelLayout.setVisibility(View.GONE);
                nextStep.setVisibility(View.VISIBLE);
            }
        });
//        refreshData(Integer.valueOf(priceEdit.getText().toString()));
        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSave()) {
//                    if (!TextUtils.isEmpty(priceEdit.getText().toString()) && Integer.valueOf(priceEdit.getText().toString()) > 0) {
//                        recharge(Integer.valueOf(priceEdit.getText().toString()));
                        Intent intent = new Intent(getActivity(), PayActivity.class);
                        intent.putExtra(Constants.KEY_RAINBOW_TYPE, Constants.RAINBOW_BUY);
                        intent.putExtra(Constants.KEY_PRICE,price);
                        intent.putExtra(Constants.KEY_NAME,name);
                        intent.putExtra(Constants.KEY_PHONE,phone);
                        intent.putExtra(Constants.KEY_ADDR,provinceCity + address);
                        intent.putExtra(Constants.KEY_IS_DISCOUNT,true);
                        startActivity(intent);
                        getActivity().finish();
//                    } else {
//                        UIUtils.toast("请选择充值金额");
//                    }
//                    nextStep.setBackgroundResource(R.drawable.button_darkgray);
//                    nextStep.setEnabled(false);
                }
            }
        });
    }

    void up(){
        view.setVisibility(View.VISIBLE);
        mRootScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        priceEdit.setCursorVisible(true);
        priceEdit.requestFocus(); //edittext是一个EditText控件
        Timer timer = new Timer(); //设置定时器
        timer.schedule(new TimerTask() {
            @Override
            public void run() { //弹出软键盘的代码
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(priceEdit, InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }, 300); //设置300毫秒的时长

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
        if(a == 100) {
            adapter.setSeclection(0);
        }else if(a == 300){
            adapter.setSeclection(1);
        }else if(a == 500){
            adapter.setSeclection(2);
        }else if(a == 1000){
            adapter.setSeclection(3);
        }else {
            if(a != 0)
                adapter.setSeclection(-1);
        }
        nextStep.setBackgroundResource(R.drawable.query_select_item);
        nextStep.setEnabled(true);
//        priceEdit.setSelection(priceEdit.getText().toString().length());
        price = a;
    }

    //充值金额列表
    void getPoints(){
        withBtwVolley().load(API.API_GET_AMOUNT_LIST)
                .method(Request.Method.GET)
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<RechargePointsEntity>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(RechargePointsEntity resp) {
                        unitPoints = resp.data.unit;
                        defaultMoney = resp.data.defaultMoney;
                        userRate = resp.data.rate;
                        refershUi();
                        if(!resp.data.list.isEmpty()){
                            adapter.setRate(userRate);
                            adapter.setPointsEntitys(resp.data.list,new ArrayList<GiveEntity>());
                        }
                    }

                    @Override
                    public void onBtwError(BtwRespError<RechargePointsEntity> error) {
                        UIUtils.toast(error.errorMessage);
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        UIUtils.toast(getString(R.string.network_error));
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(RechargePointsEntity.class);
    }

    void refershUi(){
        price = 100;
//        priceEdit.setText(String.valueOf(defaultMoney));
        nextStep.setVisibility(View.VISIBLE);
    }

    private boolean isSave(){
        name = nameEdit.getText().toString();
        phone = phoneEdit.getText().toString();
        provinceCity = cityTv.getText().toString();
        address = addrEdit.getText().toString();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(getActivity(), R.string.name_null,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!Validation.isNameStr(name)){
            Toast.makeText(getActivity(), R.string.name_illegal,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(getActivity(), R.string.number_null,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!Validation.isMobile(phone)){
            Toast.makeText(getActivity(), R.string.number_illegal,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(provinceCity)){
            Toast.makeText(getActivity(),R.string.city_null,Toast.LENGTH_LONG).show();
            return false;
        }
        if(TextUtils.isEmpty(address)){
            Toast.makeText(getActivity(),R.string.address_null,Toast.LENGTH_LONG).show();
            return false;
        }
        if(price % 100 != 0){
            Toast.makeText(getActivity(),R.string.recharge_error2,Toast.LENGTH_LONG).show();
            priceEdit.setText("100");
            price = 100;
            adapter.setSeclection(0);
            return false;
        }

        return true;
    }

    //充值下单
    void recharge(final int price){

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("money",price);
        parameters.put("phone",phone);
        parameters.put("address",provinceCity+address);
        parameters.put("user_name",name);

        withBtwVolley().load(API.API_RAINBOW_RECHARGE)
                .setHeader("Accept", API.VERSION)
                .method(Request.Method.POST)
                .setParam("money",price)
                .setParam("phone",phone)
                .setParam("address",provinceCity+address)
                .setParam("user_name",name)
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
                        Intent intent = new Intent(getActivity(), PayActivity.class);
                        /*Bundle bundle = new Bundle();
                        bundle.putString(Constants.KEY_NAME,name);
                        bundle.putString(Constants.KEY_PHONE,phone);
                        bundle.putString(Constants.KEY_RECHARGE_NO,resp.data.tradeNo);
                        bundle.putString(Constants.KEY_ADDR,provinceCity+address);
                        bundle.putInt(Constants.KEY_PRICE,price);
                        bundle.putInt(Constants.KEY_RAINBOW_TYPE,Constants.RAINBOW_BUY);
                        intent.putExtras(bundle);*/
                        intent.putExtra(Constants.KEY_ORDER_MODEL,resp.data);
                        intent.putExtra(Constants.KEY_RAINBOW_TYPE,Constants.RAINBOW_BUY);
                        startActivity(intent);
                        getActivity().finish();
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

                    }
                }).excute(OrderModel.class);
    }

    private void setUpListener() {
        // 添加change事件
        mViewProvince.addChangingListener(this);
        // 添加change事件
        mViewCity.addChangingListener(this);
        // 添加change事件
        mViewDistrict.addChangingListener(this);
        // 添加onclick事件
    }

    private void setUpData() {
        initProvinceDatas();
        mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(getActivity(), mProvinceDatas));
        // 设置可见条目数量
        mViewProvince.setVisibleItems(7);
        mViewCity.setVisibleItems(7);
        mViewDistrict.setVisibleItems(7);
        updateCities();
        updateAreas();
    }

    /**
     * 解析省市区的XML数据
     */

    protected void initProvinceDatas()
    {
        List<ProvinceModel> provinceList = null;
        AssetManager asset = getActivity().getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            // 获取解析出来的数据
            provinceList = handler.getDataList();
            //*/ 初始化默认选中的省、市、区
            if (provinceList!= null && !provinceList.isEmpty()) {
                mCurrentProviceName = provinceList.get(0).getName();
                List<CityModel> cityList = provinceList.get(0).getCityList();
                if (cityList!= null && !cityList.isEmpty()) {
                    mCurrentCityName = cityList.get(0).getName();
                    List<DistrictModel> districtList = cityList.get(0).getDistrictList();
                    mCurrentDistrictName = districtList.get(0).getName();
                    mCurrentZipCode = districtList.get(0).getZipcode();
                }
            }
            //*/
            mProvinceDatas = new String[provinceList.size()];
            for (int i=0; i< provinceList.size(); i++) {
                // 遍历所有省的数据
                mProvinceDatas[i] = provinceList.get(i).getName();
                List<CityModel> cityList = provinceList.get(i).getCityList();
                String[] cityNames = new String[cityList.size()];
                for (int j=0; j< cityList.size(); j++) {
                    // 遍历省下面的所有市的数据
                    cityNames[j] = cityList.get(j).getName();
                    List<DistrictModel> districtList = cityList.get(j).getDistrictList();
                    String[] distrinctNameArray = new String[districtList.size()];
                    DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
                    for (int k=0; k<districtList.size(); k++) {
                        // 遍历市下面所有区/县的数据
                        DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        // 区/县对于的邮编，保存到mZipcodeDatasMap
                        mZipcodeDatasMap.put(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        distrinctArray[k] = districtModel;
                        distrinctNameArray[k] = districtModel.getName();
                    }
                    // 市-区/县的数据，保存到mDistrictDatasMap
                    mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
                }
                // 省-市的数据，保存到mCitisDatasMap
                mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

        }
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mViewProvince) {
            updateCities();
        } else if (wheel == mViewCity) {
            updateAreas();
        } else if (wheel == mViewDistrict) {
            mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
            mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
        }
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {
        int pCurrent = mViewCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);

        if (areas == null) {
            areas = new String[] { "" };
        }
        mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(getActivity(), areas));
        mViewDistrict.setCurrentItem(0);
        mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[0];
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = mViewProvince.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[] { "" };
        }
        mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(getActivity(), cities));
        mViewCity.setCurrentItem(0);
        updateAreas();
    }
}
