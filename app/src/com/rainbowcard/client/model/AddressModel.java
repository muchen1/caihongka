package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by gc on 2016/4/25.
 */
public class AddressModel {

    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public ArrayList<AddressEntity> data;

}
