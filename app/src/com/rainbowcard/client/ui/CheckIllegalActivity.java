package com.rainbowcard.client.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.CheckDetailModel;
import com.rainbowcard.client.model.CityModel2;
import com.rainbowcard.client.model.DefaultCityModel;
import com.rainbowcard.client.model.MarkerInfoModel;
import com.rainbowcard.client.model.ProvinceEntity;
import com.rainbowcard.client.model.ProvinceModel2;
import com.rainbowcard.client.service.XmlParserIllegalHandler;
import com.rainbowcard.client.ui.adapter.ProvinceNumAdapter;
import com.rainbowcard.client.utils.AllCapTransformationMethod;
import com.rainbowcard.client.utils.PrefsManager;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.widget.MyGridView;
import com.rainbowcard.client.widget.wheel.OnWheelChangedListener;
import com.rainbowcard.client.widget.wheel.WheelView;
import com.rainbowcard.client.widget.wheel.adapters.ArrayWheelAdapter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2016-12-28.
 */
public class CheckIllegalActivity extends MyBaseActivity implements OnWheelChangedListener {

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.select_layout)
    RelativeLayout selectLayout;
    @InjectView(R.id.wheel_layout)
    RelativeLayout wheelLayout;
    @InjectView(R.id.tv_city)
    TextView cityTv;
    @InjectView(R.id.brief_layout)
    RelativeLayout briefBtn;
    @InjectView(R.id.brief_text)
    TextView briefTv;
    @InjectView(R.id.plate_number)
    EditText plateNum;
    @InjectView(R.id.engine_layout)
    RelativeLayout engineLayout;
    @InjectView(R.id.edit_engine)
    EditText engineEt;
    @InjectView(R.id.heading_layout)
    RelativeLayout headingLayout;
    @InjectView(R.id.heading_code)
    EditText headingCode;
    @InjectView(R.id.check_btn)
    Button checkBtn;
    @InjectView(R.id.accomplish)
    TextView accomplishBtn;
    @InjectView(R.id.cancel)
    TextView cancelBtn;
    @InjectView(R.id.id_province)
    WheelView mViewProvince;
    @InjectView(R.id.id_city)
    WheelView mViewCity;

    /**
     * 所有省
     */
    protected String[] mProvinceDatas;

    /**
     * 所有省简称
     */
    protected List<String> mIllegalDatas;

    /**
     * key - 省 value - 市
     */
    protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    /**
    /**
     * key - 省 value - 城市代码
     */
    protected Map<String, String[]> mCityCodeMap = new HashMap<String, String[]>();
    /**
     * key - 省 value - 发动机号
     */
    protected Map<String, int[]> mCarEngineMap = new HashMap<String, int[]>();
    /**
     * key - 省 value - 车辆识别代码
     */
    protected Map<String, int[]> mCarClassMap = new HashMap<String, int[]>();
    /**
     * key - 省 value - 渠道类型
     */
    protected Map<String, int[]> mSourceTypeMap = new HashMap<String, int[]>();
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
    /**
     * 当前省的名称
     */
    protected String mCurrentProviceillegal = "京";
    /**
     * 当前市的名称
     */
    protected String mCurrentCityName = "北京";
    /**
     * 当前区的名称
     */
    protected String mCurrentDistrictName ="";

    /**
     * 当前区的邮政编码
     */
    protected String mCurrentZipCode ="";

    ProvinceNumAdapter adapter;
    Dialog dialog;

    boolean isEngine = true;
    boolean isClass = true;
    String cityCode;
    int carEngineLen;
    int carClassLen;
    int sourceType;
    int pCurrent;
    int pCityCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_illegal);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(CheckIllegalActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(CheckIllegalActivity.this,true);
        initView();
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.check_illegal));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        plateNum.setTransformationMethod(new AllCapTransformationMethod());
        initUI();
        setUpListener();
        initProvinceDatas();
        wheelLayout.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        selectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheelLayout.setVisibility(View.VISIBLE);
                checkBtn.setVisibility(View.INVISIBLE);
                if(getCurrentFocus() != null) {
                    ((InputMethodManager) getSystemService(CheckIllegalActivity.this.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(CheckIllegalActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
        accomplishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityTv.setText(mCurrentCityName);
                briefTv.setText(mCurrentProviceillegal);
//                area = String.format(getString(R.string.area),mCurrentProviceName,mCurrentCityName,mCurrentDistrictName);
                wheelLayout.setVisibility(View.GONE);
                checkBtn.setVisibility(View.VISIBLE);
                initHint();
            }
        });
        briefBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheelLayout.setVisibility(View.GONE);
                showDialog();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheelLayout.setVisibility(View.GONE);
                checkBtn.setVisibility(View.VISIBLE);
            }
        });

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCheck()){
                    checkIllegal();
                }
            }
        });
    }

    void initUI(){
        if(TextUtils.isEmpty(PrefsManager.getInstance(this).getCheckCity())){
            getDefaultCheckCity();
        }else {
            cityTv.setText(PrefsManager.getInstance(this).getCheckCity());
            briefTv.setText(PrefsManager.getInstance(this).getCheckProvinceNum());
            plateNum.setText(PrefsManager.getInstance(this).getPlateNum());
            carEngineLen = Integer.parseInt(PrefsManager.getInstance(this).getEngine());
            carClassLen = Integer.parseInt(PrefsManager.getInstance(this).getClassLen());
            sourceType = Integer.parseInt(PrefsManager.getInstance(this).getSource());
            cityCode = PrefsManager.getInstance(this).getCityCode();
            initHint();

        }
    }

    void initHint(){
        switch (carEngineLen){
            case 0:
                engineLayout.setVisibility(View.GONE);
                isEngine = false;
                break;
            case 99:
                engineLayout.setVisibility(View.VISIBLE);
                engineEt.setHint(getString(R.string.hint_engine_all));
                isEngine = true;
                break;
            default:
                isEngine = true;
                engineLayout.setVisibility(View.VISIBLE);
                engineEt.setHint(String.format(getString(R.string.hint_engine_part),carEngineLen));
        }
        switch (carClassLen){
            case 0:
                isClass = false;
                headingLayout.setVisibility(View.GONE);
                break;
            case 99:
                isClass = true;
                headingLayout.setVisibility(View.VISIBLE);
                headingCode.setHint(getString(R.string.hint_heading_all));
                break;
            default:
                isClass = true;
                headingLayout.setVisibility(View.VISIBLE);
                headingCode.setHint(String.format(getString(R.string.hint_heading_part),carClassLen));
        }
    }

    private boolean isCheck(){
        String plateStr = plateNum.getText().toString();
        String engineStr = engineEt.getText().toString();
        String headingCodeStr = headingCode.getText().toString();
        if(TextUtils.isEmpty(plateStr)){
            Toast.makeText(CheckIllegalActivity.this, R.string.plate_null,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if(isEngine) {
            if (TextUtils.isEmpty(engineStr)) {
                Toast.makeText(CheckIllegalActivity.this, R.string.engine_null,
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if(isClass) {
            if (TextUtils.isEmpty(headingCodeStr)) {
                Toast.makeText(CheckIllegalActivity.this, R.string.heading_code_null,
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    private void setUpListener() {
        // 添加change事件
        mViewProvince.addChangingListener(this);
        // 添加change事件
        mViewCity.addChangingListener(this);
        // 添加change事件
    }

    private void setUpData() {
        mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(CheckIllegalActivity.this, mProvinceDatas));
        // 设置可见条目数量
        mViewProvince.setVisibleItems(7);
        mViewCity.setVisibleItems(7);
        if(PrefsManager.getInstance(CheckIllegalActivity.this).getCurrentItem() == -1){
            mViewProvince.setCurrentItem(1);
        }else {
            mViewProvince.setCurrentItem(PrefsManager.getInstance(CheckIllegalActivity.this).getCurrentItem());
            mViewCity.setCurrentItem(PrefsManager.getInstance(CheckIllegalActivity.this).getCityCurrentItem());
        }
        updateCities();
//        updateAreas();
    }

    /**
     * 解析省市区的XML数据
     */

    protected void initProvinceDatas()
    {
        withBtwVolley().load(API.API_GET_ILLEGAL_CHECK_ALL_CITY)
                .method(Request.Method.GET)
                .setHeader("Accept", API.VERSION)
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(20000)
                .setResponseHandler(new BtwVolley.ResponseHandler<ProvinceModel2>() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onResponse(ProvinceModel2 resp) {
                        List<ProvinceEntity> provinceList = resp.data;
                        if (provinceList!= null && !provinceList.isEmpty()) {
                            mCurrentProviceName = provinceList.get(0).name;
                            mCurrentProviceillegal = provinceList.get(0).provinceNum;
                            List<CityModel2> cityList = provinceList.get(0).cityList;
                            if (cityList!= null && !cityList.isEmpty()) {
                                mCurrentCityName = cityList.get(0).name;
                            }
                        }
                        //*/
                        mProvinceDatas = new String[provinceList.size()];
                        mIllegalDatas = new ArrayList<String>();
                        for (int i=0; i< provinceList.size(); i++) {
                            // 遍历所有省的数据
                            mProvinceDatas[i] = provinceList.get(i).name;
                            mIllegalDatas.add(provinceList.get(i).provinceNum);
                            List<CityModel2> cityList = provinceList.get(i).cityList;
                            String[] cityNames = new String[cityList.size()];
                            int[] carEngines = new int[cityList.size()];
                            int[] carClasss = new int[cityList.size()];
                            int[] sourceTypes = new int[cityList.size()];
                            String[] cityCodes = new String[cityList.size()];
                            for (int j=0; j< cityList.size(); j++) {
                                // 遍历省下面的所有市的数据
                                cityNames[j] = cityList.get(j).name;
                                carEngines[j] = cityList.get(j).carEngineLen;
                                carClasss[j] = cityList.get(j).carClassLen;
                                sourceTypes[j] = cityList.get(j).sourceType;
                                cityCodes[j] = cityList.get(j).cityCode;
                            }
                            // 省-市的数据，保存到mCitisDatasMap
                            mCitisDatasMap.put(provinceList.get(i).name, cityNames);
                            mCarEngineMap.put(provinceList.get(i).name,carEngines);
                            mCarClassMap.put(provinceList.get(i).name,carClasss);
                            mSourceTypeMap.put(provinceList.get(i).name,sourceTypes);
                            mCityCodeMap.put(provinceList.get(i).name,cityCodes);
                        }
                        setUpData();
                    }

                    @Override
                    public void onBtwError(BtwRespError<ProvinceModel2> error) {
                        UIUtils.toast(error.errorMessage);
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        UIUtils.toast(getString(R.string.network_error));
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(ProvinceModel2.class);
        /*List<ProvinceModel2> provinceList = null;
        AssetManager asset = CheckIllegalActivity.this.getAssets();
        try {
            InputStream input = asset.open("illegal_province_data.xml");
            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();
            XmlParserIllegalHandler handler = new XmlParserIllegalHandler();
            parser.parse(input, handler);
            input.close();
            // 获取解析出来的数据
            provinceList = handler.getDataList();
            /*//*//* 初始化默认选中的省、市、区
            if (provinceList!= null && !provinceList.isEmpty()) {
                mCurrentProviceName = provinceList.get(0).getName();
                mCurrentProviceillegal = provinceList.get(0).getIllegal();
                List<CityModel2> cityList = provinceList.get(0).getCityList();
                if (cityList!= null && !cityList.isEmpty()) {
                    mCurrentCityName = cityList.get(0).getName();
                }
            }
            /*//*//*
            mProvinceDatas = new String[provinceList.size()];
            mIllegalDatas = new String[provinceList.size()];
            for (int i=0; i< provinceList.size(); i++) {
                // 遍历所有省的数据
                mProvinceDatas[i] = provinceList.get(i).getName();
                mIllegalDatas[i] = provinceList.get(i).getIllegal();
                List<CityModel2> cityList = provinceList.get(i).getCityList();
                String[] cityNames = new String[cityList.size()];
                for (int j=0; j< cityList.size(); j++) {
                    // 遍历省下面的所有市的数据
                    cityNames[j] = cityList.get(j).getName();
                }
                // 省-市的数据，保存到mCitisDatasMap
                mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

        }*/
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mViewProvince) {
            updateCities();
        } else if (wheel == mViewCity) {
            updateAreas();
        } /*else if (wheel == mViewDistrict) {
            mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
            mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
        }*/
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {
        pCityCurrent = mViewCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCityCurrent];
        cityCode = mCityCodeMap.get(mCurrentProviceName)[pCityCurrent];
        carEngineLen = mCarEngineMap.get(mCurrentProviceName)[pCityCurrent];
        carClassLen = mCarClassMap.get(mCurrentProviceName)[pCityCurrent];
        sourceType = mSourceTypeMap.get(mCurrentProviceName)[pCityCurrent];
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);

        if (areas == null) {
            areas = new String[] { "" };
        }
//        mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(getActivity(), areas));
//        mViewDistrict.setCurrentItem(0);
//        mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[0];
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        pCurrent = mViewProvince.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        mCurrentProviceillegal = mIllegalDatas.get(pCurrent);
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[] { "" };
        }
        mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(CheckIllegalActivity.this, cities));
        mViewCity.setCurrentItem(0);
        updateAreas();
    }

    //查询违章
    void checkIllegal(){
        withBtwVolley().load(API.API_GET_CHECK_DETAIL)
                .method(Request.Method.POST)
                .setHeader("Accept", API.VERSION)
                .setParam("city",cityCode)
                .setParam("city_name",cityTv.getText().toString())
                .setParam("hphm",briefTv.getText().toString()+plateNum.getText().toString())
                .setParam("engineno",engineEt.getText().toString())
                .setParam("classno",headingCode.getText().toString())
                .setParam("channel",sourceType)
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(20000)
                .setResponseHandler(new BtwVolley.ResponseHandler<CheckDetailModel>() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onResponse(CheckDetailModel resp) {

                        PrefsManager.getInstance(CheckIllegalActivity.this).setCheckCity(cityTv.getText().toString());
                        PrefsManager.getInstance(CheckIllegalActivity.this).setCheckProvinceNum(briefTv.getText().toString());
                        PrefsManager.getInstance(CheckIllegalActivity.this).setCityCode(cityCode);
                        PrefsManager.getInstance(CheckIllegalActivity.this).setEngine(String.valueOf(carEngineLen));
                        PrefsManager.getInstance(CheckIllegalActivity.this).setClassLen(String.valueOf(carClassLen));
                        PrefsManager.getInstance(CheckIllegalActivity.this).setSource(String.valueOf(sourceType));
                        PrefsManager.getInstance(CheckIllegalActivity.this).setCurrenItem(pCurrent);
                        PrefsManager.getInstance(CheckIllegalActivity.this).setCityCurrentItem(pCityCurrent);
                        PrefsManager.getInstance(CheckIllegalActivity.this).setPlateNum(plateNum.getText().toString());

                        Intent intent = new Intent(CheckIllegalActivity.this,SearchResultActivity.class);
                        intent.putExtra(Constants.KEY_SIGN,resp.data);
                        startActivity(intent);
                    }

                    @Override
                    public void onBtwError(BtwRespError<CheckDetailModel> error) {
                        UIUtils.toast(error.errorMessage);
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        UIUtils.toast(getString(R.string.network_error));
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(CheckDetailModel.class);
    }

    //获取默认查询城市
    void getDefaultCheckCity(){
        withBtwVolley().load(API.API_GET_DEFAULT_CHECK_CITY)
                .method(Request.Method.GET)
                .setHeader("Accept", API.VERSION)
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(20000)
                .setResponseHandler(new BtwVolley.ResponseHandler<DefaultCityModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(DefaultCityModel resp) {
                        cityTv.setText(resp.data.name);
                        briefTv.setText(resp.data.preCarNum);
                        carEngineLen = resp.data.carEngineLen;
                        carClassLen = resp.data.carClassLen;
                        sourceType = resp.data.sourceType;
                        initHint();

                    }

                    @Override
                    public void onBtwError(BtwRespError<DefaultCityModel> error) {
                        UIUtils.toast(error.errorMessage);
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        UIUtils.toast(getString(R.string.network_error));
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(DefaultCityModel.class);
    }

    private void showDialog() {
        dialog = new Dialog(CheckIllegalActivity.this,R.style.customDialog);
        dialog.setContentView(R.layout.ui_province_num_bottom);
        Window dialogWindow = dialog.getWindow();
        Display display = getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = -80;
        lp.width = (int)(display.getWidth());
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.BOTTOM);
        MyGridView gridView = (MyGridView) dialog.findViewById(R.id.grid_view);
        adapter = new ProvinceNumAdapter(CheckIllegalActivity.this);
        adapter.setProvinceNums(mIllegalDatas);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                briefTv.setText(mIllegalDatas.get(position));
                adapter.setSeclection(position);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
}
