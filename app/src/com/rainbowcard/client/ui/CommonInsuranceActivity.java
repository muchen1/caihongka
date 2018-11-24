package com.rainbowcard.client.ui;

import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.rainbowcard.client.utils.MD5Util;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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

    String url = "http://t.yangyanxing.com/bx/api/getCarInfo";
    private void searchUserinfoByCarnum(String carnum) {
        String token = String.format(getString(R.string.token),
                MyConfig.getSharePreStr(CommonInsuranceActivity.this,
                        Constants.USERINFO, Constants.UID));

        String province = ((TextView) (mCityNumSpinner.getSelectedView())).getText().toString();
        String carNumString = province + carnum.toUpperCase();
        StringBuilder stringBuilder = new StringBuilder("LicenseNo=");
        stringBuilder.append(carNumString).append("&").append("shopId=").append("1").append("&")
                .append("secKey=yangwangdai");
        String sceCode = MD5Util.stringMD5(stringBuilder.toString());
        withBtwVolley().load(url)
                .method(Request.Method.POST)
                .setParam("LicenseNo", URLEncoder.encode(carNumString))
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

                    }

                    @Override
                    public void onBtwError(BtwRespError<String> error) {

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {

                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute();

    }
}
