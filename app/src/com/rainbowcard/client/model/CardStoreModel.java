package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by gc on 2016-11-14.
 */
public class CardStoreModel {
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public ArrayList<CardStoreEntity> data;

    public class CardStoreEntity implements Serializable{
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("address")
        public String address;
        @Expose
        @SerializedName("telephone")
        public String telephone;
        @Expose
        @SerializedName("lng")
        public double longitude;
        @Expose
        @SerializedName("lat")
        public double latitude;
        @Expose
        @SerializedName("remarks")
        public String remarks;
    }
}
