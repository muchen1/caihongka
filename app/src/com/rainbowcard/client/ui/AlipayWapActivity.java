package com.rainbowcard.client.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rainbowcard.client.R;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.common.utils.DLog;


/**
 * Created by gc on 14-8-1.
 */
public class AlipayWapActivity extends MyBaseActivity {

    public final static String TAG = AlipayWapActivity.class.getName();

    public final static int RESULT_ALIPAY_CANCEL = 100;


    private WebView mWebView;
    private String mUrl;
    private Boolean mIsPaid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_PROGRESS);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.act_alipay_wap);

        mUrl = getIntent().getStringExtra(Constants.KEY_ALIPAY_URL);
        if (TextUtils.isEmpty(mUrl)) {
            setResult(Activity.RESULT_CANCELED);
            this.finish();
        }

        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        mWebView = (WebView) findViewById(R.id.wv_alipay);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);


        mWebView.setWebViewClient(new WebViewClient() {


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                DLog.i("onPageFinished " + url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                getSupportActionBar().setTitle(mWebView.getTitle());
                DLog.i("onPageStarted " + url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                DLog.i("shouldOverrideUrlLoading " + url);
                if (url.startsWith("http://m.betterwood.com")) {
                    setResult(Activity.RESULT_OK);
                    mIsPaid = true;
                    finishWithResult();
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                newProgress = (Window.PROGRESS_END - Window.PROGRESS_START) / 100 * newProgress;
                setProgress(newProgress);
            }
        });

        mWebView.loadUrl(mUrl);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Menu.FIRST:
            case android.R.id.home:
                finishWithResult();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishWithResult();
    }


    private void finishWithResult() {
        if (mIsPaid) {
            setResult(Activity.RESULT_OK);
        } else {
            setResult(Activity.RESULT_CANCELED);
        }
        finish();
    }
}
