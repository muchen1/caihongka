package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2017-12-7.
 */
public class ExchangeEntity {
    @Expose
    @SerializedName("trade_no")
    public String tradeNo;
    @Expose
    @SerializedName("goods_type")
    public int goodsType;
    @Expose
    @SerializedName("pay_money")
    public float payMoney;
    @Expose
    @SerializedName("pay_type")
    public int payType;
    @Expose
    @SerializedName("pay_integral")
    public int payIntegral;
    @Expose
    @SerializedName("created_at")
    public String createdAt;
    @Expose
    @SerializedName("goods_title")
    public String goodsTitle;
    @Expose
    @SerializedName("goods_img")
    public String goodsImg;
    @Expose
    @SerializedName("goods_info")
    public String goodsInfo;
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("address")
    public Address address;

    public class Address{
        @Expose
        @SerializedName("phone")
        public String phone;
        @Expose
        @SerializedName("user_name")
        public String userName;
        @Expose
        @SerializedName("province")
        public String province;
        @Expose
        @SerializedName("city")
        public String city;
        @Expose
        @SerializedName("area")
        public String area;
        @Expose
        @SerializedName("addr")
        public String addr;
    }
}
