package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2016-11-17.
 */
public class CardInfoModel {
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public CardInfoEntity data;

    public class CardInfoEntity{
        @Expose
        @SerializedName("show")
        public boolean show;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("phone")
        public String phone;
    }
}
