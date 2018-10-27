package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2016-5-17.
 */
public class OrderEntity {
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public OrderDetail orderDetail;

    public class OrderDetail{
        @Expose
        @SerializedName("received_name")
        public String receivedName;
        @Expose
        @SerializedName("received_phone")
        public String receivedPhone;
        @Expose
        @SerializedName("received_address")
        public String receivedAddress;
        @Expose
        @SerializedName("shop_name")
        public String shopName;
        @Expose
        @SerializedName("points")
        public String points;
        @Expose
        @SerializedName("exchange_time")
        public String exchangeTime;
        @Expose
        @SerializedName("order_num")
        public String orderNum;
        @Expose
        @SerializedName("ep_num")
        public String epNum;
        @Expose
        @SerializedName("ep_company")
        public String epCompany;
        @Expose
        @SerializedName("ep_status")
        public String epStatus;
        @Expose
        @SerializedName("created_time")
        public String createdTime;
        @Expose
        @SerializedName("freight")
        public String freight;
        @Expose
        @SerializedName("price")
        public String price;
        @Expose
        @SerializedName("discount")
        public String discount;
        @Expose
        @SerializedName("amount")
        public String amount;
    }
}
