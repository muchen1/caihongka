package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2018-3-8.
 */
public class UserBaseModel {

    @Expose
    @SerializedName("status")
    public int status;

    @Expose
    @SerializedName("data")
    public UserBaseEntity data;

    public class UserBaseEntity{

        @Expose
        @SerializedName("real_money")
        public float realMoney;      //真是金额
        @Expose
        @SerializedName("cash_money")
        public float cashMoney;          //可提金额
        @Expose
        @SerializedName("finance_coupon")
        public float financeCoupon;          //可用券
        @Expose
        @SerializedName("finance_lock_coupon")
        public float financeLockCoupon;          //锁定券
        @Expose
        @SerializedName("total_coupon")
        public float totalCoupon;         //总券数

    }

}
