package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2016/4/22.
 */
public class ALiPayEntity {
    @Expose
    @SerializedName("partner")
    public String partner;
    @Expose
    @SerializedName("seller_email")
    public String sellerEmail;
    @Expose
    @SerializedName("notify_url")
    public String notifyUrl;
}
