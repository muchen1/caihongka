package com.rainbowcard.client.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.widget.LoadingFrameLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017-1-25.
 */
public class AgreementActivity extends MyBaseActivity{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.v_container)
    LoadingFrameLayout mLoadingFrameLayout;
    @InjectView(R.id.webview)
    WebView webView;

    int agreementType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(AgreementActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(AgreementActivity.this,true);
        agreementType = getIntent().getIntExtra(Constants.KEY_AGREEMENT_TYPE,0);
        initView();
    }

    void initView(){
        mLoadingFrameLayout.showLoading();
        mHeadControlPanel.initHeadPanel();
//        mHeadControlPanel.setMiddleTitle(getString(R.string.my_agreement));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mLoadingFrameLayout.showLoading();

        WebSettings webSetting = webView.getSettings();
        webSetting.setPluginState(WebSettings.PluginState.ON);
        webSetting.setAllowFileAccess(true);
        webSetting.setDefaultTextEncodingName("UTF-8");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(false);// 设置显示缩放按钮
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);//让webview读取网页设置的viewport，pc版网页
        webView.getSettings().setLoadWithOverviewMode(true);
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
                view.loadUrl(url);
                return true;
            }
        });
        if(agreementType == 0){
            webView.loadUrl("file:///android_asset/asset/agreement.html");
        }else {
            webView.loadUrl("file:///android_asset/asset/recharge_agreement.html");
        }
        WebChromeClient wvcc = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if(!TextUtils.isEmpty(title)) {
                    mHeadControlPanel.setMiddleTitle(title);
                }
            }
        };
        webView.setWebChromeClient(wvcc);
    }
}
