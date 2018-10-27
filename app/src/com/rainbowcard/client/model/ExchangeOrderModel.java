package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2017-12-6.
 */
public class ExchangeOrderModel {

    @Expose
    @SerializedName("status")
    public int status;

    @Expose
    @SerializedName("data")
    public String data;

//    @Expose
//    @SerializedName("data")
//    public ExchangeOrderEntity data;

    public class ExchangeOrderEntity{
        @Expose
        @SerializedName("trade_no")
        public String tradeNo;

        @Expose
        @SerializedName("sign")
        public String sign;
    }

}
