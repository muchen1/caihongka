package com.rainbowcard.client.common.city;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by kleist on 14-9-2.
 */
public class CityEntity implements Serializable{

    @Expose
    @SerializedName("id")
    public int id = 0;
    public int baiduId;
    @Expose
    @SerializedName("name")
    public String name;
    @Expose
    @SerializedName("cname")
    public String pinyin;
    public int provinceId;
    public double longitude;
    public double latitude;

    public CityEntity() {

    }

    public CityEntity(int id, int baiduId, String name, String pinyin, int provinceId,double longitude,double latitude) {
        this.id = id;
        this.baiduId = baiduId;
        this.name = name;
        this.pinyin = pinyin;
        this.provinceId = provinceId;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
