package com.rainbowcard.client.wxapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by gc on 16/5/23.
 */
public class WXToken {
    @Expose
    @SerializedName("access_token")
    public String accessToken = "";

    @Expose
    @SerializedName("expires_in")
    public int expiresIn;

    @Expose
    @SerializedName("refresh_token")
    public String refreshToken = "";

    @Expose
    @SerializedName("openid")
    public String openID;

    @Expose
    @SerializedName("unionid")
    public String unionId;

    @Expose
    public String scope;
}
