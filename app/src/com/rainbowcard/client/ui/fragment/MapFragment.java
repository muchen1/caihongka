package com.rainbowcard.client.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseFragment;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.MapEntity;
import com.rainbowcard.client.model.MarkerInfoModel;
import com.rainbowcard.client.model.MarkerInfoUtil;
import com.rainbowcard.client.ui.BranchActivity;
import com.rainbowcard.client.ui.ShopDetailActivity;
import com.rainbowcard.client.utils.DensityUtil;
import com.rainbowcard.client.utils.LocateManager;
import com.rainbowcard.client.utils.UIUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2016-10-25.
 */
public class MapFragment extends MyBaseFragment {

    private BaiduMap mBaiduMap;
    @InjectView(R.id.bmapView)
    TextureMapView mMapView;
    @InjectView(R.id.ib_loc)
    ImageButton locIb;
    @InjectView(R.id.branch_marker)
    RelativeLayout markerRl;

    BitmapDescriptor selectBitmap;
    BitmapDescriptor bitmap;
    Marker selectMarker;
    MarkerInfoUtil selectInfo;

    //创建自己的箭头定位
    private BitmapDescriptor bitmapDescriptor;
    //模式切换，正常模式
    private boolean modeFlag = true;
    //当前地图缩放级别
    private float zoomLevel;
    //定位相关
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;
    //是否第一次定位，如果是第一次定位的话要将自己的位置显示在地图 中间
    private boolean isFirstLocation = true;
    public boolean isRefresh = true;
    BranchActivity instance = null;
    public LatLng latLng;

    public static MapFragment newInstance(){
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map,container,false);
        instance = (BranchActivity) getActivity();
        ButterKnife.inject(this,view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null) {
            UIUtils.toast(bundle.getString(Constants.KEY_NAME));
        }

        //初始化地图
        initMap();
        //定位
        initLocation();
        //开启定位
        mBaiduMap.setMyLocationEnabled(true);
        if(!mLocationClient.isStarted()){
            mLocationClient.start();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
//        if(isRefresh) {
//            isFirstLocation = true;
//        }
        if(isRefresh && latLng != null){
            addCenterOverlay(latLng);
            addOverlay(instance.infos);
        }
    }


    private void initMap() {
        // 不显示缩放比例尺
        mMapView.showZoomControls(false);
        // 不显示百度地图Logo
        mMapView.removeViewAt(1);
        //百度地图
        mBaiduMap = mMapView.getMap();

        // 改变地图状态
        MapStatus mMapStatus = new MapStatus.Builder().zoom(13.7f).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        //设置地图状态改变监听器
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus arg0) {

            }
            @Override
            public void onMapStatusChangeFinish(MapStatus arg0) {
                MapStatus mmapStatus;
                mmapStatus = mBaiduMap.getMapStatus();
                LatLng center = mmapStatus.target;
                if(center.latitude - latLng.latitude > 0.00001 || center.latitude - latLng.longitude > 0.00001 ||
                        latLng.latitude - center.latitude > 0.00001 || center.latitude - latLng.latitude > 0.00001) {
                    latLng = center;
                    addCenterOverlay(center);
//                addOverlay(infos);
                    getShoop(center.latitude, center.longitude);
                }
            }
            @Override
            public void onMapStatusChange(MapStatus arg0) {
                //当地图状态改变的时候，获取放大级别
                zoomLevel = arg0.zoom;
            }
        });
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                markerRl.setVisibility(View.GONE);
                mBaiduMap.hideInfoWindow();
                if(selectMarker != null){
                    selectMarker.setIcon(bitmap);
                }
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        mMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerRl.setVisibility(View.GONE);
                mBaiduMap.hideInfoWindow();
                if(selectMarker != null){
                    selectMarker.setIcon(bitmap);
                }
            }
        });
        locIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFirstLocation = true;
            }
        });
    }

    private void initLocation() {
        //定位客户端的设置
        mLocationClient = new LocationClient(getActivity());
        mLocationListener = new MyLocationListener();
        //注册监听
        mLocationClient.registerLocationListener(mLocationListener);
        //配置定位
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");//坐标类型
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//打开Gps
        option.setScanSpan(1000);//1000毫秒定位一次
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onStart() {
        super.onStart();
        //开启定位
        mBaiduMap.setMyLocationEnabled(true);
        if(!mLocationClient.isStarted()){
            mLocationClient.start();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        //关闭定位
        mBaiduMap.setMyLocationEnabled(false);
        if(mLocationClient.isStarted()){
            mLocationClient.stop();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
        isRefresh = true;
    }

    //显示消息
    private void showInfo(String str){
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }

    //显示中心点marker
    public void addCenterOverlay(LatLng location) {
        //清空地图
        mBaiduMap.clear();
        //创建marker的显示图标
//        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.home_zb);
//        Marker marker;
//        OverlayOptions options;
//            //获取经纬度
//            //设置marker
//            options = new MarkerOptions()
//                    .position(location)//设置位置
////                    .icon(bitmap)//设置图标样式
//                    .zIndex(9) // 设置marker所在层级
//                    .draggable(true); // 设置手势拖拽;
//            //添加marker
//            marker = (Marker) mBaiduMap.addOverlay(options);
            //使用marker携带info信息，当点击事件的时候可以通过marker获得info信息
            Bundle bundle = new Bundle();
            //info必须实现序列化接口
//            bundle.putSerializable("info", info);
//            marker.setExtraInfo(bundle);
        //将地图显示在最后一个marker的位置
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(location);
        mBaiduMap.setMapStatus(msu);
        //添加marker点击事件的监听
       /* mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //从marker中获取info信息
                Bundle bundle = marker.getExtraInfo();
                MarkerInfoUtil infoUtil = (MarkerInfoUtil) bundle.getSerializable("info");
                //将信息显示在界面上
                ImageView iv_img = (ImageView)markerRl.findViewById(R.id.iv_img);
                iv_img.setBackgroundResource(infoUtil.getImgId());
                TextView tv_name = (TextView)markerRl.findViewById(R.id.tv_name);
                tv_name.setText(infoUtil.getName());
                TextView tv_description = (TextView)markerRl.findViewById(R.id.tv_description);
                tv_description.setText(infoUtil.getDescription());
                //将布局显示出来
                markerRl.setVisibility(View.VISIBLE);

                //infowindow中的布局
                TextView tv = new TextView(getActivity());
                tv.setBackgroundResource(R.drawable.jifenkuang);
                tv.setPadding(20, 10, 20, 20);
                tv.setTextColor(android.graphics.Color.WHITE);
                tv.setText(infoUtil.getName());
                tv.setGravity(Gravity.CENTER);
                bitmapDescriptor = BitmapDescriptorFactory.fromView(tv);
                //infowindow位置
                LatLng latLng = new LatLng(infoUtil.getLatitude(), infoUtil.getLongitude());
                //infowindow点击事件
                InfoWindow.OnInfoWindowClickListener listener = new InfoWindow.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick() {
                        //隐藏infowindow
                        mBaiduMap.hideInfoWindow();
                    }
                };
                //显示infowindow
                InfoWindow infoWindow = new InfoWindow(bitmapDescriptor, latLng, -47, listener);
                mBaiduMap.showInfoWindow(infoWindow);
                return true;
            }
        });*/
    }
    //显示marker
    private void addOverlay(final List<MarkerInfoUtil> infos) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        //清空地图
//        mBaiduMap.clear();
        //创建marker的显示图标
        bitmap = BitmapDescriptorFactory.fromResource(R.drawable.home_wd);
        selectBitmap = BitmapDescriptorFactory.fromResource(R.drawable.home_wd_pre);
        LatLng latLng = null;
        Marker marker;
        OverlayOptions options;
        for(MarkerInfoUtil info:infos){
            //获取经纬度
            latLng = new LatLng(info.latitude,info.longitude);
            //设置marker
            if(info.isSelect){
                options = new MarkerOptions()
                        .position(latLng)//设置位置
                        .icon(selectBitmap)//设置图标样式
                        .zIndex(9) // 设置marker所在层级
                        .draggable(false); // 设置手势拖拽;
                marker = (Marker) mBaiduMap.addOverlay(options);
                selectMarker = marker;
                showLayout(info);
            }else {
                options = new MarkerOptions()
                        .position(latLng)//设置位置
                        .icon(bitmap)//设置图标样式
                        .zIndex(9) // 设置marker所在层级
                        .draggable(false); // 设置手势拖拽;
                marker = (Marker) mBaiduMap.addOverlay(options);
            }
//            //添加marker
//            marker = (Marker) mBaiduMap.addOverlay(options);
            //使用marker携带info信息，当点击事件的时候可以通过marker获得info信息
            Bundle bundle = new Bundle();
            //info必须实现序列化接口
            bundle.putSerializable("info", info);
            marker.setExtraInfo(bundle);
            builder.include(latLng);
        }
        //显示地图可视范围内所有marker
//        mBaiduMap.setMapStatus(MapStatusUpdateFactory
//                .newLatLngBounds(builder.build()));

        //将地图显示在最后一个marker的位置
//        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
//        mBaiduMap.setMapStatus(msu);
        //添加marker点击事件的监听
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for(MarkerInfoUtil info:infos){
                    info.isSelect = false;
                }
                if(selectMarker != null){
                    selectMarker.setIcon(bitmap);
                }
                if(selectInfo != null){
                    selectInfo.isSelect = false;
                }
                marker.setIcon(selectBitmap);
                selectMarker = marker;
                //从marker中获取info信息
                Bundle bundle = marker.getExtraInfo();
                MarkerInfoUtil infoUtil = (MarkerInfoUtil) bundle.getSerializable("info");
                selectInfo = infoUtil;
                infoUtil.isSelect = true;
                //将信息显示在界面上
                showLayout(infoUtil);

                //infowindow中的布局
                TextView tv = new TextView(getActivity());
                tv.setBackgroundResource(R.drawable.jifenkuang);
                tv.setPadding(20, 10, 20, 20);
                tv.setTextColor(android.graphics.Color.WHITE);
                tv.setText(infoUtil.name);
                tv.setGravity(Gravity.CENTER);
                bitmapDescriptor = BitmapDescriptorFactory.fromView(tv);
                //infowindow位置
                LatLng latLng = new LatLng(infoUtil.latitude, infoUtil.longitude);
                //infowindow点击事件
                InfoWindow.OnInfoWindowClickListener listener = new InfoWindow.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick() {
                        //隐藏infowindow
                        mBaiduMap.hideInfoWindow();
                    }
                };
                //显示infowindow
                InfoWindow infoWindow = new InfoWindow(bitmapDescriptor, latLng, -147, listener);
//                mBaiduMap.showInfoWindow(infoWindow);
                return true;
            }
        });
    }

    void showLayout(final MarkerInfoUtil infoUtil){
        markerRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShopDetailActivity.class);
                intent.putExtra(Constants.KEY_SHOP_ID,infoUtil.shopId);
                intent.putExtra(Constants.KEY_IS_FREE,instance.isFree);
                intent.putExtra(Constants.KEY_SERVICE_TYPE,instance.serviceType);
                intent.putExtra(Constants.KEY_RB_USER,instance.isRb);
                startActivity(intent);
            }
        });
        RelativeLayout navLayout = (RelativeLayout) markerRl.findViewById(R.id.branch_nav_layout);
        navLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        /*try {
                            Uri uri = Uri.parse(String.format("geo:%1$s,%2$s", infoUtil.latitude, infoUtil.longitude));
                            Intent it = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(it);
                        }catch (Exception ex){
                            Toast.makeText(getActivity(), "请安装地图客户端",
                                    Toast.LENGTH_SHORT).show();
                        }*/
                if(LocateManager.isAvilible(getActivity(),"com.baidu.BaiduMap") || LocateManager.isAvilible(getActivity(),
                        "com.autonavi.minimap") || LocateManager.isAvilible(getActivity(),"com.tencent.map")) {
                    loadMap(infoUtil);
                }else {
                    UIUtils.toast("请安装地图客户端");
                }
            }
        });
        TextView tv_name = (TextView)markerRl.findViewById(R.id.shop_name);
        tv_name.setText(infoUtil.name);
        TextView tv_addr = (TextView)markerRl.findViewById(R.id.shop_addr);
        tv_addr.setText(infoUtil.addr);
        ImageView shopIcon = (ImageView) markerRl.findViewById(R.id.shop_icon);
        ImageView washIcon = (ImageView) markerRl.findViewById(R.id.wash_icon);
        ImageView rinseIcon = (ImageView) markerRl.findViewById(R.id.rinse_icon);
        ImageView filmIcon = (ImageView) markerRl.findViewById(R.id.film_icon);
        if(TextUtils.isEmpty(infoUtil.imgUrl)){
            shopIcon.setImageResource(R.drawable.shop_default);
        }else {
            Picasso.with(getActivity())
                    .load(String.format(getString(R.string.img_url),infoUtil.imgUrl))
                    .resize(DensityUtil.dip2px(getActivity(),70),DensityUtil.dip2px(getActivity(),70))
                    .centerCrop()
                    .placeholder(R.drawable.order_default)
                    .error(R.drawable.order_default).into(shopIcon);
        }

        TextView orderTv = (TextView) markerRl.findViewById(R.id.tv_order);
        orderTv.setText(String.format(getString(R.string.order),infoUtil.orderNum));
        TextView distanceTv = (TextView) markerRl.findViewById(R.id.tv_distance);
        distanceTv.setText(String.format(getString(R.string.distance),infoUtil.distance));
        TextView commentTv = (TextView) markerRl.findViewById(R.id.tv_comment);
        commentTv.setText(String.format(getString(R.string.comment),infoUtil.star));
        for(int i = 0;i < infoUtil.serviceEntitys.size();i++){
            if(infoUtil.serviceEntitys.get(i).serviceType == 1){
                washIcon.setVisibility(View.VISIBLE);
                break;
            }else {
                washIcon.setVisibility(View.GONE);
            }
        }
        for(int i = 0;i < infoUtil.serviceEntitys.size();i++){
            if(infoUtil.serviceEntitys.get(i).serviceType == 2){
                filmIcon.setVisibility(View.VISIBLE);
                break;
            }else {
                filmIcon.setVisibility(View.GONE);
            }
        }
        for(int i = 0;i < infoUtil.serviceEntitys.size();i++){
            if(infoUtil.serviceEntitys.get(i).serviceType == 4){
                rinseIcon.setVisibility(View.VISIBLE);
                break;
            }else {
                rinseIcon.setVisibility(View.GONE);
            }
        }


//        TextView tv_dolly = (TextView) markerRl.findViewById(R.id.tv_dolly);
//        tv_dolly.setText(String.format(getString(R.string.dollty_text),infoUtil.dolly,infoUtil.smallcarcount));
//        TextView tv_cart = (TextView) markerRl.findViewById(R.id.tv_cart);
//        tv_cart.setText(String.format(getString(R.string.cart_text),infoUtil.cart,infoUtil.bigcarcount));

        //将布局显示出来
        markerRl.setVisibility(View.VISIBLE);
    }

    void loadMap(final MarkerInfoUtil infoUtil){
        List<MapEntity> mapList = new ArrayList<MapEntity>();
        if(LocateManager.isAvilible(getActivity(),"com.baidu.BaiduMap")){
            MapEntity entity = new MapEntity();
            entity.mapName = "百度地图";
            entity.mapType = 0;
            mapList.add(entity);
        }
        if(LocateManager.isAvilible(getActivity(),"com.autonavi.minimap")){
            MapEntity entity = new MapEntity();
            entity.mapName = "高德地图";
            entity.mapType = 1;
            mapList.add(entity);
        }
        if(LocateManager.isAvilible(getActivity(),"com.tencent.map")){
            MapEntity entity = new MapEntity();
            entity.mapName = "腾讯地图";
            entity.mapType = 2;
            mapList.add(entity);
        }
        Dialog dialog = UIUtils.alertButtonListBottom(getActivity(),"选择地图导航",false);
        Window dialogWindow = dialog.getWindow();
        Display display = getActivity().getWindowManager().getDefaultDisplay();
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


    //自定义的定位监听
    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //将获取的location信息给百度map
            MyLocationData data = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            mBaiduMap.setMyLocationData(data);
            if(isFirstLocation){
                if("131".equals(location.getCityCode())) {
                    //获取经纬度
                    LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                    MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(ll);
                    //mBaiduMap.setMapStatus(status);//直接到中间
                    mBaiduMap.animateMapStatus(status);//动画的方式到中间
                    isFirstLocation = false;
                    if (!TextUtils.isEmpty(location.getCity())) {
                        latLng = ll;
                        instance.refreCity(location.getCity().replace("市", ""));
                    addCenterOverlay(new LatLng(location.getLatitude(),location.getLongitude()));
                    getShoop(location.getLatitude(),location.getLongitude());
//                    getShoop(location.getLatitude(),location.getLongitude());
                    }
                }else {
                    LatLng ll = new LatLng(39.089478, 117.207137);
                    latLng = ll;
                    if(!TextUtils.isEmpty(location.getCity())) {
                        instance.refreCity(location.getCity().replace("市", ""));
                    }
                    MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(ll);
                    mBaiduMap.animateMapStatus(status);//动画的方式到中间
                    isFirstLocation = false;
                    showConfirmDialog("温馨提示","暂时还没有开通您所在的城市哦");
                }
            }
        }

    }

    // 显示更新程序对话框，供主程序调用
    public void showConfirmDialog(String title,String remarks){
        final Dialog dialog = new Dialog(getActivity(),R.style.loading_dialog);
        dialog.setContentView(R.layout.ui_alert_dialog);
        Window dialogWindow = dialog.getWindow();
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = -80;
        lp.width = (int)(display.getWidth() * 0.95);
        dialogWindow.setAttributes(lp);

        TextView alertTitle = (TextView) dialog.findViewById(R.id.alert_title);
        TextView alertTip = (TextView) dialog.findViewById(R.id.alert_tip);
        TextView alertOk = (TextView) dialog.findViewById(R.id.alert_ok);
        TextView alertCancle = (TextView) dialog.findViewById(R.id.alert_cancel);
        alertTitle.setText(title);
        alertTip.setText(remarks);
        alertOk.setText("确定");
        alertCancle.setText("取消");
        alertCancle.setVisibility(View.INVISIBLE);
        dialog.findViewById(R.id.alert_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveBeiJing();
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.alert_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    //如果定位到非北京，把光标移入北京
    void moveBeiJing(){
        LatLng ll = new LatLng(39.915049, 116.403958);
        MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(ll);
        //mBaiduMap.setMapStatus(status);//直接到中间
        mBaiduMap.animateMapStatus(status);//动画的方式到中间
        isFirstLocation = false;
        latLng = ll;
        instance.refreCity("北京");
        addCenterOverlay(ll);
        getShoop(39.915049, 116.403958);
    }

    public void getShoop(double lat , double lng){
        withBtwVolley().load(API.API_GET_AMOUNT_LIST)
                .setHeader("Accept", API.VERSION)
                .setParam("lng",lng)
                .setParam("lat",lat)
                .setParam("is_rb",instance.isRb)
                .method(Request.Method.GET)
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(20000)
                .setResponseHandler(new BtwVolley.ResponseHandler<MarkerInfoModel>() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onResponse(MarkerInfoModel resp) {
                        markerRl.setVisibility(View.GONE);
                        if(!resp.data.isEmpty()){
                            instance.setShops(resp.data);
                            addOverlay(resp.data);

                        }

                    }

                    @Override
                    public void onBtwError(BtwRespError<MarkerInfoModel> error) {
                        UIUtils.toast(error.errorMessage);
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        UIUtils.toast(getString(R.string.network_error));
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(MarkerInfoModel.class);
    }
}
