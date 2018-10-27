package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2017-1-13.
 */
public class UserModel {
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public UserInfo data;

    public class UserInfo {
        @Expose
        @SerializedName("phone")
        public String phone;

        @Expose
        @SerializedName("money")
        public float money;

        @Expose
        @SerializedName("total_money")
        public float totalMoney;

        @Expose
        @SerializedName("default_account")
        public String defaultAccount;

        @Expose
        @SerializedName("indicator")
        public String indicator;

        @Expose
        @SerializedName("status")
        public String status;

        @Expose
        @SerializedName("coupon_used")
        public String couponUsed;  //已使用优惠券
        @Expose
        @SerializedName("coupon_un_used")
        public String couponUnUsed; //未使用优惠券
        @Expose
        @SerializedName("coupon_expire")
        public String couponExpire; //过期优惠券
        @Expose
        @SerializedName("coupon_total")
        public String couponTotal;//优惠券总数
        @Expose
        @SerializedName("integral")
        public String integral;
        @Expose
        @SerializedName("integral_url")
        public String integralUrl;
        @Expose
        @SerializedName("need_certification")
        public int needCertification;
    }
}
