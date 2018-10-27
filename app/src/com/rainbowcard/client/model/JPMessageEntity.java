package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2016-5-25.
 */
public class JPMessageEntity {
    @Expose
    @SerializedName("push_type")
    public int pushType;  //推送类型:1为轮播,2为新闻,3为开奖,4为赛事,5为积分商品兑换

    //轮播推送参数
    @Expose
    @SerializedName("skip_id")
    public int skipId;
    @Expose
    @SerializedName("url")
    public String url;
    @Expose
    @SerializedName("type")
    public int type;      //轮播类型 1为赛程页面,2为h5页面,3为游戏页面
    @Expose
    @SerializedName("img")
    public String img;

    @Expose
    @SerializedName("schedule_id")
    public int scheduleId;

    //轮播h5用
    @Expose
    @SerializedName("is_share")
    public int isShare;
    @Expose
    @SerializedName("iphoto_num")
    public int iphotoNum;
    @Expose
    @SerializedName("user_login")
    public int userLogin;

    @Expose
    @SerializedName("descs")
    public String describe;

    //新闻推送参数
    @Expose
    @SerializedName("news_id")
    public int newsId;
    @Expose
    @SerializedName("isother")
    public int isother; //是否为第三方连接新闻 1为第三方 0时需要拼接news_id
    @Expose
    @SerializedName("thirdurl")
    public String thirdurl;

    @Expose
    @SerializedName("bet_id")
    public int betId;

    @Expose
    @SerializedName("shop_id")
    public int shopId;
}
