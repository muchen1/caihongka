package com.rainbowcard.client.ui;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.rainbowcard.client.model.AddressEntity;
import com.rainbowcard.client.model.CityModel;
import com.rainbowcard.client.model.DistrictModel;
import com.rainbowcard.client.model.ProvinceModel;
import com.rainbowcard.client.service.XmlParserHandler;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.StringUtil;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.utils.Validation;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.widget.wheel.OnWheelChangedListener;
import com.rainbowcard.client.widget.wheel.WheelView;
import com.rainbowcard.client.widget.wheel.adapters.ArrayWheelAdapter;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2018/1/2.
 */
public class EditAddressActivity extends MyBaseActivity implements OnWheelChangedListener {

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.tv_name)
    EditText nameEt;
    @InjectView(R.id.tv_number)
    EditText numberEt;
    @InjectView(R.id.tv_address)
    EditText addressEt;
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
    @InjectView(R.id.save)
    Button saveBtn;
    @InjectView(R.id.id_province)
    WheelView mViewProvince;
    @InjectView(R.id.id_city)
    WheelView mViewCity;
    @InjectView(R.id.id_district)
    WheelView mViewDistrict;

    private String name;
    private String number;
    private String provinceCity;
    private String area;
    private String address;
    private String token;
    private AddressEntity addressEntity;
    private int type;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(EditAddressActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(EditAddressActivity.this,true);
        addressEntity = (AddressEntity) getIntent().getSerializableExtra(Constants.KEY_ADDRESS_ENTITY);
        type = getIntent().getIntExtra(Constants.KEY_TYPE,0);
        initView();
    }

    void initView(){
        if(addressEntity != null){
            nameEt.setText(addressEntity.userName);
            numberEt.setText(addressEntity.phone);
            addressEt.setText(addressEntity.addr);
            cityTv.setText(addressEntity.province+addressEntity.city+addressEntity.area);
            mCurrentProviceName = addressEntity.province;
            mCurrentCityName = addressEntity.city;
            mCurrentDistrictName = addressEntity.area;

        }else {
            String phone = MyConfig.getSharePreStr(EditAddressActivity.this, Constants.USERINFO, Constants.PHONE);
            if (!TextUtils.isEmpty(phone) && !phone.equals("0")) {
                numberEt.setText(phone);
            }
        }
        mHeadControlPanel.initHeadPanel();
        if(type == 1) {
            mHeadControlPanel.setMiddleTitle(getString(R.string.edit_consignee_address));
        }else {
            mHeadControlPanel.setMiddleTitle(getString(R.string.add_consignee_address));
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
        setUpListener();
        setUpData();
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheelLayout.setVisibility(View.VISIBLE);
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(EditAddressActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        accomplishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityTv.setText(mCurrentProviceName + mCurrentCityName + mCurrentDistrictName);
                area = String.format(getString(R.string.area),mCurrentProviceName,mCurrentCityName,mCurrentDistrictName);
                Log.d("GCCCCCCCCCCC","editaddress"+area+"///"+mCurrentProviceName+"////"+mCurrentCityName+"?????"+mCurrentDistrictName);
                wheelLayout.setVisibility(View.GONE);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheelLayout.setVisibility(View.GONE);
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSave()) {
                    if(type == 1){
                        edit(addressEntity.id);
                    }else {
                        commit();
                    }
                }
            }
        });
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
        mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(EditAddressActivity.this, mProvinceDatas));
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
        AssetManager asset = getAssets();
        try {
            InputStream input = asset.open("all_province_data.xml");
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
        mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
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
        mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
        mViewCity.setCurrentItem(0);
        updateAreas();
    }

    private boolean isSave(){
        name = nameEt.getText().toString();
        number = numberEt.getText().toString();
        provinceCity = cityTv.getText().toString();
        address = addressEt.getText().toString();
        if(TextUtils.isEmpty(name)){
            Toast.makeText(EditAddressActivity.this, R.string.name_null,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!Validation.isChinaStr(name)){
            Toast.makeText(EditAddressActivity.this, R.string.name_illegal,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(number)){
            Toast.makeText(EditAddressActivity.this, R.string.number_null,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!Validation.isMobile(number)){
            Toast.makeText(EditAddressActivity.this, R.string.number_illegal,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(provinceCity)){
            Toast.makeText(EditAddressActivity.this, R.string.city_null,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(address)){
            Toast.makeText(EditAddressActivity.this, R.string.address_null,
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    void commit(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(EditAddressActivity.this, Constants.USERINFO, Constants.UID));

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("name", URLEncoder.encode(name));
        parameters.put("phone",number);
        parameters.put("province",URLEncoder.encode(mCurrentProviceName));
        parameters.put("city",URLEncoder.encode(mCurrentCityName));
        parameters.put("area",URLEncoder.encode(mCurrentDistrictName));
        parameters.put("address",URLEncoder.encode(address));

        BtwVolley mReques = withBtwVolley().load(API.API_ADD_ADDRESS)
                .method(Request.Method.POST)
                .setHeader("Authorization", token)
                .setHeader("Accept", API.VERSION)
                .setParam("name",name)
                .setParam("phone",number)
                .setParam("province",mCurrentProviceName)
                .setParam("city",mCurrentCityName)
                .setParam("area",mCurrentDistrictName)
                .setParam("address",address)
                .setParam("paramSign", StringUtil.createParameterSign(parameters))
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<String>() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onResponse(String resp) {
                        Toast.makeText(EditAddressActivity.this, R.string.add_success,
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onBtwError(BtwRespError<String> error) {
                        Toast.makeText(EditAddressActivity.this, error.errorMessage,
                                Toast.LENGTH_SHORT).show();
                    }


                    @Override
                    public void onRefreToken() {

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(EditAddressActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }
                });
        mReques.excute();
    }
    void edit(int arid){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(EditAddressActivity.this, Constants.USERINFO, Constants.UID));

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("name", URLEncoder.encode(name));
        parameters.put("phone",number);
        parameters.put("province",URLEncoder.encode(mCurrentProviceName));
        parameters.put("city",URLEncoder.encode(mCurrentCityName));
        parameters.put("area",URLEncoder.encode(mCurrentDistrictName));
        parameters.put("address",URLEncoder.encode(address));

        BtwVolley mReques = withBtwVolley().load(String.format(getString(R.string.url),API.API_EDIT_ADDRESS,arid))
                .method(Request.Method.POST)
                .setHeader("Authorization", token)
                .setHeader("Accept", API.VERSION)
                .setParam("name",name)
                .setParam("phone",number)
                .setParam("province",mCurrentProviceName)
                .setParam("city",mCurrentCityName)
                .setParam("area",mCurrentDistrictName)
                .setParam("address",address)
                .setParam("paramSign",StringUtil.createParameterSign(parameters))
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<String>() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onResponse(String resp) {
                        Toast.makeText(EditAddressActivity.this, R.string.add_success,
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onBtwError(BtwRespError<String> error) {
                        Toast.makeText(EditAddressActivity.this, error.errorMessage,
                                Toast.LENGTH_SHORT).show();
                    }


                    @Override
                    public void onRefreToken() {

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(EditAddressActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }
                });
        mReques.excute();
    }
}
