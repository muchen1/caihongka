package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2017-1-6.
 */
public class CheckDetailModel {
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public String data;
}
