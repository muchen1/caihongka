package com.rainbowcard.client.ui;


import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.search.core.PoiInfo;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBasicActivity;
import com.rainbowcard.client.base.YHApplication;
import com.rainbowcard.client.common.city.CityEntity;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.BannerEntity;
import com.rainbowcard.client.model.BannerModel;
import com.rainbowcard.client.model.MarkerInfoUtil;
import com.rainbowcard.client.model.MessageEntity;
import com.rainbowcard.client.model.PopUpModel;
import com.rainbowcard.client.model.TokenModel;
import com.rainbowcard.client.model.UpdateModel;
import com.rainbowcard.client.ui.adapter.GridViewAdapter;
import com.rainbowcard.client.ui.adapter.ViewPagerAdapter;
import com.rainbowcard.client.ui.fragment.HomeFragment;
import com.rainbowcard.client.ui.fragment.ListFragment;
import com.rainbowcard.client.ui.fragment.MapFragment;
import com.rainbowcard.client.ui.fragment.CaihongkaFragment;
import com.rainbowcard.client.ui.fragment.PersonalFragment;
import com.rainbowcard.client.utils.DensityUtil;
import com.rainbowcard.client.utils.LoginControl;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.SimpleFragmentSwitcher;
import com.rainbowcard.client.utils.SystemUtils;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.utils.UpdateManger;
import com.rainbowcard.client.widget.BottomControlPanel;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.widget.adlibrary.AdConstant;
import com.rainbowcard.client.widget.adlibrary.AdManager;
import com.rainbowcard.client.widget.adlibrary.bean.AdInfo;
import com.rainbowcard.client.widget.adlibrary.transformer.RotateDownPageTransformer;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2016/4/14.
 */
public class MainActivity extends MyBasicActivity implements BottomControlPanel.BottomPanelCallback{

    @InjectView(R.id.bottom_layout)
    BottomControlPanel mBottomControlPanel;
    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
//    @InjectView(R.id.indicator)
//    IOSTabIndicator mIOSTabIndicator;
    @InjectView(R.id.search_layout)
    RelativeLayout searchBtn;
    @InjectView(R.id.nav_back)
    RelativeLayout barckLayout;
    @InjectView(R.id.iv_back)
    ImageView backIv;
    @InjectView(R.id.city_title)
    TextView cityTv;
    @InjectView(R.id.right_layout)
    LinearLayout rightLayout;
    @InjectView(R.id.right_image)
    ImageView rightIv;

    private FragmentManager mFagmentManager;
    private FragmentTransaction mFragmentTransaction;
    private SimpleFragmentSwitcher mFragmentSwitcher;
    private HomeFragment mHomeFragment;
//    public MapFragment mMapFragment;
    private ListFragment mListFragment;
    private CaihongkaFragment mCaihongkaFragment;
    private PersonalFragment mPersonalFragment;

    public static MainActivity instance = null;
    public List<MarkerInfoUtil> infos = new ArrayList<MarkerInfoUtil>();
    public List<BannerEntity> bannerEntities = new ArrayList<BannerEntity>();

    //定位相关
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;

    public double lat = 39.915049;
    public double lng = 116.403958;
    public String cityId = "1";

    public String province;
    public String city;
    private boolean isLocation = true;
    private boolean isCitySupport = false;

    private ArrayList<CityEntity> supportCitys = new ArrayList<CityEntity>();

    public static MainActivity getInstance() {
        return instance;
    }


    //插屏
    private SharedPreferences mSharedPreferences;
    boolean isAd = true;

    public boolean isPersonal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        ButterKnife.inject(this);
        supportCitys = (ArrayList<CityEntity>) getIntent().getSerializableExtra(Constants.KEY_CITYS);

        mSharedPreferences = getSharedPreferences(Constants.SP_SCREEN_AD, 0);
        checkUpdate();
        initLocation();

        initUI();
        mFagmentManager = getFragmentManager();
        mFragmentTransaction = mFagmentManager.beginTransaction();
        setDefaultFirstFragment(Constants.FRAGMENT_FLAG_MAP);

        setTabSelection(0);

//        onBottomPanelClick(4);


        Bundle bundle = getIntent().getBundleExtra(Constants.EXTRA_BUNDLE);
        if(bundle != null){
            Log.d("GCCCCC","jinlaiil");
            //如果bundle存在，取出其中的参数，启动DetailActivity
//            String name = bundle.getString("name");
//            String price = bundle.getString("price");
            int type = Integer.valueOf(bundle.getString(Constants.MESSAGE_TYPE));
            Intent intent;
            if(type == 100){
                intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }else {
                intent = new Intent(MainActivity.this,MessageActivity.class);
                intent.putExtra(Constants.MESSAGE_TYPE,type);
                startActivity(intent);
            }
            /*switch (type){
                case 1:
                    intent = new Intent(MainActivity.this,SurprisedActivity.class);
                    intent.putExtra(Constants.KEY_URL,bundle.getString(Constants.KEY_URL));
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                case 2:
                    intent = new Intent(MainActivity.this, MyDiscountActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                case 3:
                    intent = new Intent(MainActivity.this,MessageDetailActivity.class);
                    intent.putExtra(Constants.KEY_CONTENT,bundle.getString(Constants.KEY_CONTENT));
                    startActivity(intent);
                    break;
                default:
                    intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
            }*/
//            Intent intent = new Intent(MainActivity.this,SurprisedActivity.class);
//            intent.putExtra(Constants.KEY_URL,"http://www.baidu.com");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
        }
//        checkUpdate();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        isPersonal = intent.getBooleanExtra(Constants.KEY_IS_PERSONAL,false);
        if (isPersonal) {
            setTabSelection(2);
//            onBottomPanelClick(4);
            mBottomControlPanel.setDefaultBtnChecked(2);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //开启定位
        if(!mLocationClient.isStarted()){
            mLocationClient.start();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        //关闭定位
        if(mLocationClient.isStarted()){
            mLocationClient.stop();
        }
    }

    public void setmMapFragment(){
//        mIOSTabIndicator.select(0);
        setTabSelection(0);
    }

//    void checkUpdate(){
//        if(YHApplication.instance().getVersionCode() < YHApplication.instance().getLatestVersion()
//                && YHApplication.instance().getVersionCode() > YHApplication.instance().getLowestVersion()){
//            new UpdateManger(MainActivity.this).checkUpdateInfo(false);
//        }
//        if(YHApplication.instance().getVersionCode() < YHApplication.instance().getLowestVersion()
//                && YHApplication.instance().getVersionCode() < YHApplication.instance().getLatestVersion()){
//            new UpdateManger(MainActivity.this).checkUpdateInfo(true);
//        }
//    }

    void initUI(){
//        mIOSTabIndicator.addButtons(getString(R.string.map),
//                getString(R.string.list));
        if(mBottomControlPanel != null){
            mBottomControlPanel.initBottomPanel();
            mBottomControlPanel.setBottomCallback(this);
        }
        if(mHeadControlPanel != null){
            mHeadControlPanel.initHeadPanel();
        }
        rightIv.setImageResource(R.drawable.nav_message);
        rightIv.setPadding(0,0,0,2);
        rightIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (LoginControl.getInstance(MainActivity.this).isLogin()) {
                    intent = new Intent(MainActivity.this, MessageActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
//                Intent intent = new Intent(MainActivity.this, KeyWordsAct.class);
//                startActivityForResult(intent,Constants.REQUEST_KEYWORD);
            }
        });
        cityTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SelectCityActivity.class);
                startActivityForResult(intent,Constants.REQUEST_CITY);
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchBranchActivity.class);
                intent.putExtra(Constants.KEY_CITY_ID,cityId);
                intent.putExtra(Constants.KEY_LAT,lat);
                intent.putExtra(Constants.KEY_LNG,lng);
                startActivity(intent);
            }
        });
        /*mIOSTabIndicator.setOnTabClickListener(new IOSTabIndicator.OnTabClickListener() {
            @Override
            public Boolean onClick(int pos, Button button) {
                switch (pos) {
                    case 0:
                        setTabSelection(0);
                        break;
                    case 1:
                        setTabSelection(3);
                        break;
                }
                return true;
            }
        });*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_KEYWORD && data != null) {
//            setmMapFragment();
            PoiInfo poiInfo = data.getParcelableExtra(Constants.KEY_POIINFO);
//            mMapFragment.isRefresh = true;
//            mMapFragment.latLng = poiInfo.location;
//            mMapFragment.addCenterOverlay(mMapFragment.latLng);
//            mMapFragment.getShoop(poiInfo.location.latitude,poiInfo.location.longitude);
            UIUtils.toast(poiInfo.address);
        }
        if(requestCode == Constants.REQUEST_CITY && data != null){
            CityEntity cityEntity = (CityEntity) data.getSerializableExtra(Constants.KEY_CITYENTITY);
            cityTv.setText(cityEntity.name);
            cityId = String.valueOf(cityEntity.id);
//            mHomeFragment.getShoop(lat,lng,cityId);
            isLocation = false;

        }

    }

    @Override
    public void onBottomPanelClick(int itemId) {
        String tag = "";
        int page = 0;
        if((itemId & Constants.BTN_FLAG_MAP) != 0){
            tag = Constants.FRAGMENT_FLAG_MAP;
            page =  0;
            searchBtn.setVisibility(View.VISIBLE);
//            mIOSTabIndicator.setVisibility(View.VISIBLE);
//            barckLayout.setVisibility(View.VISIBLE);
//            backIv.setVisibility(View.GONE);
//            cityTv.setVisibility(View.VISIBLE);

//            mIOSTabIndicator.select(0);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(MainActivity.this,65));
            mHeadControlPanel.setLayoutParams(layoutParams);
        }else if((itemId & Constants.BTN_FLAG_GEREN) != 0){
            tag = Constants.FRAGMENT_FLAG_GEREN;
            page = 1;
            searchBtn.setVisibility(View.GONE);
//            mIOSTabIndicator.setVisibility(View.GONE);
            barckLayout.setVisibility(View.INVISIBLE);
            rightLayout.setVisibility(View.INVISIBLE);
//            mIOSTabIndicator.select(0);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(MainActivity.this,65));
            mHeadControlPanel.setLayoutParams(layoutParams);
        }else if((itemId & Constants.BTN_FLAG_SET) != 0){
            tag = Constants.FRAGMENT_FLAG_SET;
            page = 2;
            searchBtn.setVisibility(View.GONE);
//            mIOSTabIndicator.setVisibility(View.GONE);
            barckLayout.setVisibility(View.INVISIBLE);
            rightLayout.setVisibility(View.INVISIBLE);
//            mIOSTabIndicator.select(0);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(MainActivity.this,0));
            mHeadControlPanel.setLayoutParams(layoutParams);
        }
        setTabSelection(page); //切换Fragment
        mHeadControlPanel.setMiddleTitle(tag);//切换标题
    }

    public void refreCity(String city){
        cityTv.setText(city);
    }

    public void setShops(ArrayList<MarkerInfoUtil> list){
        infos.clear();
        infos.addAll(list);
    }

    public void setBannerEntities(ArrayList<BannerEntity> list){
        bannerEntities.clear();
        bannerEntities.addAll(list);
    }


    private void setDefaultFirstFragment(String tag){
//        setTabSelection(tag);
        mBottomControlPanel.defaultBtnChecked();
    }
    public void setTabSelection(int index) {

        Log.d("GCCCCCCCCCCC","zhelishishenme"+index);
        // 每次选中之前先清楚掉上次的选中状态
//        clearSelection();
        // 开启一个Fragment事务
        FragmentTransaction transaction = mFagmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
//        hideFragments(transaction);
        switch (index) {
            case 0:
                barckLayout.setVisibility(View.VISIBLE);
                backIv.setVisibility(View.GONE);
                cityTv.setVisibility(View.VISIBLE);
                rightLayout.setVisibility(View.VISIBLE);
                rightIv.setVisibility(View.VISIBLE);
                searchBtn.setVisibility(View.VISIBLE);
                if (mHomeFragment == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    mHomeFragment = HomeFragment.newInstance();
                }
                transaction.replace(R.id.fragment_content, mHomeFragment);
                transaction.addToBackStack(null);
                break;
            case 1:
                if (mCaihongkaFragment == null) {
                    // 如果ContactsFragment为空，则创建一个并添加到界面上
                    mCaihongkaFragment = CaihongkaFragment.newInstance().newInstance();
                }
                transaction.replace(R.id.fragment_content, mCaihongkaFragment);
                transaction.addToBackStack(null);
                break;
            case 2:
                if (mPersonalFragment == null) {
                    // 如果NewsFragment为空，则创建一个并添加到界面上
                    mPersonalFragment = PersonalFragment.newInstance();
                }
                transaction.replace(R.id.fragment_content, mPersonalFragment);
                transaction.addToBackStack(null);
                break;
            case 3:
                if(mListFragment == null){
                    mListFragment = ListFragment.newInstance();
                }
                transaction.replace(R.id.fragment_content,mListFragment);
                transaction.addToBackStack(null);
                break;

        }
        transaction.commitAllowingStateLoss();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (mHomeFragment != null) {
            transaction.hide(mHomeFragment);
        }
        if (mCaihongkaFragment != null) {
            transaction.hide(mCaihongkaFragment);
        }
        if (mPersonalFragment != null) {
            transaction.hide(mPersonalFragment);
        }

    }

    private void changeFragment(int pos) {
        mFragmentSwitcher.showFragment(R.id.fragment_content, pos);
    }

    private void setFragments() {
        mFragmentSwitcher = new SimpleFragmentSwitcher(getFragmentManager()) {
            @Override
            public Fragment getItem(int pos) {
                switch (pos) {
                    case 0:
                        return MapFragment.newInstance();
                    case 1:
                        return CaihongkaFragment.newInstance();
                    case 2:
                        return PersonalFragment.newInstance();
                    default:
                        return null;
                }
            }
        };
        changeFragment(2);

    }

    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                UIUtils.toast("再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initLocation() {
        //定位客户端的设置
        mLocationClient = new LocationClient(MainActivity.this);
        mLocationListener = new MyLocationListener();
        //注册监听
        mLocationClient.registerLocationListener(mLocationListener);
        //配置定位
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");//坐标类型
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//打开Gps
        option.setScanSpan(360000);//1000毫秒定位一次
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mLocationClient.setLocOption(option);
    }

    //自定义的定位监听
    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            /*if(!"131".equals(location.getCityCode())){
//                showConfirmDialog("温馨提示","暂时还没有开通您所在的城市哦");
                cityTv.setText("北京");
            }else {
                cityTv.setText(location.getCity().replace("市", ""));
                lat = location.getLatitude();
                lng = location.getLongitude();
            }*/
            if(isLocation) {
                if(!TextUtils.isEmpty(location.getCity())) {
                    province = location.getProvince();
                    city = location.getCity();
                    if(supportCitys == null||supportCitys.size() < 1) {
                        setBeiJing();
                    }else {
                        for (CityEntity cityEntity : supportCitys) {
                            if (location.getCity().contains(cityEntity.name)) {
                                cityTv.setText(cityEntity.name);
                                cityId = String.valueOf(cityEntity.id);
                                lat = location.getLatitude();
                                lng = location.getLongitude();
                                isCitySupport = true;
                                Log.d("GCCCCCCCCC","chaogededingwei  lat:"+lat+"lng:"+lng);
                                mHomeFragment.getBanner(cityId);
                                if(isAd) {
                                    getPopUpWindows(cityId);
                                    isAd = false;
                                }
                                break;
                            } else {
                                isCitySupport = false;
                            }
                        }
                        if (!isCitySupport) {
                            showConfirmDialog("温馨提示", "暂时还没有开通您所在的城市哦,是否定位到北京");
                        }
                    }
                }else {
//                    UIUtils.toast("请开启定位");
//                    setBeiJing();
                    showConfirmDialog("温馨提示", "您没有开启定位,是否先定位到北京");
                }
            }
        }

    }

    public void showConfirmDialog(String title,String remarks){
        final Dialog dialog = new Dialog(MainActivity.this,R.style.loading_dialog);
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
        alertTip.setText(remarks);
        alertOk.setText("是");
        alertCancle.setText("否");
        alertCancle.setVisibility(View.INVISIBLE);
        dialog.findViewById(R.id.alert_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBeiJing();
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

    void setBeiJing(){
        cityTv.setText("北京");
        cityId = "1";
        lat = 39.915049;
        lng = 116.403958;
    }


    //检测更新
    public void checkUpdate(){
        withBtwVolley().load(API.API_CHECK_UPDATE)
                .method(Request.Method.GET)
                .setHeader("Accept", API.VERSION)
                .setParam("version", YHApplication.instance().getVersionName())
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
                            new UpdateManger(MainActivity.this,resp.data.url).showConfirmDialog("温馨提示",resp.data.remarks);
                            MyConfig.putSharePre(MainActivity.this, Constants.USERINFO, Constants.DOWNLOAD_APK_URL, resp.data.url);
                            MyConfig.putSharePre(MainActivity.this, Constants.USERINFO, Constants.IS_DOWNLOAD, resp.data.must);
                            MyConfig.putSharePre(MainActivity.this, Constants.USERINFO, Constants.UPDATE_REMARKS, resp.data.remarks);
                        }else {
                            if(resp.data.update){
                                new UpdateManger(MainActivity.this,resp.data.url).showDialog("温馨提示",resp.data.remarks);
                            }else {
//                                UIUtils.toast("当前已是最新版本");
                                MyConfig.putSharePre(MainActivity.this, Constants.USERINFO, Constants.IS_DOWNLOAD, resp.data.must);
                            }
                        }
                    }

                    @Override
                    public void onBtwError(BtwRespError<UpdateModel> error) {
//                        Toast.makeText(MainActivity.this, error.errorMessage,
//                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
//                        Toast.makeText(MainActivity.this, R.string.network_error,
//                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(UpdateModel.class);
    }

    public void getPopUpWindows(String cityId){
        withBtwVolley().load(API.API_GET_POPUP_WINDOWS)
                .setHeader("Accept", API.VERSION)
                .setParam("city_id",cityId)
                .method(Request.Method.GET)
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(20000)
                .setResponseHandler(new BtwVolley.ResponseHandler<PopUpModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
//                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(PopUpModel resp) {
                        if(!resp.data.isEmpty()) {

                            if(isPopUp()) {
                                final AdManager adManager = new AdManager(MainActivity.this, resp.data);
                                adManager.setOnImageClickListener(new AdManager.OnImageClickListener() {
                                    @Override
                                    public void onImageClick(View view, BannerEntity bannerEntity) {
                                        Intent intent;
                                        switch (bannerEntity.type) {
                                            case 1:
                                                intent = new Intent(MainActivity.this, SurprisedActivity.class);
                                                intent.putExtra(Constants.KEY_URL, bannerEntity.url);
                                                startActivity(intent);
                                                break;
                                            case 2:
                                                if (LoginControl.getInstance(MainActivity.this).isLogin()) {
                                                    intent = new Intent(MainActivity.this, RechargeAccountActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    intent = new Intent(MainActivity.this, LoginActivity.class);
                                                    startActivity(intent);
                                                }
                                                break;
                                            case 3:
                                                gotoBranchList(1);
                                                break;
                                            case 4:
                                                gotoBranchList(2);
                                                break;
                                            case 5:

                                                break;
                                            case 6:
                                                gotoBranchList(4);
                                                break;
                                            case 7:
                                                if (LoginControl.getInstance(MainActivity.this).isLogin()) {
                                                    refreshToken(bannerEntity.url,7);
                                                } else {
                                                    intent = new Intent(MainActivity.this, LoginActivity.class);
                                                    startActivity(intent);
                                                }
                                                break;
                                            case 8:
                                                if (LoginControl.getInstance(MainActivity.this).isLogin()) {
                                                    intent = new Intent(MainActivity.this, BindCardActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    intent = new Intent(MainActivity.this, LoginActivity.class);
                                                    startActivity(intent);
                                                }
                                                break;
                                            case 9:
                                                if (LoginControl.getInstance(MainActivity.this).isLogin()) {
                                                    intent = new Intent(MainActivity.this, MyDiscountActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    intent = new Intent(MainActivity.this, LoginActivity.class);
                                                    startActivity(intent);
                                                }
                                                break;
                                            case 10:
                                                if (LoginControl.getInstance(MainActivity.this).isLogin()) {
                                                    intent = new Intent(MainActivity.this, GoodsDetailActivity.class);
                                                    intent.putExtra(Constants.KEY_GOODS_ID,bannerEntity.content);
                                                    startActivity(intent);
                                                } else {
                                                    intent = new Intent(MainActivity.this, LoginActivity.class);
                                                    startActivity(intent);
                                                }
                                                break;
                                            case 11:
                                                if (LoginControl.getInstance(MainActivity.this).isLogin()) {
                                                    intent = new Intent(MainActivity.this, RechargeRainbowCardActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    intent = new Intent(MainActivity.this, LoginActivity.class);
                                                    startActivity(intent);
                                                }
                                                break;
                                            case 12:
                                                if (LoginControl.getInstance(MainActivity.this).isLogin()) {
                                                    refreshToken(bannerEntity.url,12);
                                                } else {
                                                    intent = new Intent(MainActivity.this, LoginActivity.class);
                                                    startActivity(intent);
                                                }
                                                break;
                                        }
                                        adManager.dismissAdDialog();
                                    }
                                })
                                        .setPageTransformer(new RotateDownPageTransformer())
                                        .showAdDialog(AdConstant.ANIM_UP_TO_DOWN);
                            }
                        }
                    }

                    @Override
                    public void onBtwError(BtwRespError<PopUpModel> error) {
                        UIUtils.toast(error.errorMessage);
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        UIUtils.toast(getString(R.string.network_error));
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(PopUpModel.class);
    }

    void gotoBranchList(int type){
        Intent intent = new Intent(MainActivity.this, BranchActivity.class);
        intent.putExtra(Constants.KEY_CITY_ID,instance.cityId);
        intent.putExtra(Constants.KEY_LAT,instance.lat);
        intent.putExtra(Constants.KEY_LNG,instance.lng);
        intent.putExtra(Constants.KEY_TYPE,type);
        startActivity(intent);
    }

    public void refreshToken(final String url,final int showType){
        withBtwVolley().load(API.API_REFRESH_TOKEN)
                .method(Request.Method.POST)
                .setHeader("Authorization",String.format(getString(R.string.token), MyConfig.getSharePreStr(MainActivity.this, Constants.USERINFO, Constants.UID)))
                .setHeader("Accept", API.VERSION)
                .setUIComponent(MainActivity.this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<TokenModel>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onResponse(TokenModel resp) {
                        MyConfig.putSharePre(MainActivity.this, Constants.USERINFO, Constants.UID, resp.data.token);
                        Intent intent;
                        if(showType == 12){
                            intent = new Intent(MainActivity.this, IntegralActivity.class);
                        }else {
                            intent = new Intent(MainActivity.this, SurprisedActivity.class);
                            intent.putExtra(Constants.KEY_PROVINCE,instance.province);
                            intent.putExtra(Constants.KEY_CITY,instance.city);
                            intent.putExtra(Constants.KEY_CITY_ID,instance.cityId);
                        }
                        intent.putExtra(Constants.KEY_URL,url);
                        startActivity(intent);
                    }

                    @Override
                    public void onBtwError(BtwRespError<TokenModel> error) {
                        Toast.makeText(MainActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(MainActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(TokenModel.class);
    }


    //判断是否插屏
    private boolean isPopUp(){
        Date dt=new Date();
        SimpleDateFormat matter=new SimpleDateFormat("yyyy-MM-dd");
        String popUpDate = matter.format(dt);

        if(TextUtils.isEmpty(mSharedPreferences.getString(Constants.SP_SCREEN_DATE,""))){
            mSharedPreferences.edit().putString(Constants.SP_SCREEN_DATE, popUpDate).commit();
            mSharedPreferences.edit().putInt(Constants.SP_SCREEN_COUNT,1).commit();
            return true;
        }else {
            if(popUpDate.equals(mSharedPreferences.getString(Constants.SP_SCREEN_DATE,""))){
                int count = mSharedPreferences.getInt(Constants.SP_SCREEN_COUNT,0);
                if(count < 2){
                    mSharedPreferences.edit().putInt(Constants.SP_SCREEN_COUNT,count + 1).commit();
                    return true;
                }else {
                    return false;
                }
            }else {
                mSharedPreferences.edit().putString(Constants.SP_SCREEN_DATE, popUpDate).commit();
                mSharedPreferences.edit().putInt(Constants.SP_SCREEN_COUNT,1).commit();
                return true;
            }
        }
    }
}
