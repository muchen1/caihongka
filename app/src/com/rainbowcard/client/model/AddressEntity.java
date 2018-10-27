package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gc on 2016/4/20.
 */
public class AddressEntity implements Serializable{

    @Expose
    @SerializedName("id")
    public int id;

    @Expose
    @SerializedName("user_name")
    public String userName;

    @Expose
    @SerializedName("phone")
    public String phone;

    @Expose
    @SerializedName("area")
    public String area;

    @Expose
    @SerializedName("province")
    public String province;

    @Expose
    @SerializedName("city")
    public String city;

    @Expose
    @SerializedName("addr")
    public String addr;

    @Expose
    @SerializedName("default")
    public int isDefault;

    @Expose
    @SerializedName("status")
    public int status;

}
