package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by gc on 2018-3-9.
 */
public class FinanceRecordModel {
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public ArrayList<FinanceRecordEntity> data;

    public class FinanceRecordEntity{
        @Expose
        @SerializedName("money")
        public double money;
        @Expose
        @SerializedName("trade_no")
        public String tradeNo;
        @Expose
        @SerializedName("lock_days")
        public String period;
        @Expose
        @SerializedName("finance_coupon")
        public String financeCoupon;
        @Expose
        @SerializedName("created_at")
        public String createdAt;
        @Expose
        @SerializedName("lock_at")
        public String lockAt;
        @Expose
        @SerializedName("status")
        public int status;   // 2)锁定 3已到期
        @Expose
        @SerializedName("ticket_icon")
        public String ticketIcon;
    }
}
