package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by gc on 2017-1-6.
 */
public class SearchResultEntity {

    @Expose
    @SerializedName("list")
    public ArrayList<IllegalEntity> list;
    @Expose
    @SerializedName("hphm")
    public String hphm;
    @Expose
    @SerializedName("count")
    public String count;
    @Expose
    @SerializedName("money")
    public String money;
    @Expose
    @SerializedName("city_name")
    public String cityName;
    @Expose
    @SerializedName("fen")
    public String fen;
    @Expose
    @SerializedName("isShare")
    public boolean isShare;
    @Expose
    @SerializedName("url")
    public String url;
}
