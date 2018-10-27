package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2016/4/22.
 */
public class TenPayEntity {
    @Expose
    @SerializedName("appid")
    public String appid;
    @Expose
    @SerializedName("partner")
    public String partner;
    @Expose
    @SerializedName("partner_key")
    public String partnerKey;
    @Expose
    @SerializedName("app_secret")
    public String appSecret;
    @Expose
    @SerializedName("app_key")
    public String appKey;
    @Expose
    @SerializedName("notify_url")
    public String notifyUrl;
}
