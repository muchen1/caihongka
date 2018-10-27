package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gc on 2017-3-16.
 */
public class DiscountEntity implements Serializable{

    @Expose
    @SerializedName("id")
    public int id;
    @Expose
    @SerializedName("lock")
    public int lock;
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("start_time")
    public String startTime;
    @Expose
    @SerializedName("end_time")
    public String endTime;
    @Expose
    @SerializedName("title")
    public String title;
    @Expose
    @SerializedName("money")
    public String money;
    @Expose
    @SerializedName("money_type")
    public int moneyType;
    @Expose
    @SerializedName("used_money")
    public String usedMoney;
    @Expose
    @SerializedName("rule")
    public String rule;
    @Expose
    @SerializedName("pay_money")
    public float payMoney;
    @Expose
    @SerializedName("coupon_money")
    public float couponMoney;
    @Expose
    @SerializedName("will_expired")
    public boolean willExpired;
}
