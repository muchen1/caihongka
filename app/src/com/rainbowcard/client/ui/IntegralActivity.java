package com.rainbowcard.client.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.PhoneSignUtil;
import com.rainbowcard.client.utils.StringUtil;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.widget.LoadingFrameLayout;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017/1/20.
 */
public class IntegralActivity extends MyBaseActivity {

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.iv_back)
    ImageView ivBack;
    @InjectView(R.id.right_layout)
    LinearLayout rightLayout;
    @InjectView(R.id.right_title)
    TextView rightTv;
    @InjectView(R.id.v_container)
    LoadingFrameLayout mLoadingFrameLayout;
    @InjectView(R.id.webview)
    WebView webView;

    private String url;
    private static final String APP_CACAHE_DIRNAME = "/webcache";
    private static final String TAG = IntegralActivity.class.getSimpleName();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(IntegralActivity.this, true);
        UIUtils.setMeizuStatusBarDarkIcon(IntegralActivity.this, true);
        url = getIntent().getStringExtra(Constants.KEY_URL);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        url = getIntent().getStringExtra(Constants.KEY_URL);
        initView();
    }

    public void initView() {
        mLoadingFrameLayout.showLoading();
        mHeadControlPanel.initHeadPanel();
//        mHeadControlPanel.setMiddleTitle(getString(R.string.new_info));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.app_white));
//        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        mHeadControlPanel.setMiddleTitle("");
        backBtn.setVisibility(View.VISIBLE);
        ivBack.setImageResource(R.drawable.nav_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
            }
        });
        rightLayout.setVisibility(View.VISIBLE);
        rightTv.setVisibility(View.VISIBLE);
        rightTv.setText(getString(R.string.integral_rele));
        rightTv.setTextColor(getResources().getColor(R.color.app_white));
        rightTv.setTextSize(12);
        rightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl("file:///android_asset/asset/rule.html");
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
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            webView.setWebViewClient(new WebViewClient() {

                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {                 // Handle the error

                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    mLoadingFrameLayout.showLoadingView(false);
                }

                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.startsWith("http:") || url.startsWith("https:")) {
                        return false;
                    } else if (url.startsWith("tel:")) {
                        Intent tel = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                        startActivity(tel);
                        return true;
                    } else if (url.startsWith("mailto:")) {
                        startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(url)));
                        return true;
                    } else {
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
                    if (!TextUtils.isEmpty(title)) {
                        if (!title.contains("te")) {
                            mHeadControlPanel.setMiddleTitle(title);
                            if (title.equals("积分规则")) {
                                rightLayout.setVisibility(View.GONE);
                            } else {
                                rightLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
                /*@Override
                public boolean onJsAlert(WebView view, String url, final String message, JsResult result) {
                    Log.d("main", "onJsAlert:" + message);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            new AlertDialog.Builder(IntegralActivity.this)
                                    .setTitle("提示")
                                    .setMessage(message)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setNegativeButton("取消",null)
                                    .show();

                        }
                    });
                    result.confirm();//这里必须调用，否则页面会阻塞造成假死
                    return true;
                }*/
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
        clearWebViewCache();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public class jsObj {
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
        public String mutualMethod(String key, String v) {
            if (key.equals("goBack")) {
                finish();
            } else if (key.equals("setEncryptInfo")) {
                long timestamp = System.currentTimeMillis() / 1000;
                String phoneSign = URLEncoder.encode(PhoneSignUtil.getDeviceId(IntegralActivity.this));
                SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
                parameters.put("timestamp", timestamp);
                parameters.put("clientId", phoneSign);
                JSONObject jsonObj = new JSONObject();
                try {
                    jsonObj.put("signature", StringUtil.createSign(parameters));
                    jsonObj.put("clientId", PhoneSignUtil.getDeviceId(IntegralActivity.this));
                    jsonObj.put("timestamp",timestamp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonObj.toString();
            } else {
                return MyConfig.getSharePreStr(IntegralActivity.this, Constants.USERINFO, Constants.UID);
            }
            return "";
        }
    }

    /**
     * 清除WebView缓存
     */
    public void clearWebViewCache() {

        //清理Webview缓存数据库
        try {
            deleteDatabase("webview.db");
            deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //WebView 缓存文件
        File appCacheDir = new File(getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME);
        Log.e(TAG, "appCacheDir path=" + appCacheDir.getAbsolutePath());

        File webviewCacheDir = new File(getCacheDir().getAbsolutePath() + "/webviewCache");
        Log.e(TAG, "webviewCacheDir path=" + webviewCacheDir.getAbsolutePath());

        //删除webview 缓存目录
        if (webviewCacheDir.exists()) {
            deleteFile(webviewCacheDir);
        }
        //删除webview 缓存 缓存目录
        if (appCacheDir.exists()) {
            deleteFile(appCacheDir);
        }
    }

    /**
     * 递归删除 文件/文件夹
     *
     * @param file
     */
    public void deleteFile(File file) {

        Log.i(TAG, "delete file path=" + file.getAbsolutePath());

        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        } else {
            Log.e(TAG, "delete file no exists " + file.getAbsolutePath());
        }
    }


}
