package com.rainbowcard.client.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.bestpay.app.PaymentTask;
import com.bestpay.plugin.Plugin;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.base.YHApplication;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.UpdateModel;
import com.rainbowcard.client.utils.CryptTool;
import com.rainbowcard.client.utils.DataCleanManager;
import com.rainbowcard.client.utils.LoginControl;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.PayUtil;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.utils.UpdateManger;
import com.rainbowcard.client.utils.Util;
import com.rainbowcard.client.widget.HeadControlPanel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2016-10-25.
 */
public class SettingActivity extends MyBaseActivity {

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.feedback_layout)
    RelativeLayout feedbackLayout;
    @InjectView(R.id.phone_layout)
    RelativeLayout phoneLayout;
    @InjectView(R.id.service_tel)
    TextView telTv;
    @InjectView(R.id.about_layout)
    RelativeLayout aboutLayout;
    @InjectView(R.id.update_layout)
    RelativeLayout updateLayout;
    @InjectView(R.id.account_layout)
    RelativeLayout accountLayout;
    @InjectView(R.id.clear_layout)
    RelativeLayout clearLayout;
    @InjectView(R.id.clear_size)
    TextView clearSizeTv;
    @InjectView(R.id.exit_btn)
    TextView exitBtn;

    PaymentTask task = new PaymentTask(SettingActivity.this);
    private boolean mIsH5Payment = false;
    static final int ORDER_FAIL = -1;
    static final int ORDER_SUCCESS = 0;
    Hashtable<String, String> paramsHashtable = new Hashtable<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(SettingActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(SettingActivity.this,true);
        initView();
    }


    void initView(){

        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.setting));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        phoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
//                        alertCallServiceDialog(getActivity());
                        UIUtils.showConfirmDialog(SettingActivity.this,telTv.getText().toString());
                    }
                }, 300);

            }
        });
        feedbackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this,FeedBackActivity.class);
                startActivity(intent);
            }
        });
        aboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this,AboutActivity.class);
                startActivity(intent);
            }
        });

        updateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUpdate();
            }
        });

        queryCache();
        clearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("温馨提示","缓存清除将无法恢复");
//                pay();
            }
        });

        accountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(LoginControl.getInstance(SettingActivity.this).isLogin()) {
                    intent = new Intent(SettingActivity.this, UserSetActivity.class);
                    startActivity(intent);
                }else {
                    intent = new Intent(SettingActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        if(LoginControl.getInstance(SettingActivity.this).isLogin()){
            exitBtn.setBackgroundResource(R.drawable.query_select_item);
            exitBtn.setTextColor(getResources().getColor(R.color.white));
            exitBtn.setEnabled(true);
        }else {
            exitBtn.setBackgroundResource(R.drawable.button_darkgray);
            exitBtn.setTextColor(getResources().getColor(R.color.gray_text));
            exitBtn.setEnabled(false);
        }
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginControl.getInstance(SettingActivity.this).isLogin()){
                    MyConfig.putSharePre(SettingActivity.this, Constants.USERINFO, Constants.UID, "");
//                        MyConfig.putSharePre(SettingActivity.this, Constants.USERINFO, Constants.PHONE, "");
                    exitBtn.setBackgroundResource(R.drawable.button_darkgray);
                    exitBtn.setTextColor(getResources().getColor(R.color.gray_text));
                    exitBtn.setEnabled(false);
                    finish();
//                    logout();
                }
            }
        });
    }

    private void showDialog(String title,String tip) {
        final Dialog dialog = new Dialog(SettingActivity.this,R.style.loading_dialog);
        dialog.setContentView(R.layout.ui_alert_dialog);
        Window dialogWindow = dialog.getWindow();
        Display display = getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = -80;
        lp.width = (int)(display.getWidth() * 0.95);
        dialogWindow.setAttributes(lp);

        TextView alertTitle = (TextView) dialog.findViewById(R.id.alert_title);
        TextView alertTip = (TextView) dialog.findViewById(R.id.alert_tip);
        TextView alertOk = (TextView) dialog.findViewById(R.id.alert_ok);
        TextView alertCancle = (TextView) dialog.findViewById(R.id.alert_cancel);
        alertTitle.setText(title);
        alertTip.setText(tip);
        alertOk.setText("继续");
        alertCancle.setText("取消");
        dialog.findViewById(R.id.alert_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCache();
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.alert_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void clearCache(){
        DataCleanManager.clearAllCache(SettingActivity.this);
        queryCache();
    }

    void queryCache(){
        try {
            clearSizeTv.setText(DataCleanManager.getTotalCacheSize(SettingActivity.this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void checkUpdate(){
        withBtwVolley().load(API.API_CHECK_UPDATE)
                .method(Request.Method.GET)
                .setHeader("Accept", API.VERSION)
                .setParam("version",YHApplication.instance().getVersionName())
                .setParam("type", API.API_TYPE)
                .setTimeout(10000)
                .setRetrys(0)
                .setUIComponent(this)
                .setResponseHandler(new BtwVolley.ResponseHandler<UpdateModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(UpdateModel resp) {
                        if(resp.data.must){
                            new UpdateManger(SettingActivity.this,resp.data.url).showConfirmDialog("温馨提示",resp.data.remarks);
                        }else {
                            if(resp.data.update){
                                new UpdateManger(SettingActivity.this,resp.data.url).showDialog("温馨提示",resp.data.remarks);
                            }else {
                                UIUtils.toast("当前已是最新版本");
                            }
                        }
                    }

                    @Override
                    public void onBtwError(BtwRespError<UpdateModel> error) {
                        Toast.makeText(SettingActivity.this, error.errorMessage,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(SettingActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(UpdateModel.class);
    }

    //退出登录
    void logout(){
        withBtwVolley().load(API.API_LOGOUT)
                .method(Request.Method.DELETE)
                .setHeader("Accept", API.VERSION)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<String>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(String resp) {
                        MyConfig.putSharePre(SettingActivity.this, Constants.USERINFO, Constants.UID, "");
//                        MyConfig.putSharePre(SettingActivity.this, Constants.USERINFO, Constants.PHONE, "");
                        MyConfig.putSharePre(SettingActivity.this, Constants.USERINFO, Constants.KEY_IS_RB, -1);
                        exitBtn.setBackgroundResource(R.drawable.button_darkgray);
                        exitBtn.setTextColor(getResources().getColor(R.color.gray_text));
                        exitBtn.setEnabled(false);
                        finish();
                    }

                    @Override
                    public void onBtwError(BtwRespError<String> error) {
                        Toast.makeText(SettingActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(SettingActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute();
    }

    /*public  void alertCallServiceDialog(Context context) {
        final Dialog callServiceDialog = new Dialog(context,
                R.style.alert_buttonlist_bottom);// 创建自定义样式dialog

        callServiceDialog
                .setContentView(R.layout.ui_alert_customer_service_telephone);
        TextView serviceTel = (TextView)callServiceDialog.findViewById(R.id.service_tel);

        final String serviceTelNum = "4000337977";
        callServiceDialog
                .findViewById(R.id.tel_ll)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + serviceTelNum));
                        startActivity(intent);
                    }
                });
        WindowManager windowManager = getActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = callServiceDialog.getWindow().getAttributes();
        lp.width = (int)(display.getWidth());
        callServiceDialog.getWindow().setAttributes(lp);
        callServiceDialog.setCancelable(true);
        callServiceDialog.show();

    }*/


    void pay(){
        String merchantId = "043101180050000";
        String orderseq = String.valueOf(System.currentTimeMillis());
        String orderTranseq = System.currentTimeMillis() + "00001";
        String orderTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()));
        String ordervalidityTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()+ 60* 1000 * 60 * 24));


        //16个参数；
        StringBuffer md5Buffer = new StringBuffer();
        md5Buffer.append("SERVICE=").append("mobile.security.pay")
                .append("&MERCHANTID=").append(merchantId)
                .append("&MERCHANTPWD=").append("534231")
                .append("&SUBMERCHANTID=").append("")
                .append("&BACKMERCHANTURL=").append("http://127.0.0.1:8040/wapBgNotice.action")
                .append("&ORDERSEQ=").append(orderseq)
                .append("&ORDERREQTRANSEQ=").append(orderTranseq)
                .append("&ORDERTIME=").append(orderTime)
                .append("&ORDERVALIDITYTIME=").append(ordervalidityTime)
                .append("&CURTYPE=").append("RMB")
                .append("&ORDERAMOUNT=").append("0.01")
                .append("&SUBJECT=").append("商品测试")
                .append("&PRODUCTID=").append("04")
                .append("&PRODUCTDESC=").append("Test")
                .append("&CUSTOMERID=").append("12345678901")
                .append("&SWTICHACC=").append("true")
                .append("&KEY=").append("344C4FB521F5A52EA28FB7FC79AEA889478D4343E4548C02");//添加key是用于key校验改造；

        Log.i("GCCCC", "sign加密原串："+md5Buffer.toString());
        String sign = null;

        try {
            sign = CryptTool.md5Digest(md5Buffer.toString());

        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Log.i("GCCC", "加密sign值："+sign);

//						String mac = "MERCHANTID="+ merchantId
//								+ "&ORDERSEQ="+ orderseq
//								+ "&ORDERREQTRNSEQ="+ orderTranseq
//								+ "&ORDERTIME="+ orderTime
//								+ "&KEY=" + TestConstant.CKEY;
//
//						try {
//							mac = CryptTool.md5Digest(mac);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}


        //29+1《类似session的字段》个参数；
        final String paramsStr = "MERCHANTID="+merchantId
                +"&SUBMERCHANTID="+""
                +"&MERCHANTPWD="+"534231"
                +"&ORDERSEQ="+orderseq
                +"&ORDERAMOUNT="+"0.01"
                +"&ORDERTIME="+orderTime
                +"&ORDERVALIDITYTIME="+ordervalidityTime
                +"&PRODUCTDESC="+"Test"
                +"&CUSTOMERID="+"12345678901"
                +"&PRODUCTAMOUNT="+"0.01"
                +"&ATTACHAMOUNT=" +"0"
                +"&CURTYPE="+ "RMB"
                +"&BACKMERCHANTURL=" +"http://127.0.0.1:8040/wapBgNotice.action"
                +"&ATTACH=" +""
                +"&PRODUCTID=" +"04"
                +"&USERIP=" +"192.168.11.130"
                +"&DIVDETAILS=" +""

                +"&ACCOUNTID=" +""
                +"&BUSITYPE=" +"04"
                +"&ORDERREQTRANSEQ=" +orderTranseq
                +"&SERVICE=" +"mobile.security.pay"
                +"&SIGNTYPE=" + "MD5"

                +"&SIGN="+sign
                +"&SUBJECT="+"商品测试"
                +"&SWTICHACC="+"true"
                +"&SESSIONKEY="+"asdfasdfahskfjalsdfkajsdfljasdlfjsjfkj"
                +"&OTHERFLOW="+"01"
                +"ACCESSTOKEN"+"lajsfsdjfaljdsflajdsfjalkjslaajdlsjfaldjf";


//						SIGN//上传后台参数值；
//		APPID//无此参数；
//		APPENV//无此参数；
//						NOTIFYURL //BACKMERCHANTURL后台通知地址；
//						ORDERAMT //ORDERAMOUNT 支付订单金额；
//						SUBJECT  //商品简称；
//		SWTICHACC//大厅需要，本地判断，与后台没关系；true表示大厅进入可切换账户；
//		EXTERNTOKEN//无此参数；
//	    SDKVERSIONCODE//sdk中固定写死；
//						        SESSIONKEY//大厅中使用；

        Log.i("GCCC","&&格式字符串："+ paramsStr);





        if (!mIsH5Payment) {
            getUIUtils().loading();
            final Hashtable<String, String> paramsHashtable2 = paramsHashtable;
            // 增加下单流程
            Log.i("GCC", "Android收银台下单");
            new Thread(new Runnable() {
                @Override
                public void run() {
//                    String orderResult = PayUtil.order(paramsStr);
//                    Log.d("GCCCCCCCC","rinidaye"+orderResult);
//                    if (orderResult != null
//                            && "00".equals((orderResult.split("&"))[0])) {
                        Message msg = new Message();
                        msg.what = ORDER_SUCCESS;
                        msg.obj = paramsStr;
                        mHandler.sendMessage(msg);
//                    } else {
//                        mHandler.sendEmptyMessage(ORDER_FAIL);
//                    }
                }
            }).start();
        } else {
            Log.i("GCCCCC", "调用H5收银台");
            task.pay(paramsStr);
        }



    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ORDER_FAIL:
                    Toast.makeText(SettingActivity.this, "下单失败", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case ORDER_SUCCESS:
                    String aa = (String) msg.obj;
                    Log.d("GCCCCC","zenmejinzhelilaile"+aa);
//                    task.pay( (String) msg.obj);
                    String bb = "SERVICE=mobile.security.pay&MERCHANTID=043101180050000&MERCHANTPWD=534231&SUBMERCHANTID=&BACKMERCHANTURL=http://127.0.0.1:8080/abc&SIGNTYPE=MD5&MAC=&ORDERSEQ=1498555365921&ORDERREQTRNSEQ=&ORDERTIME=20170628133129&ORDERVALIDITYTIME=&ORDERAMOUNT=0.01&CURTYPE=RMB&PRODUCTID=04&PRODUCTDESC=test&PRODUCTAMOUNT=0.01&ATTACHAMOUNT=0&ATTACH=&DIVDETAILS=&CUSTOMERID=&USERIP=127.0.0.1&BUSITYPE=04&ACCOUNTID=&ORDERREQTRANSEQ=1498555365921&SIGN=538EDB362A14A9A67A71A19058417204&SUBJECT=ttt&SWTICHACC=";
                    task.pay(bb);
//                    Plugin.yzfClientRecharge(SettingActivity.this, "");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getUIUtils().dismissLoading();
        Log.i("========",resultCode+"");
        if(data!=null)
        {
            Toast.makeText(SettingActivity.this,data.getExtras().getString("result"), Toast.LENGTH_LONG).show();
        }
    }

}
