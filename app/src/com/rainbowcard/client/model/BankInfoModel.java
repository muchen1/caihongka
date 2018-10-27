package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2018-3-9.
 */
public class BankInfoModel {
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public BankInfoEntity data;

    public class BankInfoEntity{
        @Expose
        @SerializedName("bank_id")
        public String bankId;
        @Expose
        @SerializedName("bank_name")
        public String bankName;
        @Expose
        @SerializedName("bank_card")
        public String bankCard;
        @Expose
        @SerializedName("bank_img")
        public String bankImg;
    }
}
