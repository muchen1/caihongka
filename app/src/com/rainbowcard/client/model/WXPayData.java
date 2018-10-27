package com.rainbowcard.client.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by kleist on 14-10-17.
 */
public class WXPayData implements Serializable {

    public String sign;

    public String timestamp;

    public String partnerid;

    public String noncestr;

    public String prepayid;

    @SerializedName("package")
    public String packageData;

    public String appid;
}
