package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2017-3-27.
 */
public class PayModel {
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public PayEntity data;

    public class PayEntity{
        @Expose
        @SerializedName("sign")
        public String sign;
    }
}
