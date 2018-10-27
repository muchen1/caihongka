package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gc on 2018-3-7.
 */
public class FinanceOrderModel {

    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public FinanceOrderEntity data;


    public class FinanceOrderEntity implements Serializable{

        @Expose
        @SerializedName("trade_no")
        public String tradeNo;
        @Expose
        @SerializedName("u_id")
        public int uId;
        @Expose
        @SerializedName("money")
        public int money;
        @Expose
        @SerializedName("finance_coupon")
        public String financeCoupon;
        @Expose
        @SerializedName("lock_days")
        public String lockDays;
        @Expose
        @SerializedName("return_time")
        public String returnTime;
        @Expose
        @SerializedName("status")
        public int status;
        @Expose
        @SerializedName("updated_at")
        public String updatedAt;
        @Expose
        @SerializedName("created_at")
        public String createdAt;
    }
}
