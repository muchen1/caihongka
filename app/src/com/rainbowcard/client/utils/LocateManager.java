package com.rainbowcard.client.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.rainbowcard.client.common.city.CityEntity;
import com.rainbowcard.client.common.city.LocateEntity;
import com.rainbowcard.client.common.utils.DLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kleist on 14-9-2.
 */
public class LocateManager {

    private static LocateManager mInstance;
    private Context mContext;
    private SparseArray<CityEntity> mCitySparseArray;
    private List<CityEntity> mCityList;
    private List<CityEntity> allCityList;
    private LocateEntity mCachedLocateEntity;

    private LocationClient mLocationClient;

    private LocateManager(Context context) {
        mContext = context.getApplicationContext();
        mCitySparseArray = new SparseArray<CityEntity>();
        mCityList = new ArrayList<CityEntity>();
        allCityList = new ArrayList<CityEntity>();

        mLocationClient = new LocationClient(mContext);
        LocationClientOption option = new LocationClientOption();
//        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        option.setCoorType("bd09ll");
        option.setOpenGps(true);//打开Gps
        option.setScanSpan(1000);
        option.setNeedDeviceDirect(false);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);

        init();
    }


    public static LocateManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (LocateManager.class) {
                if (mInstance == null) {
                    mInstance = new LocateManager(context);
                }
            }
        }
        return mInstance;
    }

    public CityEntity getCityByBaiduId(int baiduId) {
        if (mCitySparseArray == null || mCitySparseArray.size() == 0) {
            loadCityList();
        }
        for (CityEntity cityEntity : allCityList) {
            if (cityEntity.baiduId == baiduId) {
                return cityEntity;
            }
        }
        return mCitySparseArray.get(baiduId);
    }

    public CityEntity getCityById(int cityId) {
        if (mCityList == null || mCityList.isEmpty()) {
            loadCityList();
        }

        for (CityEntity cityEntity : mCityList) {
            if (cityEntity.id == cityId) {
                return cityEntity;
            }
        }

        return null;
    }

    public CityEntity getCachedCity() {
        return mCachedLocateEntity.city;
    }

    public List<CityEntity> getCityList() {
        loadCityList();
        return allCityList;
    }

    public List<CityEntity> getLocationCityList() {
        loadLocationCityList();
        return allCityList;
    }

    public void setCachedCity(CityEntity city) {
        mCachedLocateEntity = new LocateEntity();
        mCachedLocateEntity.city = city;
    }


    private List<CityEntity> parseCityList(SparseArray<CityEntity> sparseArray) {
        List<CityEntity> list = new ArrayList<CityEntity>(sparseArray.size());
        for (int i = 0, len = sparseArray.size(); i < len; i++) {
            list.add(sparseArray.valueAt(i));
        }

        return list;

    }

    public void locateWithCache(OnLocateListener listener) {
        if (mCachedLocateEntity != null) {
            listener.onLocate(mCachedLocateEntity);
        } else {
            locate(listener, true);
        }
    }

    public void locate(final OnLocateListener listener) {
        locate(listener, false);
    }

    public void locate(final OnLocateListener listener, final Boolean isSaveCache) {
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (bdLocation == null || TextUtils.isEmpty(bdLocation.getCityCode())) {
                    DLog.i("locate fail");
                    /*if (listener != null) {
                        CityEntity cityEntity = new CityEntity();
                        cityEntity.id = 28;
                        cityEntity.name = "北京";
                        LocateEntity locateEntity = new LocateEntity();
                        locateEntity.city = cityEntity;
                        if (isSaveCache) {
                            mCachedLocateEntity = locateEntity;
                        }
                        listener.onLocate(locateEntity);
                    }*/
                } else {
                    CityEntity city = getCityByBaiduId(Integer.valueOf(bdLocation.getCityCode()));
                    LocateEntity locateEntity = new LocateEntity();
                    locateEntity.city = city;
                    locateEntity.latitude = bdLocation.getLatitude();
                    locateEntity.longitude = bdLocation.getLongitude();
                    StringBuilder sb = new StringBuilder();
                    sb.append(bdLocation.getCity()).append(bdLocation.getDistrict()).append(bdLocation.getStreet());
                    locateEntity.address = sb.toString();
                    locateEntity.position = bdLocation.getCity() + bdLocation.getDistrict() + bdLocation.getStreet();

                    if (isSaveCache) {
                        mCachedLocateEntity = locateEntity;
                    }
                    if (listener != null) {
                        listener.onLocate(locateEntity);
                    }
                }


                mLocationClient.unRegisterLocationListener(this);
                mLocationClient.stop();
            }

        });
        mLocationClient.start();
        mLocationClient.requestLocation();
    }

    private void init() {
        locate(null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadCityList();
                loadLocationCityList();
            }
        }).start();
    }

    private void loadCityList() {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(
                    new InputStreamReader(mContext.getAssets().open("appcity.csv")));

            String str;
            allCityList.clear();
            while ((str = bufferedReader.readLine()) != null) {
                CityEntity city = new CityEntity();
                String[] strings = str.split(",");

                city.baiduId = Integer.valueOf(strings[1]);
                city.id = Integer.valueOf(strings[0]);// as the key of sparseArray
                city.name = strings[2];
                city.pinyin = strings[3];
                city.provinceId = Integer.valueOf(strings[4]);
                city.longitude = Double.valueOf(strings[5]);
                city.latitude = Double.valueOf(strings[6]);
                mCitySparseArray.append(city.id, city);
                allCityList.add(city);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            DLog.e(Log.getStackTraceString(e));
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    DLog.e(Log.getStackTraceString(e));
                }
            }
        }

        mCityList.clear();
        mCityList.addAll(parseCityList(mCitySparseArray));
    }

    private void loadLocationCityList() {

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(
                    new InputStreamReader(mContext.getAssets().open("appcity.csv")));

            String str;
            allCityList.clear();
            while ((str = bufferedReader.readLine()) != null) {
                CityEntity city = new CityEntity();
                String[] strings = str.split(",");

                city.baiduId = Integer.valueOf(strings[1]);
                city.id = Integer.valueOf(strings[0]);// as the key of sparseArray
                city.name = strings[2];
                city.pinyin = strings[3];
                city.provinceId = Integer.valueOf(strings[4]);
                city.longitude = Double.valueOf(strings[6]);
                city.latitude = Double.valueOf(strings[5]);

                mCitySparseArray.append(city.id, city);
                allCityList.add(city);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            DLog.e(Log.getStackTraceString(e));
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    DLog.e(Log.getStackTraceString(e));
                }
            }
        }

        mCityList.clear();
        mCityList.addAll(parseCityList(mCitySparseArray));
    }

    public static interface OnLocateListener {
        public void onLocate(LocateEntity locateEntity);
    }

    /**
     * 检查手机上是否安装了指定的软件
     *
     * @param context
     * @param packageName：应用包名
     * @return
     */
    public static boolean isAvilible(Context context, String packageName) {
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

}
