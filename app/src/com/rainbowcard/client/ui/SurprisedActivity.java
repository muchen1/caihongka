package com.rainbowcard.client.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rainbowcard.client.R;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.widget.LoadingFrameLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017/1/20.
 */
public class SurprisedActivity extends MyBaseActivity {

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.v_container)
    LoadingFrameLayout mLoadingFrameLayout;
    @InjectView(R.id.webview)
    WebView webView;

    private String url;
    private String province;
    private String city;
    private String cityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(SurprisedActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(SurprisedActivity.this,true);
        url = getIntent().getStringExtra(Constants.KEY_URL);
        province = getIntent().getStringExtra(Constants.KEY_PROVINCE);
        city = getIntent().getStringExtra(Constants.KEY_CITY);
        cityId = getIntent().getStringExtra(Constants.KEY_CITY_ID);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),"没有权限,请手动开启定位权限",Toast.LENGTH_SHORT).show();
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(SurprisedActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        url = getIntent().getStringExtra(Constants.KEY_URL);
        initView();
    }

    public void initView(){
        mLoadingFrameLayout.showLoading();
        mHeadControlPanel.initHeadPanel();
//        mHeadControlPanel.setMiddleTitle(getString(R.string.new_info));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        mHeadControlPanel.setMiddleTitle("");
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(webView.canGoBack()){
//                    webView.goBack();
//                }else {
                    finish();
//                }
            }
        });
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(this, "地址异常,加载失败",
                    Toast.LENGTH_LONG).show();
        } else {
            WebSettings webSetting = webView.getSettings();
            webSetting.setPluginState(WebSettings.PluginState.ON);
            webSetting.setAllowFileAccess(true);
            webSetting.setDefaultTextEncodingName("GBK");
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.getSettings().setSupportZoom(true);
            webView.getSettings().setDisplayZoomControls(false);// 设置显示缩放按钮
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setUseWideViewPort(true);//让webview读取网页设置的viewport，pc版网页

            //启用数据库
            webView.getSettings().setDatabaseEnabled(true);
            //设置定位的数据库路径
            String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
            webView.getSettings().setGeolocationDatabasePath(dir);

            webView.getSettings().setGeolocationEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);

            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            webView.setWebViewClient(new WebViewClient() {

                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
                {                 // Handle the error

                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    mLoadingFrameLayout.showLoadingView(false);
                }

                public boolean shouldOverrideUrlLoading(WebView view, String url)
                {
                    if (url.startsWith("http:") || url.startsWith("https:")) {
                        return false;
                    }else if (url.startsWith("tel:")) {
                        Intent tel = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                        startActivity(tel);
                        return true;
                    }else if (url.startsWith("mailto:")) {
                        startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(url)));
                        return true;
                    }else {
                        //请务必使用try、catch 因为该处返回的url可能为无效url或者手机没有安转支付宝导致webview闪退
                        try {
                            //通过意图调起支付宝
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);
                        } catch (Exception e) {
                        }
                        return true;
                    }
                }
            });
            webView.addJavascriptInterface(new jsObj(), "jsObj");
            webView.loadUrl(url);
            WebChromeClient wvcc = new WebChromeClient() {
                @Override
                public void onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);
                    if(!TextUtils.isEmpty(title)) {
                        mHeadControlPanel.setMiddleTitle(title);
                    }
                }

                @Override
                public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                    super.onGeolocationPermissionsShowPrompt(origin, callback);
                    callback.invoke(origin, true, false);
                }
            };
            webView.setWebChromeClient(wvcc);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.reload();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public class jsObj{
//        @JavascriptInterface
//        public void HtmlcallJava(){
//            finish();
//        }
//        @JavascriptInterface
//        public String HtmlcallJava(String key){
//            Log.d("GCCCCCCCCCkele", MyConfig.getSharePreStr(SurprisedActivity.this, Constants.USERINFO, Constants.UID));
//            return MyConfig.getSharePreStr(SurprisedActivity.this, Constants.USERINFO, Constants.UID);
//        }
        @JavascriptInterface
        public String mutualMethod(String key,String v){
            Log.d("GCCCCCCCCCkele!!!!!!!!!", MyConfig.getSharePreStr(SurprisedActivity.this, Constants.USERINFO, Constants.UID)+"??"+key+"???"+v);
            if(key.equals("getCity")){
                Log.d("GCCCCCKELE???????????",String.format(getString(R.string.city_json),cityId));
                return String.format(getString(R.string.city_json),cityId);
            }else if(key.equals("goBack")){
                finish();
            }else {
                return MyConfig.getSharePreStr(SurprisedActivity.this, Constants.USERINFO, Constants.UID);
            }
            return "";
        }
    }
}
