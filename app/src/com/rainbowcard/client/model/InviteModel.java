package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2018-3-13.
 */
public class InviteModel {

    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public InviteEntity data;

    public class InviteEntity{
        @Expose
        @SerializedName("invite_number")
        public String inviteNumber;
        @Expose
        @SerializedName("friends_coupon")
        public String friendsCoupon;
        @Expose
        @SerializedName("invite_url")
        public String inviteUrl;
    }
}
