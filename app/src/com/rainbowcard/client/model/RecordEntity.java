package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gc on 2017-1-9.
 */
public class RecordEntity implements Serializable{
    @Expose
    @SerializedName("time")
    public String date;

    @Expose
    @SerializedName("account")
    public String num;
    @Expose
    @SerializedName("money")
    public float recharge;
    @Expose
    @SerializedName("pay_money")
    public float payMoney;
    @Expose
    @SerializedName("pay_type")
    public String payType;
    @Expose
    @SerializedName("card_type")
    public String cardType;
    @Expose
    @SerializedName("trade_no")
    public String tradeNo;
    @Expose
    @SerializedName("coupon_money")
    public float couponMoney;
    @Expose
    @SerializedName("pay_type_t")
    public String  payTypeT;
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("user_name")
    public String userName;
}
