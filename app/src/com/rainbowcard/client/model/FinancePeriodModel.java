package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by gc on 2018-3-6.
 */
public class FinancePeriodModel {

    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public FinancePeriodData data;


    public class FinancePeriodData{

        @Expose
        @SerializedName("goods")
        public ArrayList<FinancePeriodEntity> goods;

        @Expose
        @SerializedName("add")
        public double add;

        @Expose
        @SerializedName("finance_coupon_money")
        public int financeCouponMoney;
    }

    public class FinancePeriodEntity{

        @Expose
        @SerializedName("day")
        public int day;
        @Expose
        @SerializedName("interest")
        public double yearInterest;
        @Expose
        @SerializedName("month")
        public int month;

        public String count;
    }
}
