package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by gc on 2018-3-6.
 */
public class TicketFreezeModel {
    @Expose
    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public ArrayList<TicketFreezeEntity> data;

    public class TicketFreezeEntity{

        @Expose
        @SerializedName("trade_no")
        public String tradeNo;
        @Expose
        @SerializedName("created_at")
        public String startDate;
        @Expose
        @SerializedName("lock_at")
        public String endDate;
        @Expose
        @SerializedName("finance_coupon")
        public String financeCoupon;
    }
}
