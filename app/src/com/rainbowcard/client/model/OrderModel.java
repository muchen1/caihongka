package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gc on 2016-11-17.
 */
public class OrderModel {
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public OrderEntity data;

    public class OrderEntity implements Serializable{
        @Expose
        @SerializedName("trade_no")
        public String tradeNo;
        @Expose
        @SerializedName("time")
        public String time;
        @Expose
        @SerializedName("card_type_text")
        public String cardTypeText;
        @Expose
        @SerializedName("pay_type")
        public String payType;
        @Expose
        @SerializedName("status")
        public int status;
        @Expose
        @SerializedName("user_name")
        public String userName;
        @Expose
        @SerializedName("phone")
        public String phone;
        @Expose
        @SerializedName("card_number")
        public String cardNumber;
        @Expose
        @SerializedName("address")
        public String address;
        @Expose
        @SerializedName("money")
        public float money;
        @Expose
        @SerializedName("pay_money")
        public float payMoney;
        @Expose
        @SerializedName("coupon_money")
        public float couponMoney;
        @Expose
        @SerializedName("coupon_money_type")    //优惠券使用情况 0未使用  1金额 2 折扣
        public int couponMoneyType;
        @Expose
        @SerializedName("coupon_dis")   //优惠券   X折
        public float couponDis;
        @Expose
        @SerializedName("rebate_money")
        public float rebateMoney;
        @Expose
        @SerializedName("expire")
        public String expire;


        //商户消费订单详情
        @Expose
        @SerializedName("shop_id")
        public String shopId;
        @Expose
        @SerializedName("shop_address")
        public String shopAddress;
        @Expose
        @SerializedName("shop_img")
        public String shopImg;
        @Expose
        @SerializedName("shop_name")
        public String shopName;
        @Expose
        @SerializedName("service_type")
        public String serviceType;
        @Expose
        @SerializedName("service_son_type")
        public String serviceSonType;
        @Expose
        @SerializedName("service_type_text")
        public String serviceTypeText;
        @Expose
        @SerializedName("service_son_type_text")
        public String serviceSonTypeText;
        @Expose
        @SerializedName("payment_type")
        public int paymentType;
        @Expose
        @SerializedName("code")
        public String code;
        @Expose
        @SerializedName("lat")
        public double latitude;//纬度
        @Expose
        @SerializedName("lng")
        public double longitude;//经度

    }
}
