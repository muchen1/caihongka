package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by gc on 2018-3-12.
 */
public class HeadlineModel {

    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public ArrayList<HeadlineEntity> data;

    public class HeadlineEntity{
        @Expose
        @SerializedName("phone")
        public String phone;

        @Expose
        @SerializedName("coupon")
        public float coupon;
    }
}
