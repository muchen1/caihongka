package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2018-3-10.
 */
public class CashInfoModel {

    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public CashInfoEntity data;

    public class CashInfoEntity{
        @Expose
        @SerializedName("money")
        public float money;
        @Expose
        @SerializedName("exceed_cash_money")
        public float exceedCashMoney;     //超出金额
        @Expose
        @SerializedName("charges_money")
        public float chargesMoney;     //手续费  为0可以直接提现
        @Expose
        @SerializedName("max_cash_money")
        public float maxCashMoney;   //最大提现 0标示可以提
    }
}
