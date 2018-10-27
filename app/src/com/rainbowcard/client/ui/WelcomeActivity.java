package com.rainbowcard.client.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.baidu.mapapi.SDKInitializer;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.base.MyBasicActivity;
import com.rainbowcard.client.base.YHApplication;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.HotCity;
import com.rainbowcard.client.utils.UIUtils;

public class WelcomeActivity extends MyBasicActivity {

    private static final String LTAG = WelcomeActivity.class.getSimpleName();
    private boolean mAppIsFirstStart;
    private SharedPreferences mSharedPreferences;
    public class SDKReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();

            /*if(s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)){
                UIUtils.toast("key 验证出错！错误码："+ intent.getIntExtra
                        (SDKInitializer.SDK_BROADTCAST_INTENT_EXTRA_INFO_KEY_ERROR_CODE,0)
                        + ":请在 AndroidManifest.xml 文件中检查 key 设置");
            }else if(s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)){
                UIUtils.toast("key 验证成功！功能可以正常使用");
            }else if(s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)){
                UIUtils.toast("网络出错！");
            }*/
        }
    }
    private SDKReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        mSharedPreferences = getSharedPreferences(Constants.SHARE_APP_TAG, 0);
        mAppIsFirstStart = mSharedPreferences.getBoolean(Constants.ISFIRSTOPENAPP, true);

        IntentFilter filter = new IntentFilter();
        filter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        filter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        filter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);

        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver,filter);
        loadHotCity();

    }

    void loadHotCity(){
            withBtwVolley().load(API.API_GET_CITY_LIST)
                    .setHeader("Accept", API.VERSION)
                    .method(Request.Method.GET)
                    .setRetrys(0)
                    .setUIComponent(this)
                    .setTimeout(20000)
                    .setResponseHandler(new BtwVolley.ResponseHandler<HotCity>() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onFinish() {

                        }

                        @Override
                        public void onResponse(HotCity resp) {
                            if (mAppIsFirstStart) {
                                mSharedPreferences.edit().putBoolean(Constants.ISFIRSTOPENAPP, false).commit();
                                Intent intent = new Intent(WelcomeActivity.this, GuideActivity.class);
                                intent.putExtra(Constants.KEY_CITYS,resp.data);
                                startActivity(intent);
                                finish();
                            }else {
                                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                                if (getIntent().getBundleExtra(Constants.EXTRA_BUNDLE) != null) {
                                    intent.putExtra(Constants.EXTRA_BUNDLE,
                                            getIntent().getBundleExtra(Constants.EXTRA_BUNDLE));
                                }
                                intent.putExtra(Constants.KEY_CITYS,resp.data);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onBtwError(BtwRespError<HotCity> error) {
                            UIUtils.toast(error.errorMessage);
                            if (mAppIsFirstStart) {
                                mSharedPreferences.edit().putBoolean(Constants.ISFIRSTOPENAPP, false).commit();
                                Intent intent = new Intent(WelcomeActivity.this, GuideActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                                if (getIntent().getBundleExtra(Constants.EXTRA_BUNDLE) != null) {
                                    intent.putExtra(Constants.EXTRA_BUNDLE,
                                            getIntent().getBundleExtra(Constants.EXTRA_BUNDLE));
                                }
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onNetworkError(VolleyUtils.NetworkError error) {
                            UIUtils.toast(getString(R.string.network_error));
                            if (mAppIsFirstStart) {
                                mSharedPreferences.edit().putBoolean(Constants.ISFIRSTOPENAPP, false).commit();
                                Intent intent = new Intent(WelcomeActivity.this, GuideActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                                if (getIntent().getBundleExtra(Constants.EXTRA_BUNDLE) != null) {
                                    intent.putExtra(Constants.EXTRA_BUNDLE,
                                            getIntent().getBundleExtra(Constants.EXTRA_BUNDLE));
                                }
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onRefreToken() {

                        }
                    }).excute(HotCity.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    public void goToOthers(Class<?> cls){
        Intent intent = new Intent(WelcomeActivity.this,cls);
        startActivity(intent);
        finish();
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }
}
