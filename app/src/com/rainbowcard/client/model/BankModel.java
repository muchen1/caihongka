package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by gc on 2018-3-8.
 */
public class BankModel {

    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public ArrayList<BankEntity> data;

    public class BankEntity{
        @Expose
        @SerializedName("id")
        public String id;
        @Expose
        @SerializedName("bank_name")
        public String bankName;
        @Expose
        @SerializedName("bank_img")
        public String bankImg;
    }
}
