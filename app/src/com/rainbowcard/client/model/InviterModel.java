package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by gc on 2018-3-30.
 */
public class InviterModel {

    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public ArrayList<InviterEntity> data;

    public class InviterEntity{
        @Expose
        @SerializedName("phone")
        public String phone;
        @Expose
        @SerializedName("finance_time")
        public String financeTime;
    }
}
