package com.rainbowcard.client.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.common.PayActivity;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.MapEntity;
import com.rainbowcard.client.model.OrderModel;
import com.rainbowcard.client.model.ShopEntity;
import com.rainbowcard.client.model.ShopOrderEntity;
import com.rainbowcard.client.utils.DensityUtil;
import com.rainbowcard.client.utils.LocateManager;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.TimeUtil;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.utils.Validation;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.iwgang.countdownview.CountdownView;

/**
 * Created by gc on 2017-1-11.
 */
public class OrderDetailActivity extends MyBaseActivity{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.content_layout)
    RelativeLayout contentLayout;
    @InjectView(R.id.tv_name)
    TextView nameTv;
    @InjectView(R.id.status)
    TextView statusTv;
    @InjectView(R.id.shop_icon)
    ImageView shopIcon;
    @InjectView(R.id.tv_addr)
    TextView addrTv;
    @InjectView(R.id.shop_type)
    TextView shopType;
    @InjectView(R.id.shop_money)
    TextView shopMoney;
    @InjectView(R.id.code_type)
    TextView codeType;
    @InjectView(R.id.code)
    TextView codeTv;
    @InjectView(R.id.price)
    TextView priceTv;
    @InjectView(R.id.pay)
    TextView payTv;
    @InjectView(R.id.pay_type)
    TextView payType;
    @InjectView(R.id.order)
    TextView orderTv;
    @InjectView(R.id.date)
    TextView dateTv;
    @InjectView(R.id.discount)
    TextView discountTv;
    @InjectView(R.id.comment)
    TextView commentBtn;
    @InjectView(R.id.hint_text)
    TextView hintText;
    @InjectView(R.id.no_pay_layout)
    LinearLayout noPayLayout;
    @InjectView(R.id.countdown)
    CountdownView countdown;
    @InjectView(R.id.cancel_order)
    TextView cancelOrder;
    @InjectView(R.id.pay_order)
    TextView payOrder;
    @InjectView(R.id.nav_btn)
    TextView navBtn;
    @InjectView(R.id.need_pay)
    TextView needPay;

    OrderModel.OrderEntity orderEntity;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(OrderDetailActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(OrderDetailActivity.this,true);
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(OrderDetailActivity.this, Constants.USERINFO, Constants.UID));
        orderEntity = (OrderModel.OrderEntity) getIntent().getSerializableExtra(Constants.KEY_ORDER_MODEL);
        initView();
    }
    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.order_detail));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailActivity.this,ShopDetailActivity.class);
                intent.putExtra(Constants.KEY_SHOP_ID,orderEntity.shopId);
                startActivity(intent);
            }
        });

        if(!TextUtils.isEmpty(orderEntity.shopName)){
            nameTv.setText(orderEntity.shopName);
        }

        switch (orderEntity.status){
            case 1:
                statusTv.setText("待使用");
                commentBtn.setVisibility(View.GONE);
                hintText.setVisibility(View.VISIBLE);
                noPayLayout.setVisibility(View.GONE);
                needPay.setText("实付款：");
                break;
            case 2:
                statusTv.setText("待评论");
                commentBtn.setVisibility(View.VISIBLE);
                hintText.setVisibility(View.GONE);
                noPayLayout.setVisibility(View.GONE);
                needPay.setText("实付款");
                break;
            case 3:
                statusTv.setText("已评价");
                commentBtn.setVisibility(View.GONE);
                hintText.setVisibility(View.GONE);
                noPayLayout.setVisibility(View.GONE);
                needPay.setText("实付款：");
                break;
            case 4:
                statusTv.setText("已过期");
                commentBtn.setVisibility(View.GONE);
                hintText.setVisibility(View.GONE);
                noPayLayout.setVisibility(View.GONE);
                needPay.setText("需付款：");
                break;
            case 8:
                statusTv.setText("待支付");
                commentBtn.setVisibility(View.GONE);
                hintText.setVisibility(View.GONE);
                noPayLayout.setVisibility(View.VISIBLE);
                needPay.setText("需付款：");
                break;
        }

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailActivity.this,CommentActivity.class);
                intent.putExtra(Constants.KEY_SHOP_IMG,orderEntity.shopImg);
                intent.putExtra(Constants.KEY_TRADE_NO,orderEntity.tradeNo);
                startActivityForResult(intent,Constants.REQUEST_COMMENT);
            }
        });
        if(TextUtils.isEmpty(orderEntity.shopImg)){
            shopIcon.setImageResource(R.drawable.order_default);
        }else {
            Picasso.with(OrderDetailActivity.this)
                    .load(String.format(getString(R.string.img_url),orderEntity.shopImg))
                    .resize(DensityUtil.dip2px(OrderDetailActivity.this,70),DensityUtil.dip2px(OrderDetailActivity.this,70))
                    .centerCrop()
                    .placeholder(R.drawable.order_default)
                    .error(R.drawable.order_default).into(shopIcon);
        }

        if(!TextUtils.isEmpty(orderEntity.shopAddress)){
            addrTv.setText(orderEntity.shopAddress);
        }
        if (!TextUtils.isEmpty(orderEntity.code)){
            codeTv.setText(orderEntity.code);
            hintText.setText(String.format(getString(R.string.hint_text),orderEntity.code));
            Spannable span = new SpannableString(hintText.getText());
            span.setSpan(new AbsoluteSizeSpan(58), 8, 8 + orderEntity.code.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.money_color)), 8, 8 + orderEntity.code.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            span.setSpan(new BackgroundColorSpan(Color.YELLOW), 11, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            hintText.setText(span);
        }

        if(TextUtils.isEmpty(orderEntity.serviceSonTypeText)){
            shopType.setText(orderEntity.serviceTypeText);
        }else {
            shopType.setText(String.format(getString(R.string.service_type), orderEntity.serviceTypeText, orderEntity.serviceSonTypeText));
        }
//        if(!TextUtils.isEmpty(orderEntity.money)){
            priceTv.setText(String.format(getString(R.string.price),new
                    DecimalFormat("##,###,###,###,##0.00").format(orderEntity.money)));
            shopMoney.setText(String.format(getString(R.string.price),new
                    DecimalFormat("##,###,###,###,##0.00").format(orderEntity.money)));
//        }
//        if(!TextUtils.isEmpty(orderEntity.payMoney)){
            payTv.setText(String.format(getString(R.string.price),new
                    DecimalFormat("##,###,###,###,##0.00").format(orderEntity.payMoney)));
//        }

        if(!TextUtils.isEmpty(orderEntity.time)){
            dateTv.setText(orderEntity.time);
        }
        if(!TextUtils.isEmpty(orderEntity.tradeNo)){
            orderTv.setText(orderEntity.tradeNo);
        }

        switch (orderEntity.paymentType){
            case 0:
                payType.setText("未支付");
                break;
            case 1:
                payType.setText("余额支付");
                break;
            case 2:
                payType.setText(String.format(getString(R.string.rainbow_no),orderEntity.cardNumber));
                break;
            case 3:
                payType.setText("微信支付");
                break;
            case 4:
                payType.setText("支付宝支付");
                break;
            case 9:
                payType.setText("免费洗车券");
                break;
            default:
                payType.setText("未支付");
        }

        discountTv.setText(String.format(getString(R.string.sub_price),new
                DecimalFormat("##,###,###,###,##0.00").format(orderEntity.couponMoney)));

        countdown.start(TimeUtil.countDownTime(orderEntity.expire));
        countdown.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                finish();
            }
        });

        payOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailActivity.this, ShopOrderActivity.class);
                intent.putExtra(Constants.KEY_ORDER_MODEL,orderEntity);
                startActivity(intent);
            }
        });

        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("温馨提示","订单取消将无法恢复",orderEntity.tradeNo);
            }
        });
        navBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LocateManager.isAvilible(OrderDetailActivity.this,"com.baidu.BaiduMap") || LocateManager.isAvilible(OrderDetailActivity.this,
                        "com.autonavi.minimap") || LocateManager.isAvilible(OrderDetailActivity.this,"com.tencent.map")) {
                    loadMap(orderEntity.shopName,orderEntity.latitude,orderEntity.longitude);
                }else {
                    UIUtils.toast("请安装地图客户端");
                }
            }
        });

    }

    void loadMap(final String name,final double lat,final double lng){
        List<MapEntity> mapList = new ArrayList<MapEntity>();
        if(LocateManager.isAvilible(OrderDetailActivity.this,"com.baidu.BaiduMap")){
            MapEntity entity = new MapEntity();
            entity.mapName = "百度地图";
            entity.mapType = 0;
            mapList.add(entity);
        }
        if(LocateManager.isAvilible(OrderDetailActivity.this,"com.autonavi.minimap")){
            MapEntity entity = new MapEntity();
            entity.mapName = "高德地图";
            entity.mapType = 1;
            mapList.add(entity);
        }
        if(LocateManager.isAvilible(OrderDetailActivity.this,"com.tencent.map")){
            MapEntity entity = new MapEntity();
            entity.mapName = "腾讯地图";
            entity.mapType = 2;
            mapList.add(entity);
        }
        Dialog dialog = UIUtils.alertButtonListBottom(OrderDetailActivity.this,"选择地图导航",false);
        Window dialogWindow = dialog.getWindow();
        Display display = getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int)(display.getWidth());
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.BOTTOM);
        for (final MapEntity mapEntity : mapList){
            UIUtils.addButtonToButtonListBottom(dialog,mapEntity.mapName,new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    switch (mapEntity.mapType){
                        case 0:
                            intent = new Intent();
                            // 驾车导航
                            intent.setData(Uri.parse("baidumap://map/navi?query="+name+"&location="+lat+","+lng));
                            startActivity(intent);
                            break;
                        case 1:
                            intent = new Intent("android.intent.action.VIEW",android.net.Uri.parse("androidamap://navi?sourceApplication=快方配送&lat=" +
                                    bd09togcj02(lat,lng)[0] + "&lon="+ bd09togcj02(lat,lng)[1] + "&dev=0"));
                            intent.setPackage("com.autonavi.minimap");
                            startActivity(intent);
                            break;
                        case 2:
                            intent = new Intent();
                            intent.setData(Uri.parse("qqmap://map/routeplan?type=drive&to="+name+"&tocoord="+bd09togcj02(lat,lng)[0]+","+bd09togcj02(lat,lng)[1]));
                            startActivity(intent);
                            break;
                    }
                }
            });
        }
    }

    /**
     * 百度坐标系(BD-09)转火星坐标系(GCJ-02)
     *
     * 百度——>谷歌、高德
     * @param bd_lon 百度坐标纬度
     * @param bd_lat 百度坐标经度
     * @return 火星坐标数组
     */
    public static double[] bd09togcj02(double bd_lon, double bd_lat) {
        double x_pi=3.14159265358979324 * 3000.0 / 180.0;
        double x = bd_lon - 0.0065;
        double y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        double gg_lng = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new double[] { gg_lng, gg_lat };
    }

    private void showDialog(String title, String tip,final String tradeNo) {
        final Dialog dialog = new Dialog(OrderDetailActivity.this,R.style.loading_dialog);
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
        alertCancle.setText("关闭");
        dialog.findViewById(R.id.alert_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrder(tradeNo);
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

    //取消订单
    void cancelOrder(String tradeNo){
        withBtwVolley().load(String.format(getString(R.string.url), API.API_DELETE_PAYMENT,tradeNo))
                .method(Request.Method.POST)
                .setHeader("Authorization",token)
                .setHeader("Accept", "application/vnd.caihongka.v2.1.0+json")
                .setUIComponent(this)
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
                        UIUtils.toast("订单已取消");
                        finish();
                    }

                    @Override
                    public void onBtwError(BtwRespError<String> error) {
                        Toast.makeText(OrderDetailActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(OrderDetailActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("GCCCCCCC",requestCode+"??????");
        if (requestCode == Constants.REQUEST_COMMENT && data != null) {
            commentBtn.setVisibility(View.GONE);
            statusTv.setText("已评价");
        }
    }
}
