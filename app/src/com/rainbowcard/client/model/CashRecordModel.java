package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by gc on 2018-3-12.
 */
public class CashRecordModel {

    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public ArrayList<CashRecordEntity> data;

    public class CashRecordEntity{
        @Expose
        @SerializedName("money")
        public String money;
        @Expose
        @SerializedName("pay_money")
        public String PayMoney;
        @Expose
        @SerializedName("charges_money")
        public String chargesMoney;
        @Expose
        @SerializedName("created_at")
        public String createdAt;
        @Expose
        @SerializedName("status")
        public int status;   //1)待打款 2)已打款
    }
}
