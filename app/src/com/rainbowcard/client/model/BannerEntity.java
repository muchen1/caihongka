package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gc on 2016-12-28.
 */
public class BannerEntity implements Serializable{
    @Expose
    @SerializedName("city_id")
    public int cityId;
    @Expose
    @SerializedName("id")
    public int id;
    @Expose
    @SerializedName("title")
    public String title;
    @Expose
    @SerializedName("url")
    public String url;
    @Expose
    @SerializedName("image")
    public String img;
    @Expose
    @SerializedName("content")
    public String content;
    @Expose
    @SerializedName("type")
    public int type;
    @Expose
    @SerializedName("site")
    public int site;
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("class_type")
    public int classType;
    @Expose
    @SerializedName("um_click_key")
    public String umClickKey;
}
