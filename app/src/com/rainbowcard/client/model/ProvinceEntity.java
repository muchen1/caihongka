package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by gc on 2017-1-6.
 */
public class ProvinceEntity {
    @Expose
    @SerializedName("province_name")
    public String name;
    @Expose
    @SerializedName("province_pre_car_num")
    public String provinceNum;
    @Expose
    @SerializedName("city_list")
    public ArrayList<CityModel2> cityList;
}
