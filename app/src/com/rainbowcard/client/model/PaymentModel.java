package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2017-1-18.
 */
public class PaymentModel {
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public PaymentEntity data;

    public class PaymentEntity{
        @Expose
        @SerializedName("trade_no")
        public String tradeNo;
    }
}
