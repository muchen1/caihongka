package com.rainbowcard.client.ui;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.search.core.PoiInfo;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.model.MarkerInfoUtil;
import com.rainbowcard.client.ui.adapter.ConstellationAdapter;
import com.rainbowcard.client.ui.adapter.RBUserAdapter;
import com.rainbowcard.client.ui.fragment.ListFragment;
import com.rainbowcard.client.ui.fragment.MapFragment;
import com.rainbowcard.client.utils.LoginControl;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.SimpleFragmentSwitcher;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2016-12-28.
 */
public class BranchActivity extends MyBaseActivity{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.iv_back)
    ImageView backIv;
    @InjectView(R.id.city_title)
    TextView cityTv;
    @InjectView(R.id.right_layout)
    LinearLayout rightLayout;
    @InjectView(R.id.right_image)
    ImageView rightIv;
    @InjectView(R.id.search_layout)
    RelativeLayout searchBtn;
    @InjectView(R.id.search_icon)
    ImageView searchIcon;
    @InjectView(R.id.search_text)
    TextView searchTv;

    boolean isList = true;

    public String cityId;
    public double lat;
    public double lng;
    public int serviceType;

    //是不是免费洗车券
    public boolean isFree;

    private FragmentManager mFagmentManager;
    private FragmentTransaction mFragmentTransaction;
    private SimpleFragmentSwitcher mFragmentSwitcher;
    public MapFragment mMapFragment;
    private ListFragment mListFragment;

    //定位相关
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;

    public static BranchActivity instance = null;
    public List<MarkerInfoUtil> infos = new ArrayList<MarkerInfoUtil>();

    public static BranchActivity getInstance() {
        return instance;
    }

    public int isRb = 0;
    private int userPosition = 0;
    private String user[] = {"彩虹卡用户洗车网点", "人保用户洗车网点"};
    private RBUserAdapter rbUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch);
        instance = this;
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(BranchActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(BranchActivity.this,true);
        cityId = getIntent().getStringExtra(Constants.KEY_CITY_ID);
        lat = getIntent().getDoubleExtra(Constants.KEY_LAT,0.0);
        lng = getIntent().getDoubleExtra(Constants.KEY_LNG,0.0);
        isFree = getIntent().getBooleanExtra(Constants.KEY_IS_FREE,false);
        serviceType = getIntent().getIntExtra(Constants.KEY_TYPE,1);
        initLocation();
        initUI();
        mFagmentManager = getFragmentManager();
        mFragmentTransaction = mFagmentManager.beginTransaction();
        setTabSelection(0);
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

    private void initLocation() {
        //定位客户端的设置
        mLocationClient = new LocationClient(BranchActivity.this);
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

    //自定义的定位监听
    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if(!"131".equals(location.getCityCode())){
            }else {
                lat = location.getLatitude();
                lng = location.getLongitude();
            }
        }

    }

    void initUI(){
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightLayout.setVisibility(View.VISIBLE);
        rightIv.setVisibility(View.VISIBLE);
        rightIv.setImageResource(R.drawable.nav_map);
        rightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isList){
                    setTabSelection(1);
                    rightIv.setImageResource(R.drawable.nav_list);
                    searchTv.setText(getString(R.string.search_addr));
                }else {
                    setTabSelection(0);
                    rightIv.setImageResource(R.drawable.nav_map);
                    searchTv.setText(getString(R.string.search_shop));
                }
                isList = !isList;
            }
        });
        searchBtn.setVisibility(View.VISIBLE);
        searchIcon.setImageResource(R.drawable.nav_search_gray);
        searchTv.setTextColor(getResources().getColor(R.color.app_gray));
        searchBtn.setBackgroundResource(R.drawable.bg_search_branch);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(!isList){
                    intent = new Intent(BranchActivity.this, KeyWordsAct.class);
                    startActivityForResult(intent,Constants.REQUEST_KEYWORD);
                }else {
                    intent = new Intent(BranchActivity.this,SearchBranchActivity.class);
                    intent.putExtra(Constants.KEY_CITY_ID,cityId);
                    intent.putExtra(Constants.KEY_LAT,lat);
                    intent.putExtra(Constants.KEY_LNG,lng);
                    startActivity(intent);
                }
            }
        });

        if(MyConfig.getSharePreIntRb(BranchActivity.this, Constants.USERINFO, Constants.KEY_IS_RB) != -1){
            isRb = MyConfig.getSharePreIntRb(BranchActivity.this, Constants.USERINFO, Constants.KEY_IS_RB);
        }

//        if(serviceType == 1 && MyConfig.getSharePreIntRb(BranchActivity.this, Constants.USERINFO, Constants.KEY_IS_RB) == -1){
//            showDialog();
//        }
        if(serviceType == 1 && MyConfig.getSharePreIntRb(BranchActivity.this, Constants.USERINFO, Constants.KEY_IS_RB) == -1){
            showDialog();
        }else if(!LoginControl.getInstance(BranchActivity.this).isLogin() && serviceType == 1){
            showDialog();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_KEYWORD && data != null) {
//            setmMapFragment();
            PoiInfo poiInfo = data.getParcelableExtra(Constants.KEY_POIINFO);
            mMapFragment.isRefresh = true;
            mMapFragment.latLng = poiInfo.location;
            mMapFragment.addCenterOverlay(mMapFragment.latLng);
            mMapFragment.getShoop(poiInfo.location.latitude,poiInfo.location.longitude);
            UIUtils.toast(poiInfo.address);
        }
    }

    public void setmMapFragment(){
        setTabSelection(0);
        isList = true;
    }

    public void refreCity(String city){
        cityTv.setText(city);
    }
    public void setShops(ArrayList<MarkerInfoUtil> list){
        infos.clear();
        infos.addAll(list);
    }

    public void setTabSelection(int index) {
        // 每次选中之前先清楚掉上次的选中状态
//        clearSelection();
        // 开启一个Fragment事务
        FragmentTransaction transaction = mFagmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
//        hideFragments(transaction);
        switch (index) {
            case 0:
                if (mListFragment == null) {
                    // 如果ContactsFragment为空，则创建一个并添加到界面上
                    mListFragment = ListFragment.newInstance();
                }
                transaction.replace(R.id.fragment_content, mListFragment);
                transaction.addToBackStack(null);
                break;
            case 1:
                if (mMapFragment == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    mMapFragment = MapFragment.newInstance();
                }
                transaction.replace(R.id.fragment_content, mMapFragment);
                transaction.addToBackStack(null);
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (mMapFragment != null) {
            transaction.hide(mMapFragment);
        }
        if (mListFragment != null) {
            transaction.hide(mListFragment);
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
                        return ListFragment.newInstance();
                    case 1:
                        return MapFragment.newInstance();
                    default:
                        return null;
                }
            }
        };
        changeFragment(2);

    }

    @Override
    public void onBackPressed() {
        //退出activity前关闭菜单
        if (mListFragment.mDropDownMenu.isShowing()) {
            mListFragment.mDropDownMenu.closeMenu();
        } else {
//            super.onBackPressed();
            finish();
        }
    }


    private void showDialog() {
        final Dialog dialog = new Dialog(BranchActivity.this,R.style.loading_dialog);
        dialog.setContentView(R.layout.ui_rb_alert);
        Window dialogWindow = dialog.getWindow();
        Display display = getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = -80;
        lp.width = (int)(display.getWidth() * 0.9);
        dialogWindow.setAttributes(lp);


        ListView constellation = (ListView) dialog.findViewById(R.id.constellation);
        rbUserAdapter = new RBUserAdapter(BranchActivity.this, Arrays.asList(user));
        constellation.setAdapter(rbUserAdapter);
        TextView ok = (TextView) dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userPosition == 0){
                    isRb = 0;
                }else {
                    isRb = 1;
                }
//                if(LoginControl.getInstance(BranchActivity.this).isLogin()) {
                    MyConfig.putSharePre(BranchActivity.this, Constants.USERINFO, Constants.KEY_IS_RB, isRb);
//                }
                mListFragment.getShop(isRb);
                dialog.dismiss();
            }
        });
        constellation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                rbUserAdapter.setCheckItem(position);
                userPosition = position;
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        dialog.show();
    }
}
