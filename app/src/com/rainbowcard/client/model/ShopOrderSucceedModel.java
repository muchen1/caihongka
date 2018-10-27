package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2017-1-18.
 */
public class ShopOrderSucceedModel {
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public ShopOrderSucceedEntity data;

    public class ShopOrderSucceedEntity{
        @Expose
        @SerializedName("shop_name")
        public String shopName;
        @Expose
        @SerializedName("service_type")
        public int serviceType;
        @Expose
        @SerializedName("service_son_type")
        public int serviceSonType;
        @Expose
        @SerializedName("service_type_text")
        public String serviceTypeText;
        @Expose
        @SerializedName("service_son_type_text")
        public String serviceSonTypeText;
        @Expose
        @SerializedName("money")
        public float money;
        @Expose
        @SerializedName("pay_money")
        public float payMoney;
        @Expose
        @SerializedName("code")
        public String code;
        @Expose
        @SerializedName("status")
        public int status;
        @Expose
        @SerializedName("coupon_money")
        public float couponMoney;
    }
}
