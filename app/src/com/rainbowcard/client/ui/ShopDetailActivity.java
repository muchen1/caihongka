package com.rainbowcard.client.ui;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
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
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.MapEntity;
import com.rainbowcard.client.model.MarkerInfoModel;
import com.rainbowcard.client.model.MarkerInfoUtil;
import com.rainbowcard.client.model.OrderModel;
import com.rainbowcard.client.model.ServeTypeEntity;
import com.rainbowcard.client.model.ShopEntity;
import com.rainbowcard.client.model.ShopModel;
import com.rainbowcard.client.ui.adapter.CommentListAdapter;
import com.rainbowcard.client.ui.adapter.ServeTypeAdapter;
import com.rainbowcard.client.utils.DensityUtil;
import com.rainbowcard.client.utils.LocateManager;
import com.rainbowcard.client.utils.LoginControl;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.StringUtil;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.LoadingFrameLayout;
import com.rainbowcard.client.widget.MyListView;
import com.rainbowcard.client.widget.MyScrollView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017-1-4.
 */
public class ShopDetailActivity extends MyBaseActivity {

    @InjectView(R.id.scrollView)
    MyScrollView scrollView;
    @InjectView(R.id.fl_loading)
    LoadingFrameLayout mFlLoading;
    @InjectView(R.id.shop_img)
    ImageView shopImg;
    @InjectView(R.id.tv_name)
    TextView nameTv;
    @InjectView(R.id.tv_horn)
    TextView hornTv;
    @InjectView(R.id.addr_layout)
    RelativeLayout addrLayout;
    @InjectView(R.id.bottom_layout)
    RelativeLayout bottomLayout;
    @InjectView(R.id.tv_addr)
    TextView addrTv;
    @InjectView(R.id.nav_icon)
    ImageView navIcon;
    @InjectView(R.id.comment_layout)
    RelativeLayout commentLayout;
    @InjectView(R.id.tv_order)
    TextView orderTv;
    @InjectView(R.id.tv_grade)
    TextView gradeTv;
    @InjectView(R.id.business_listview)
    MyListView businessLv;
    @InjectView(R.id.nav_back)
    LinearLayout navBack;
    @InjectView(R.id.nav_right_btn)
    LinearLayout navRight;
    @InjectView(R.id.nav_right)
    ImageView navRightIv;
    @InjectView(R.id.tv_money)
    TextView moneyTv;
    @InjectView(R.id.buy_btn)
    TextView buyBtn;

    ServeTypeAdapter adapter;
    List<ServeTypeEntity> serveTypeEntities = new ArrayList<ServeTypeEntity>();
    String shopId;
    int serviceType;
    int sonServiceType;
    String serviceName;
    String shopName;
    int serviceMoney;
    String activityMoney;
    boolean isEntrance;
    int isRb;

    //是否免费券洗车
    boolean isFree;

    String token;
    ShopEntity shopEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        ButterKnife.inject(this);
        shopId = getIntent().getStringExtra(Constants.KEY_SHOP_ID);
        serviceType = getIntent().getIntExtra(Constants.KEY_SERVICE_TYPE,0);
        isEntrance = getIntent().getBooleanExtra(Constants.KEY_ENTRANCE,false);
        isFree = getIntent().getBooleanExtra(Constants.KEY_IS_FREE,false);
        isRb = getIntent().getIntExtra(Constants.KEY_RB_USER,0);
        initView();
        getShopDetail();
    }

    @Override
    protected void onResume() {
        super.onResume();
        shopEntity = null;
    }

    void initView(){

        if(isRb == 1 && serviceType == 1){
            bottomLayout.setVisibility(View.GONE);
            businessLv.setVisibility(View.GONE);
        }else {
            bottomLayout.setVisibility(View.VISIBLE);
            businessLv.setVisibility(View.VISIBLE);
        }

        scrollView.smoothScrollTo(0, 0);
        adapter = new ServeTypeAdapter(this);
//        initServeType();
        businessLv.setAdapter(adapter);
        businessLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setCurrentItem(position);
                if(TextUtils.isEmpty(adapter.getServeTypeEntitys().get(position).serviceActivity)) {
                    moneyTv.setText(String.format(getString(R.string.price), new
                            DecimalFormat("##,###,###,###,##0.00").format(adapter.getServeTypeEntitys().get(position).money)));
                }else {
                    moneyTv.setText(String.format(getString(R.string.price), new
                            DecimalFormat("##,###,###,###,##0.00").format(Float.valueOf(adapter.getServeTypeEntitys().get(position).serviceActivity))));
                    activityMoney = adapter.getServeTypeEntitys().get(position).serviceActivity;
                }
                serviceType = adapter.getServeTypeEntitys().get(position).serviceType;
                sonServiceType = adapter.getServeTypeEntitys().get(position).serviceSonType;
                serviceName = adapter.getServeTypeEntitys().get(position).serviceName;
                serviceMoney = adapter.getServeTypeEntitys().get(position).money;
            }
        });

        navBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        navRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.toast("收藏了");
            }
        });
        commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopDetailActivity.this, CommentListActivity.class);
                intent.putExtra(Constants.KEY_SHOP_ID,shopId);
                startActivity(intent);
            }
        });
        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(LoginControl.getInstance(ShopDetailActivity.this).isLogin()) {
                    if(isFree){
                        if(serviceType == 1) {
                            showDialog("温馨提示", "确定使用1张免费\n洗车券购买本店铺洗车服务吗？");
                        }else {
                            UIUtils.toast("只能购买洗车服务");
                        }
                    }else {
                        intent = new Intent(ShopDetailActivity.this, ShopOrderActivity.class);
                        intent.putExtra(Constants.KEY_SHOP_ID, shopId);
                        intent.putExtra(Constants.KEY_SHOP_NAME, shopName);
                        intent.putExtra(Constants.KEY_SERVICE_TYPE, serviceType);
                        intent.putExtra(Constants.KEY_SON_SERVICE_TYPE, sonServiceType);
                        intent.putExtra(Constants.KEY_SERVICE_NAME, serviceName);
                        intent.putExtra(Constants.KEY_SERVICE_MONEY, serviceMoney);
                        intent.putExtra(Constants.KEY_ACTIVITY_MONEY, activityMoney);
                        startActivity(intent);
                    }
                }else {
                    intent = new Intent(ShopDetailActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

            }
        });
    }
    void refreshUI(){
        adapter.setServeTypes(shopEntity.serviceEntitys);
        if(shopEntity.imgUrl.length > 0) {
            if (TextUtils.isEmpty(shopEntity.imgUrl[0])) {
                shopImg.setImageResource(R.drawable.detail_default);
            } else {
                Picasso.with(ShopDetailActivity.this)
                        .load(String.format(getString(R.string.img_url),shopEntity.imgUrl[0]))
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.detail_default)
                        .error(R.drawable.detail_default).into(shopImg);
            }
        }else {
            shopImg.setImageResource(R.drawable.detail_default);
        }
        nameTv.setText(shopEntity.name);
        addrTv.setText(shopEntity.addr);
//        orderTv.setText(String.format(getString(R.string.order),shopEntity.orderNum));
//        gradeTv.setText(String.format(getString(R.string.comment),shopEntity.star));
        gradeTv.setText(shopEntity.star);
        hornTv.setText(shopEntity.notice);
        if(isEntrance) {
            switch (serviceType) {
                case 1:
                    for (int i = 0; i < adapter.getServeTypeEntitys().size(); i++) {
                        if (adapter.getServeTypeEntitys().get(i).serviceType == 1 && adapter.getServeTypeEntitys().get(i).serviceSonType == 1) {
                            adapter.setCurrentItem(i);
                            if(TextUtils.isEmpty(adapter.getServeTypeEntitys().get(i).serviceActivity)) {
                                moneyTv.setText(String.format(getString(R.string.price), new
                                        DecimalFormat("##,###,###,###,##0.00").format(adapter.getServeTypeEntitys().get(i).money)));
                            }else {
                                moneyTv.setText(String.format(getString(R.string.price), new
                                        DecimalFormat("##,###,###,###,##0.00").format(Float.valueOf(adapter.getServeTypeEntitys().get(i).serviceActivity))));
                            }
                            serviceType = adapter.getServeTypeEntitys().get(i).serviceType;
                            sonServiceType = adapter.getServeTypeEntitys().get(i).serviceSonType;
                            serviceName = adapter.getServeTypeEntitys().get(i).serviceName;
                            shopName = shopEntity.name;
                            serviceMoney = adapter.getServeTypeEntitys().get(i).money;
                            activityMoney = adapter.getServeTypeEntitys().get(i).serviceActivity;
                        }

                    }
                    break;
                case 2:
                    for (int i = 0; i < adapter.getServeTypeEntitys().size(); i++) {
                        if (adapter.getServeTypeEntitys().get(i).serviceType == 2 && adapter.getServeTypeEntitys().get(i).serviceSonType == 1) {
                            adapter.setCurrentItem(i);
                            if(TextUtils.isEmpty(adapter.getServeTypeEntitys().get(i).serviceActivity)) {
                                moneyTv.setText(String.format(getString(R.string.price), new
                                        DecimalFormat("##,###,###,###,##0.00").format(adapter.getServeTypeEntitys().get(i).money)));
                            }else {
                                moneyTv.setText(String.format(getString(R.string.price), new
                                        DecimalFormat("##,###,###,###,##0.00").format(Float.valueOf(adapter.getServeTypeEntitys().get(i).serviceActivity))));
                            }
                            serviceType = adapter.getServeTypeEntitys().get(i).serviceType;
                            sonServiceType = adapter.getServeTypeEntitys().get(i).serviceSonType;
                            serviceName = adapter.getServeTypeEntitys().get(i).serviceName;
                            shopName = shopEntity.name;
                            serviceMoney = adapter.getServeTypeEntitys().get(i).money;
                            activityMoney = adapter.getServeTypeEntitys().get(i).serviceActivity;
                        }

                    }
                    break;
                case 4:
                    for (int i = 0; i < adapter.getServeTypeEntitys().size(); i++) {
                        if (adapter.getServeTypeEntitys().get(i).serviceType == 4) {
                            adapter.setCurrentItem(i);
                            if(TextUtils.isEmpty(adapter.getServeTypeEntitys().get(i).serviceActivity)) {
                                moneyTv.setText(String.format(getString(R.string.price), new
                                        DecimalFormat("##,###,###,###,##0.00").format(adapter.getServeTypeEntitys().get(i).money)));
                            }else {
                                moneyTv.setText(String.format(getString(R.string.price), new
                                        DecimalFormat("##,###,###,###,##0.00").format(Float.valueOf(adapter.getServeTypeEntitys().get(i).serviceActivity))));
                            }
                            serviceType = adapter.getServeTypeEntitys().get(i).serviceType;
                            sonServiceType = adapter.getServeTypeEntitys().get(i).serviceSonType;
                            serviceName = adapter.getServeTypeEntitys().get(i).serviceName;
                            shopName = shopEntity.name;
                            serviceMoney = adapter.getServeTypeEntitys().get(i).money;
                            activityMoney = adapter.getServeTypeEntitys().get(i).serviceActivity;
                        }

                    }
                    break;
            }
        }else {
            if (TextUtils.isEmpty(adapter.getServeTypeEntitys().get(0).serviceActivity)) {
                moneyTv.setText(String.format(getString(R.string.price), new
                        DecimalFormat("##,###,###,###,##0.00").format(adapter.getServeTypeEntitys().get(0).money)));
            }else {
                moneyTv.setText(String.format(getString(R.string.price), new
                        DecimalFormat("##,###,###,###,##0.00").format(Float.valueOf(adapter.getServeTypeEntitys().get(0).serviceActivity))));
            }
            serviceType = adapter.getServeTypeEntitys().get(0).serviceType;
            sonServiceType = adapter.getServeTypeEntitys().get(0).serviceSonType;
            serviceName = adapter.getServeTypeEntitys().get(0).serviceName;
            shopName = shopEntity.name;
            serviceMoney = adapter.getServeTypeEntitys().get(0).money;
            activityMoney = adapter.getServeTypeEntitys().get(0).serviceActivity;
        }

        addrLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LocateManager.isAvilible(ShopDetailActivity.this,"com.baidu.BaiduMap") || LocateManager.isAvilible(ShopDetailActivity.this,
                        "com.autonavi.minimap") || LocateManager.isAvilible(ShopDetailActivity.this,"com.tencent.map")) {
                    loadMap(shopEntity);
                }else {
                    UIUtils.toast("请安装地图客户端");
                }
            }
        });

        if(shopEntity.isCollect){
            navRightIv.setImageResource(R.drawable.nav_collection_click);
        }else {
            navRightIv.setImageResource(R.drawable.nav_collection);
        }

        navRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shopEntity.isCollect){
                    deleteShop(shopEntity.shopId);
                }else {
                    collectShop(shopEntity.shopId);
                }
            }
        });
    }

    //收藏商户
    void collectShop(String shopId){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(ShopDetailActivity.this, Constants.USERINFO, Constants.UID));
        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("shop_id",shopId);

        withBtwVolley().load(API.API_COLLECT_SHOP)
                .method(Request.Method.POST)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION2D1)
                .setParam("shop_id", shopId)
                .setParam("paramSign", StringUtil.createParameterSign(parameters))
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
                        UIUtils.toast("收藏成功");
                        navRightIv.setImageResource(R.drawable.nav_collection_click);
                        shopEntity.isCollect = true;

                    }

                    @Override
                    public void onBtwError(BtwRespError<String> error) {
                        Toast.makeText(ShopDetailActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(ShopDetailActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute();
    }
    //删除收藏商户
    void deleteShop(String shopId){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(ShopDetailActivity.this, Constants.USERINFO, Constants.UID));
        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("shop_id",shopId);

        withBtwVolley().load(API.API_DELETE_COLLECT_SHOP)
                .method(Request.Method.POST)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION2D1)
                .setParam("shop_id", shopId)
                .setParam("paramSign", StringUtil.createParameterSign(parameters))
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
                        UIUtils.toast("删除成功");
                        navRightIv.setImageResource(R.drawable.nav_collection);
                        shopEntity.isCollect = false;

                    }

                    @Override
                    public void onBtwError(BtwRespError<String> error) {
                        Toast.makeText(ShopDetailActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(ShopDetailActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute();
    }

    //获取商户详情
    void getShopDetail(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(ShopDetailActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(API.API_GET_SHOP_INFO)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("shop_id",shopId)
                .method(Request.Method.GET)
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(20000)
                .setResponseHandler(new BtwVolley.ResponseHandler<ShopModel>() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onResponse(ShopModel resp) {
                        shopEntity = resp.data;
                        refreshUI();
                    }

                    @Override
                    public void onBtwError(BtwRespError<ShopModel> error) {
                        UIUtils.toast(error.errorMessage);
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        UIUtils.toast(getString(R.string.network_error));
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(ShopModel.class);
    }
    void loadMap(final ShopEntity infoUtil){
        List<MapEntity> mapList = new ArrayList<MapEntity>();
        if(LocateManager.isAvilible(ShopDetailActivity.this,"com.baidu.BaiduMap")){
            MapEntity entity = new MapEntity();
            entity.mapName = "百度地图";
            entity.mapType = 0;
            mapList.add(entity);
        }
        if(LocateManager.isAvilible(ShopDetailActivity.this,"com.autonavi.minimap")){
            MapEntity entity = new MapEntity();
            entity.mapName = "高德地图";
            entity.mapType = 1;
            mapList.add(entity);
        }
        if(LocateManager.isAvilible(ShopDetailActivity.this,"com.tencent.map")){
            MapEntity entity = new MapEntity();
            entity.mapName = "腾讯地图";
            entity.mapType = 2;
            mapList.add(entity);
        }
        Dialog dialog = UIUtils.alertButtonListBottom(ShopDetailActivity.this,"选择地图导航",false);
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
                            intent.setData(Uri.parse("baidumap://map/navi?query="+infoUtil.name+"&location="+infoUtil.latitude+","+infoUtil.longitude));
                            startActivity(intent);
                            break;
                        case 1:
                            intent = new Intent("android.intent.action.VIEW",android.net.Uri.parse("androidamap://navi?sourceApplication=快方配送&lat=" +
                                    bd09togcj02(infoUtil.latitude,infoUtil.longitude)[0] + "&lon="+ bd09togcj02(infoUtil.latitude,infoUtil.longitude)[1] + "&dev=0"));
                            intent.setPackage("com.autonavi.minimap");
                            startActivity(intent);
                            break;
                        case 2:
                            intent = new Intent();
                            intent.setData(Uri.parse("qqmap://map/routeplan?type=drive&to="+infoUtil.name+"&tocoord="+bd09togcj02(infoUtil.latitude,infoUtil.longitude)[0]+","+bd09togcj02(infoUtil.latitude,infoUtil.longitude)[1]));
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

    private void showDialog(String title, String tip) {
        final Dialog dialog = new Dialog(ShopDetailActivity.this,R.style.loading_dialog);
        dialog.setContentView(R.layout.ui_ios_alert_dialog);
        Window dialogWindow = dialog.getWindow();
        Display display = getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = -80;
        lp.width = (int)(display.getWidth() * 0.90);
        dialogWindow.setAttributes(lp);

        TextView alertTitle = (TextView) dialog.findViewById(R.id.alert_title);
        TextView alertTip = (TextView) dialog.findViewById(R.id.alert_tip);
        TextView alertOk = (TextView) dialog.findViewById(R.id.alert_ok);

        alertTitle.setText(title);
        Spannable span = new SpannableString(tip);
        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.money_color)), 4, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        alertTip.setText(span);
//        alertTip.setText(tip);
        alertOk.setTextColor(getResources().getColor(R.color.stroke));
        dialog.findViewById(R.id.alert_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payment();
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


    //免费洗车券购买数字码   只能购买洗车
    void payment(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(ShopDetailActivity.this, Constants.USERINFO, Constants.UID));

        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("son_type",sonServiceType);
        parameters.put("shop_id",shopId);

        withBtwVolley().load(API.API_FREE_PAYMENT_CODE)
                .method(Request.Method.POST)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setParam("son_type",sonServiceType)
                .setParam("shop_id",shopId)
                .setParam("paramSign", StringUtil.createParameterSign(parameters))
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<OrderModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(OrderModel resp) {
                        Intent intent = new Intent(ShopDetailActivity.this,ShopOrderSucceedActivity.class);
                        intent.putExtra(Constants.KEY_IS_FREE,isFree);
                        intent.putExtra(Constants.KEY_TRADE_NO,resp.data.tradeNo);
                        startActivity(intent);
                    }

                    @Override
                    public void onBtwError(BtwRespError<OrderModel> error) {
                        Toast.makeText(ShopDetailActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(ShopDetailActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
                    }
                }).excute(OrderModel.class);
    }
}
