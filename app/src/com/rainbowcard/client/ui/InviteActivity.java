package com.rainbowcard.client.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.Request;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.HeadlineModel;
import com.rainbowcard.client.model.InviteModel;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.UIUtils;
import com.umeng.socialize.bean.SHARE_MEDIA;


import java.net.URLDecoder;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2018-3-12.
 */
public class InviteActivity extends MyBaseActivity{

    @InjectView(R.id.nav_back)
    RelativeLayout navBack;
    @InjectView(R.id.detail_layout)
    RelativeLayout detailLayout;
    @InjectView(R.id.invite_count)
    TextView inviteCountTv;
    @InjectView(R.id.ticket_count)
    TextView ticketCountTv;
    @InjectView(R.id.invity_btn)
    Button invityBtn;
    @InjectView(R.id.profileSwitcher)
    TextSwitcher textSwitcher;

    String token;
    String shareTitle;
    private String shareUrl;


    private int index = 0;
    private boolean flag = true;
    private BitHandler bitHandler;
    private ArrayList<HeadlineModel.HeadlineEntity> mHeadlines = new ArrayList<HeadlineModel.HeadlineEntity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(InviteActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(InviteActivity.this,true);
        initView();
        getHeadline();
        getInviteCount();

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    void initView(){
        navBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InviteActivity.this,MainActivity.class);
                intent.putExtra(Constants.KEY_IS_PERSONAL,true);
                startActivity(intent);
                finish();
            }
        });

        detailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InviteActivity.this,InviteDetailActivity.class);
                startActivity(intent);
            }
        });

        invityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    void refreshView(ArrayList<HeadlineModel.HeadlineEntity> headlines){
        if(headlines != null && !headlines.isEmpty()){
            mHeadlines.addAll(headlines);
            if(flag) {
                flag = false;
                test();
            }
        }
    }


    //获得邀请头条
    void getHeadline(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(InviteActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(API.API_GET_INVITE_HEADLINE)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<HeadlineModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(HeadlineModel resp) {
                        if(resp.data != null && !resp.data.isEmpty()) {
                            refreshView(resp.data);
                        }


                    }

                    @Override
                    public void onBtwError(BtwRespError<HeadlineModel> error) {
                        Toast.makeText(InviteActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(InviteActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(HeadlineModel.class);
    }

    //获取受邀请人数、邀请链接
    void getInviteCount() {
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(InviteActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(API.API_GET_INVITE_NUMBER)
                .method(Request.Method.GET)
                .setHeader("Authorization", token)
                .setHeader("Accept", API.VERSION)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<InviteModel>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onResponse(InviteModel resp) {
                        shareUrl = URLDecoder.decode(resp.data.inviteUrl);
//                        inviteCountTv.setText(String.format(getString(R.string.invite_count),resp.data.inviteNumber));
                        SpannableString sp = new SpannableString(String.format(getResources().getString(R.string.invite_count),resp.data.inviteNumber));

                        String memberStateMent = String.format(getResources().getString(R.string.invite_count),resp.data.inviteNumber);

                        //变色
                        sp.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.money_color)), memberStateMent.indexOf( resp.data.inviteNumber),
                                memberStateMent.indexOf( resp.data.inviteNumber) + resp.data.inviteNumber.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        //加粗
                        sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), memberStateMent.indexOf( resp.data.inviteNumber),
                                memberStateMent.indexOf( resp.data.inviteNumber) + resp.data.inviteNumber.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        inviteCountTv.setText(sp);

                        SpannableString sp2 = new SpannableString(String.format(getResources().getString(R.string.tickect_count),resp.data.friendsCoupon));

                        String memberStateMent2 = String.format(getResources().getString(R.string.tickect_count),resp.data.friendsCoupon);

                        //变色
                        sp2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.money_color)), memberStateMent2.indexOf( resp.data.friendsCoupon),
                                memberStateMent2.indexOf( resp.data.friendsCoupon) + resp.data.friendsCoupon.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        //加粗
                        sp2.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), memberStateMent2.indexOf( resp.data.friendsCoupon),
                                memberStateMent2.indexOf( resp.data.friendsCoupon) + resp.data.friendsCoupon.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ticketCountTv.setText(sp2);
//                        setShareContent();
                    }

                    @Override
                    public void onBtwError(BtwRespError<InviteModel> error) {
                        Toast.makeText(InviteActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(InviteActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(InviteModel.class);
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(InviteActivity.this,R.style.loading_dialog);
        dialog.setContentView(R.layout.ui_share_dialog);
        Window dialogWindow = dialog.getWindow();
        Display display = getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = -80;
        lp.width = (int)(display.getWidth());
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.BOTTOM);
        RelativeLayout wechatLayout = (RelativeLayout) dialog.findViewById(R.id.wechat_layout);
        RelativeLayout wechatcircleLayout = (RelativeLayout) dialog.findViewById(R.id.wechatcircle_layout);

        wechatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.shareWeb(InviteActivity.this, shareUrl, "点击领取彩虹卡免费洗车券"
                        , "洗车、保养、美容，给您最周到的汽车养护服务", "", R.drawable.share_icon, SHARE_MEDIA.WEIXIN
                );
                dialog.dismiss();
            }
        });

        wechatcircleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.shareWeb(InviteActivity.this, shareUrl, "我在彩虹卡享受免费洗车，你也试试？"
                        , "洗车、保养、美容，给您最周到的汽车养护服务", "", R.drawable.share_icon, SHARE_MEDIA.WEIXIN_CIRCLE
                );
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }



    class BitHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SpannableString sp = new SpannableString(String.format(getResources().getString(R.string.headline),mHeadlines.get(index).phone,mHeadlines.get(index).coupon));

            String memberStateMent = String.format(getResources().getString(R.string.headline),mHeadlines.get(index).phone,mHeadlines.get(index).coupon);

//            sp.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_black)), memberStateMent.indexOf(mHeadlines.get(index).phone),
//                    memberStateMent.indexOf(mHeadlines.get(index).phone) + mHeadlines.get(index).phone.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            //变色
            sp.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.invite_count)), memberStateMent.indexOf( String.valueOf(mHeadlines.get(index).coupon)),
                    memberStateMent.indexOf( String.valueOf(mHeadlines.get(index).coupon)) + String.valueOf(mHeadlines.get(index).coupon).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //加粗
            sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), memberStateMent.indexOf( String.valueOf(mHeadlines.get(index).coupon)),
                    memberStateMent.indexOf( String.valueOf(mHeadlines.get(index).coupon)) + String.valueOf(mHeadlines.get(index).coupon).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textSwitcher.setText(sp);
            index++;
            if (index == mHeadlines.size()) {
                index = 0;
            }
        }
    }

    private void test(){
        textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView = null;
                if(textView == null) {
                    textView = new TextView(InviteActivity.this);
                    textView.setSingleLine();
                    textView.setTextSize(12);
                    textView.setTextColor(getResources().getColor(R.color.invite_text));
                    textView.setEllipsize(TextUtils.TruncateAt.END);
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    lp.gravity = Gravity.CENTER;
                    textView.setLayoutParams(lp);
                }
                return textView;

            }
        });
        bitHandler = new BitHandler();
        new myThread().start();
    }

    private class myThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (index < mHeadlines.size()) {
                try {
                    synchronized (this) {
                        bitHandler.sendEmptyMessage(0);
                        this.sleep(3000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){

            Intent intent = new Intent(InviteActivity.this,MainActivity.class);
            intent.putExtra(Constants.KEY_IS_PERSONAL,true);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
