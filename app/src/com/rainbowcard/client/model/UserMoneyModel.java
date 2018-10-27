package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2017-8-30.
 */
public class UserMoneyModel {

    @Expose
    @SerializedName("status")
    public int status;

    @Expose
    @SerializedName("data")
    public UserMoney data;


    public class UserMoney{
        @Expose
        @SerializedName("money")
        public float money;

        @Expose
        @SerializedName("real_money")
        public float realMoney;

        @Expose
        @SerializedName("giveaway")
        public float giveaway;

        @Expose
        @SerializedName("lock_money")
        public float lockMoney;

        @Expose
        @SerializedName("total_money")
        public float totalMoney;

    }
}
