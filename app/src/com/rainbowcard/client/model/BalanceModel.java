package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2016-11-14.
 */
public class BalanceModel {
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public BalanceEntity data;

    public class BalanceEntity{
        @Expose
        @SerializedName("balance")
        public String balance;
        @Expose
        @SerializedName("count")
        public String count;
    }
}
