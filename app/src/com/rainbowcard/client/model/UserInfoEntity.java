package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2016/4/19.
 */
public class UserInfoEntity {
    @Expose
    @SerializedName("uid")
    public String uid;

    @Expose
    @SerializedName("nickname")
    public String nickname;

    @Expose
    @SerializedName("sex")
    public int sex;

    @Expose
    @SerializedName("mobile_number")
    public String mobileNumber;

    @Expose
    @SerializedName("avatar")
    public String avatar;

}
