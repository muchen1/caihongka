package com.rainbowcard.client.ui;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.InsuranceChoiceModel;
import com.rainbowcard.client.model.InsuranceModelServerProxy;
import com.rainbowcard.client.model.InsurancePriceModel;
import com.rainbowcard.client.model.UserInfoEntity;
import com.rainbowcard.client.ui.adapter.InsuranceChoiceAdapter;
import com.rainbowcard.client.utils.GsonUtil;
import com.rainbowcard.client.utils.MD5Util;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.utils.Util;
import com.rainbowcard.client.widget.CommonInsuranceConfirmDialog;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.widget.TextChoiceDialog;
import com.rainbowcard.client.widget.adlibrary.AdConstant;
import com.rainbowcard.client.widget.adlibrary.AnimDialogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 普通车险 页面
 */
public class CommonInsuranceActivity extends MyBaseActivity implements View.OnClickListener,
        CommonInsuranceConfirmDialog.onConfirmClickListener, TextChoiceDialog.OnTextChoiceListener {

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.midle_title)
    TextView mHeaderTitle;
    @InjectView(R.id.act_common_insurance_text_footer)
    TextView mFooterTextview;
    @InjectView(R.id.common_insurance_carnum_edittext)
    EditText mCarnumEdittext;
    @InjectView(R.id.ci_text_protocol)
    TextView mProtocolTextview;
    @InjectView(R.id.ci_bt_next)
    TextView mNextButton;
    @InjectView(R.id.ac_spiner_car_city)
    TextView mCityNumSpinnerText;

    @InjectView(R.id.ac_spiner_car_city_container)
    LinearLayout mCityNumSpinnerTextContainer;
    @InjectView(R.id.insurance_common_insuracedcity_container)
    LinearLayout mInsuracedCityContainer;
    @InjectView(R.id.insurance_common_insuracedcity)
    TextView mInsurancedCity;

    // spinner的数据
    private String[] mProviceStringList;

    private SparseArray<String> mCityInsuranceList;

    private String[] mCityInsuranceArray;

    private String mCarNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_common_insurance);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(CommonInsuranceActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(CommonInsuranceActivity.this,true);
        initView();
        initData();
    }

    private void initView() {
        mHeaderTitle.setVisibility(View.VISIBLE);
        mHeaderTitle.setText(R.string.insurance_common_text);
        mHeaderTitle.setTextColor(getResources().getColor(R.color.vpi__background_holo_dark));
        mHeadControlPanel.setMyBackgroundColor(getResources().getColor(R.color.white));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        mFooterTextview.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mProtocolTextview.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mNextButton.setOnClickListener(this);
        mCityNumSpinnerTextContainer.setOnClickListener(this);
        mInsuracedCityContainer.setOnClickListener(this);
    }

    private void initData() {
        getCityListFromServer();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.nav_back:
                finish();
                break;
            case R.id.ci_bt_next:
                // 发送网络请求，进行跳转
                String carNum = mCarnumEdittext.getText().toString();
                if (TextUtils.isEmpty(carNum)) {
                    UIUtils.toast("请先输入车牌号");
                    return;
                }
                searchUserinfoByCarnum(carNum);
                break;
            case R.id.ac_spiner_car_city_container:
                // 点击选择车牌号的省份
                new TextChoiceDialog(CommonInsuranceActivity.this,
                        new TextChoiceDialog.OnTextChoiceListener() {
                            @Override
                            public void onTextChoice(String text) {
                                mCityNumSpinnerText.setText(text);
                            }
                        }, mProviceStringList).show();
                break;
            case R.id.insurance_common_insuracedcity_container:
                // 点击保险地点的区域
                new TextChoiceDialog(CommonInsuranceActivity.this,
                        CommonInsuranceActivity.this, mCityInsuranceArray).show();
                break;
            default:
                break;
        }
    }

    String url = "http://118.24.202.95:5000/bx/api/getCarInfo";
    String getCityUrl = "http://118.24.202.95:5000/bx/api/getCityList";
    String getProvinceUrl = "http://118.24.202.95:5000/bx/api/getProvince";

    private void searchUserinfoByCarnum(String carnum) {
        String token = String.format(getString(R.string.token),
                MyConfig.getSharePreStr(CommonInsuranceActivity.this,
                        Constants.USERINFO, Constants.UID));

        String province = mCityNumSpinnerText.getText().toString().trim();
        String carNumString = province + carnum.toUpperCase();
        StringBuilder stringBuilder = new StringBuilder("LicenseNo=");
        stringBuilder.append(carNumString).append("&").append("shopId=").append("1").append("&")
                .append("secKey=yangwangdai");
        Log.e("daipeng", "secCode===" + stringBuilder.toString());
        String sceCode = MD5Util.stringMD5(stringBuilder.toString());
        Log.e("daipeng", "secCode===" + sceCode);

        withBtwVolley().load(url)
                .method(Request.Method.POST)
                .setParam("LicenseNo", carNumString)
                .setParam("shopId", "1")
                .setParam("secCode", sceCode)
                .setHeader("Authorization", token)
                .setHeader("Accept", API.VERSION)
                .setUIComponent(this)
                .setRetrys(0)
                .setResponseHandler(new BtwVolley.ResponseHandler<String>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading(false);
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(String resp) {

                        // 请求完成之后开始解析
                        InsuranceChoiceModel insuranceChoiceModel = new InsuranceChoiceModel();
                        InsurancePriceModel insurancePriceModel = new InsurancePriceModel();


                        try {
                            JSONObject alldata = new JSONObject(resp);
                            // 用户信息
                            JSONObject personData = alldata.getJSONObject("data").getJSONObject("UserInfo");
                            insurancePriceModel.userInfoEntity = (InsurancePriceModel.UserInfoEntity)
                                    GsonUtil.fromJson(personData.toString(), InsurancePriceModel.UserInfoEntity.class);
                            // 车牌号
                            mCarNum = personData.getString("LicenseNo");

                            // 报价中的可以显示的保险公司信息
                            JSONArray insuranceCompanyList = alldata.getJSONArray("ComList");
                            insurancePriceModel.listData = new ArrayList<>();
                            for (int i = 0; i < insuranceCompanyList.length(); i++) {
                                JSONObject company = insuranceCompanyList.getJSONObject(i);
                                InsurancePriceModel.PriceEntity priceEntity = new InsurancePriceModel.PriceEntity();
                                priceEntity.companyId = company.getInt("comCode");
                                priceEntity.companyName = company.getString("name");
                                priceEntity.iconurl = company.getString("icon");
                                priceEntity.showResult = false;
                                insurancePriceModel.listData.add(priceEntity);
                            }

                            Log.e("daipeng", "insurancePriceModel.listData==" + insurancePriceModel.listData.size());
                            insurancePriceModel.status = 0;

                            // 险种选择model
                            insuranceChoiceModel.status = 0;
                            insuranceChoiceModel.data = new ArrayList<>();
                            JSONArray syxArray = alldata.getJSONArray("NextBXinfo");
                            for (int j = 0; j < syxArray.length(); j++) {
                                JSONObject insuranceObject = syxArray.getJSONObject(j);
                                InsuranceChoiceModel.ChildItemEntity childItemEntity = new InsuranceChoiceModel.ChildItemEntity();
                                childItemEntity.insuranceName = insuranceObject.getString("name");
                                childItemEntity.insuranceStatusTextKey = insuranceObject.getInt("default");
                                childItemEntity.insuranceBjmpStatus = insuranceObject.getJSONObject("BuJiMian")
                                        .getInt("value");
                                childItemEntity.insuranceBjmpKey = insuranceObject.getJSONObject("BuJiMian").getString("name");

                                if (childItemEntity.insuranceBjmpStatus == 1 || childItemEntity.insuranceStatusTextKey != 0) {
                                    childItemEntity.insuranceStatus = true;
                                }
                                childItemEntity.insuranceAllPrice = new SparseArray<>();
                                JSONObject options = insuranceObject.getJSONObject("option");
                                JSONArray optionNames = options.names();
                                for (int k = 0; k < optionNames.length(); k ++) {
                                    Log.e("daipeng", "insuranceName==" + childItemEntity.insuranceName);
                                    Log.e("daipeng", "k===" + k);
                                    Log.e("daipeng", "optionNames==" + optionNames.getString(k));
                                    Log.e("daipeng", "optionvalue==" + options.getInt(optionNames.getString(k)));
                                    childItemEntity.insuranceAllPrice.append(options.getInt(optionNames.getString(k)), optionNames.getString(k));
                                }

                                childItemEntity.insuranceKey = insuranceObject.getString("key");
                                insuranceChoiceModel.data.add(childItemEntity);
                            }

                            InsuranceModelServerProxy.getInstance().setModel(mCarNum,
                                    insuranceChoiceModel, insurancePriceModel);

                            // 解析成功之后出确认弹窗
                            new CommonInsuranceConfirmDialog(CommonInsuranceActivity.this, android.R.style.Theme_Material_Light_Dialog_NoActionBar)
                                    .initData(mCarNum)
                                    .setOnConfirmClickListener(CommonInsuranceActivity.this)
                                    .show();

                        } catch (Exception e) {
                            // donothing
                            Log.e("daipeng", "发生异常了==" + e.getMessage());
                            Toast.makeText(CommonInsuranceActivity.this, "服务异常，请稍后重试", Toast.LENGTH_LONG);
                        }

                    }

                    @Override
                    public void onBtwError(BtwRespError<String> error) {
                        Log.e("daipeng", "onBtwError==" + error.errorMessage);
                        Log.e("daipeng", "onBtwError==" + error.errorCode);
                        Log.e("daipeng", "onBtwError==" + error.result);
                        Toast.makeText(CommonInsuranceActivity.this, "服务异常，请稍后重试", Toast.LENGTH_LONG);

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Log.e("daipeng", "onNetworkError==" + error.message);
                        Toast.makeText(CommonInsuranceActivity.this, "请检查网络配置", Toast.LENGTH_LONG);


                    }

                    @Override
                    public void onRefreToken() {
                        Log.e("daipeng", "refershtoken==");
                        // token失效，重新登录
                        refreshToken();
                    }
                }).excute();

    }


    /**
     * 获取保险城市的请求
     */
    private void getCityListFromServer() {
        String token = String.format(getString(R.string.token),
                MyConfig.getSharePreStr(CommonInsuranceActivity.this,
                        Constants.USERINFO, Constants.UID));

        withBtwVolley().load(getCityUrl)
                .method(Request.Method.GET)
                .setHeader("Authorization", token)
                .setHeader("Accept", API.VERSION)
                .setUIComponent(this)
                .setRetrys(0)
                .setResponseHandler(new BtwVolley.ResponseHandler<String>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading(false);
                    }

                    @Override
                    public void onFinish() {
                        // 开始请求省份列表，由于请求时间很短，就不进行并行请求了，而采取串行的形式
                        getProvinceListFromServer();
                    }

                    @Override
                    public void onResponse(String resp) {
                        Log.e("daipeng", "onBtwError==" + resp);
                        // 解析列表

                        try {

                            JSONObject jsonObject = new JSONObject(resp);
                            JSONObject listObject = jsonObject.optJSONObject("data");
                            if (listObject == null) {
                                return;
                            }
                            JSONArray names = listObject.names();
                            mCityInsuranceArray = new String[names.length()];
                            mCityInsuranceList = new SparseArray<>();
                            for (int i = 0; i < names.length(); i++) {
                                mCityInsuranceList.append(listObject.getInt(names.getString(i)), names.getString(i));
                                mCityInsuranceArray[i] = names.getString(i);
                            }

                        } catch (Exception e) {
                            Log.e("daipeng", "e=" + e.getMessage());
                        }

                    }

                    @Override
                    public void onBtwError(BtwRespError<String> error) {
                        Log.e("daipeng", "onBtwError==" + error.errorMessage);
                        Log.e("daipeng", "onBtwError==" + error.errorCode);
                        Log.e("daipeng", "onBtwError==" + error.result);
                        Toast.makeText(CommonInsuranceActivity.this, "服务异常，请稍后重试", Toast.LENGTH_SHORT);
                        finish();
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Log.e("daipeng", "onNetworkError==" + error.message);
                        Toast.makeText(CommonInsuranceActivity.this, "请检查网络配置", Toast.LENGTH_SHORT);
                        finish();


                    }

                    @Override
                    public void onRefreToken() {
                        Log.e("daipeng", "refershtoken==");
                        // token失效，重新登录
                        refreshToken();
                    }
                }).excute();
    }


    /**
     * 获取车牌号省简称城市的请求
     */
    private void getProvinceListFromServer() {
        String token = String.format(getString(R.string.token),
                MyConfig.getSharePreStr(CommonInsuranceActivity.this,
                        Constants.USERINFO, Constants.UID));

        withBtwVolley().load(getProvinceUrl)
                .method(Request.Method.GET)
                .setHeader("Authorization", token)
                .setHeader("Accept", API.VERSION)
                .setUIComponent(this)
                .setRetrys(0)
                .setResponseHandler(new BtwVolley.ResponseHandler<String>() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(String resp) {
                        Log.e("daipeng", "onBtwError==" + resp);
                        // 解析省份简称列表
                        try {
                            JSONObject jsonObject = new JSONObject(resp);
                            JSONArray provinceArray = jsonObject.getJSONObject("data")
                                    .getJSONArray("Province");
                            mProviceStringList = new String[provinceArray.length()];
                            for (int i = 0; i < provinceArray.length(); i++) {
                                mProviceStringList[i] = provinceArray.getString(i);
                            }

                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onBtwError(BtwRespError<String> error) {
                        Log.e("daipeng", "onBtwError==" + error.errorMessage);
                        Log.e("daipeng", "onBtwError==" + error.errorCode);
                        Log.e("daipeng", "onBtwError==" + error.result);
                        Toast.makeText(CommonInsuranceActivity.this, "服务异常，请稍后重试", Toast.LENGTH_SHORT);
                        finish();
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Log.e("daipeng", "onNetworkError==" + error.message);
                        Toast.makeText(CommonInsuranceActivity.this, "请检查网络配置", Toast.LENGTH_SHORT);
                        finish();


                    }

                    @Override
                    public void onRefreToken() {
                        Log.e("daipeng", "refershtoken==");
                        // token失效，重新登录
                        refreshToken();
                    }
                }).excute();
    }

    @Override
    public void onConfirmBtClick() {
        // 点击确认弹窗跳转到报价页面
        Intent intent = new Intent(CommonInsuranceActivity.this, InsuranceChoiceActivity.class);
        intent.putExtra("carNum", mCarNum);
        startActivity(intent);
    }

    @Override
    public void onTextChoice(String text) {
        mInsurancedCity.setText(text);
    }
}
