package com.rainbowcard.client.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by gc on 16-11-30.
 */
public class PrefsManager {

    private static PrefsManager mInstance;
    private SharedPreferences mSharedPreferences;

    private final static String KEY_USER = "key_user";
    private final static String KEY_SDCARD = "key_sdcard";
    private final static String KEY_CHECK_PROVINCE = "key_province";
    private final static String KEY_CHECK_PROVINCE_NUM = "key_province_num";
    private final static String KEY_CHECK_CITY = "key_city";
    private final static String KEY_ENGINE = "key_engine";
    private final static String KEY_CLASS = "key_class";
    private final static String KEY_SOURCE = "key_source";
    private final static String KEY_CURRENT_ITEM = "key_current_item";
    private final static String KEY_CITY_CURRENT_ITEM = "key_city_current_tiem";
    private final static String KEY_PLATE_NUM = "key_plate_num";
    private final static String KEY_CITY_CODE = "key_city_code";

    public static PrefsManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (PrefsManager.class) {
                if (mInstance == null) {
                    mInstance = new PrefsManager(
                            context.getApplicationContext());
                }
            }
        }

        return mInstance;
    }

    private PrefsManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(KEY_USER, 0);
    }

    //卡号
    public String getLastCard() {
        return mSharedPreferences.getString(KEY_SDCARD, "");

    }

    public void setLastCard(String card) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putString(KEY_SDCARD, card);
        editor.commit();
    }
    //查询省份
    public String getCheckProvince() {
        return mSharedPreferences.getString(KEY_CHECK_PROVINCE, "");

    }

    public void setCheckProvince(String province) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putString(KEY_CHECK_PROVINCE, province);
        editor.commit();
    }
    //查询省份简称
    public String getCheckProvinceNum() {
        return mSharedPreferences.getString(KEY_CHECK_PROVINCE_NUM, "");

    }

    public void setCheckProvinceNum(String provinceNum) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putString(KEY_CHECK_PROVINCE_NUM, provinceNum);
        editor.commit();
    }
    //查询城市
    public String getCheckCity() {
        return mSharedPreferences.getString(KEY_CHECK_CITY, "");

    }

    public void setCheckCity(String city) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putString(KEY_CHECK_CITY, city);
        editor.commit();
    }
    //发动机号码
    public String getEngine() {
        return mSharedPreferences.getString(KEY_ENGINE, "");

    }

    public void setEngine(String engine) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putString(KEY_ENGINE, engine);
        editor.commit();
    }
    //车辆代码
    public String getClassLen() {
        return mSharedPreferences.getString(KEY_CLASS, "");

    }

    public void setClassLen(String classLen) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putString(KEY_CLASS, classLen);
        editor.commit();
    }
    //渠道类型
    public String getSource() {
        return mSharedPreferences.getString(KEY_SOURCE, "");

    }

    public void setSource(String source) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putString(KEY_SOURCE, source);
        editor.commit();
    }
    //当前省份位置
    public int getCurrentItem() {
        return mSharedPreferences.getInt(KEY_CURRENT_ITEM, -1);

    }

    public void setCurrenItem(int currentItem) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putInt(KEY_CURRENT_ITEM, currentItem);
        editor.commit();
    }
    //当前城市位置
    public int getCityCurrentItem() {
        return mSharedPreferences.getInt(KEY_CITY_CURRENT_ITEM, -1);

    }

    public void setCityCurrentItem(int cityCurrentItem) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putInt(KEY_CITY_CURRENT_ITEM, cityCurrentItem);
        editor.commit();
    }

    //车牌
    public String getPlateNum(){
        return mSharedPreferences.getString(KEY_PLATE_NUM, "");
    }

    public void setPlateNum(String plateNum){
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putString(KEY_PLATE_NUM, plateNum);
        editor.commit();
    }

    //城市代码
    public String getCityCode(){
        return mSharedPreferences.getString(KEY_CITY_CODE, "");
    }

    public void setCityCode(String cityCode){
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putString(KEY_CITY_CODE, cityCode);
        editor.commit();
    }

}
