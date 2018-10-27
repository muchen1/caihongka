package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2016/4/25.
 */
public class CarouselInfoEntity {
    @Expose
    @SerializedName("carousel_id")
    public int carouselId;
    @Expose
    @SerializedName("img")
    public String img;
    @Expose
    @SerializedName("type")
    public int type;
    @Expose
    @SerializedName("url")
    public String url;
    @Expose
    @SerializedName("is_share")   //是否分享 1为分享 0不分享
    public int isShare;
    @Expose
    @SerializedName("iphoto_num")  //是否需要手机号码 1为需要 0为不需要
    public int iphotoNum;
    @Expose
    @SerializedName("user_login")    //是否需要用户登录 1为需要 0为不需要
    public int userLogin;
    @Expose
    @SerializedName("skip_id")
    public int skipId;
    @Expose
    @SerializedName("sort")
    public int sort;
    @Expose
    @SerializedName("descs")
    public String describe;

}
