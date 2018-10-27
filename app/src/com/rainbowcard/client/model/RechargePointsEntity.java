package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by gc on 2016-6-22.
 */
public class RechargePointsEntity {
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public RechargePointsData data;
    public class RechargePointsData{
        @Expose
        @SerializedName("first")
        public int defaultMoney;
        @Expose
        @SerializedName("discount_rate")
        public double rate;
        @Expose
        @SerializedName("unit")
        public int unit;
        @Expose
        @SerializedName("list")
        public ArrayList<PointsEntity> list;
    }
}
