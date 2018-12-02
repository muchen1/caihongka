package com.rainbowcard.client.ui;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
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
import com.rainbowcard.client.ui.adapter.InsuranceChoiceAdapter;
import com.rainbowcard.client.utils.MD5Util;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.utils.Util;
import com.rainbowcard.client.widget.HeadControlPanel;

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
public class CommonInsuranceActivity extends MyBaseActivity implements View.OnClickListener {

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
    Spinner mCityNumSpinner;

    // spinner的数据
    private List mCityNumSpinnerData;


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
    }

    private void initData() {
        mCityNumSpinnerData = new ArrayList();
        mCityNumSpinnerData.add("京");
        mCityNumSpinnerData.add("津");
        mCityNumSpinnerData.add("沪");
        mCityNumSpinnerData.add("渝");
        mCityNumSpinnerData.add("蒙");
        mCityNumSpinnerData.add("新");
        mCityNumSpinnerData.add("藏");
        mCityNumSpinnerData.add("宁");
        mCityNumSpinnerData.add("桂");
        mCityNumSpinnerData.add("黑");
        mCityNumSpinnerData.add("吉");
        mCityNumSpinnerData.add("辽");
        mCityNumSpinnerData.add("晋");
        mCityNumSpinnerData.add("冀");
        mCityNumSpinnerData.add("青");
        mCityNumSpinnerData.add("鲁");
        mCityNumSpinnerData.add("豫");
        mCityNumSpinnerData.add("苏");
        mCityNumSpinnerData.add("皖");
        mCityNumSpinnerData.add("浙");
        mCityNumSpinnerData.add("闽");
        mCityNumSpinnerData.add("赣");
        mCityNumSpinnerData.add("湘");
        mCityNumSpinnerData.add("鄂");
        mCityNumSpinnerData.add("粤");
        mCityNumSpinnerData.add("琼");
        mCityNumSpinnerData.add("甘");
        mCityNumSpinnerData.add("贵");
        mCityNumSpinnerData.add("云");
        mCityNumSpinnerData.add("川");

//
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                R.layout.simple_item_spinner, mCityNumSpinnerData);
//
        spinnerAdapter.setDropDownViewResource(R.layout.simple_item_spinner);
//
//        //绑定 Adapter到控件
        mCityNumSpinner.setAdapter(spinnerAdapter);
        mCityNumSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(CommonInsuranceActivity.this, "nihao--" + position, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
            default:
                break;
        }
    }

    String url = "http://118.24.202.95:5000/bx/api/getCarInfo";
    private void searchUserinfoByCarnum(String carnum) {
        String token = String.format(getString(R.string.token),
                MyConfig.getSharePreStr(CommonInsuranceActivity.this,
                        Constants.USERINFO, Constants.UID));

        String province = ((TextView) (mCityNumSpinner.getSelectedView())).getText().toString().trim();
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
                        getUIUtils().loading(R.id.tv_msg);
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
                            // 车主姓名
                            String personName = personData.getString("InsuredName");
                            // 车牌号
                            String carNum = personData.getString("LicenseNo");
                            // 车类型
                            String carType = personData.getString("ModleName");
                            // 强制险到期时间
                            String forceExpireDate = personData.getString("ForceExpireDate");
                            // 强制险开始日期
                            String nextForceStartDate = personData.getString("NextForceStartDate");
                            // 商业险到期日期
                            String businessExpireDate = personData.getString("BusinessExpireDate");
                            // 商业险开始日期
                            String nextBusinessStartDate = personData.getString("NextBusinessStartDate");


                            // 报价中的可以显示的保险公司信息
                            JSONArray insuranceCompanyList = alldata.getJSONArray("ComList");
                            List<InsurancePriceModel.PriceEntity> priceList = new ArrayList<>();
                            for (int i = 0; i < insuranceCompanyList.length(); i++) {
                                JSONObject company = insuranceCompanyList.getJSONObject(i);
                                InsurancePriceModel.PriceEntity priceEntity = new InsurancePriceModel.PriceEntity();
                                priceEntity.companyId = company.getInt("comCode");
                                priceEntity.companyName = company.getString("name");
                                priceEntity.iconurl = company.getString("icon");
                                priceEntity.showResult = false;
                                priceList.add(priceEntity);
                            }
                            insurancePriceModel.listData = priceList;
                            insurancePriceModel.carNum = carNum;
                            insurancePriceModel.carType = carType;
                            insurancePriceModel.carJqxDate = nextForceStartDate;
                            insurancePriceModel.carSyxDate = nextBusinessStartDate;
                            insurancePriceModel.carPerson = personName;
                            insurancePriceModel.status = 0;

                            // 险种选择model
                            insuranceChoiceModel.status = 0;
                            insuranceChoiceModel.data = new ArrayList<>();
                            JSONArray syxArray = alldata.getJSONArray("NextBXinfo");
                            for (int j = 0; j < syxArray.length(); j++) {
                                JSONObject insuranceObject = syxArray.getJSONObject(j);
                                InsuranceChoiceModel.ChildItemEntity childItemEntity = new InsuranceChoiceModel.ChildItemEntity();
                                childItemEntity.style = InsuranceChoiceAdapter.ITEM_TYPE_CHILD;
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
                                    childItemEntity.insuranceAllPrice.append(options.getInt(optionNames.getString(k)), optionNames.getString(k));
                                }

                                childItemEntity.insuranceKey = insuranceObject.getString("key");
                                insuranceChoiceModel.data.add(childItemEntity);
                            }

                            InsuranceModelServerProxy.getInstance().setModel(carNum,
                                    insuranceChoiceModel, insurancePriceModel);


                            // 解析成功之后跳界面
                            Intent intent = new Intent(CommonInsuranceActivity.this, InsuranceChoiceActivity.class);
                            intent.putExtra("carNum", carNum);
                            startActivity(intent);
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
}
