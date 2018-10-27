package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rainbowcard.client.common.city.CityEntity;

import java.util.ArrayList;

/**
 * Created by gc on 2017-3-28.
 */
public class HotCity {

    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public ArrayList<CityEntity> data;

    public class HotCityEntity{
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("p_id")
        public int provinceId;
        @Expose
        @SerializedName("cname")
        public String pinyin;
    }
}
