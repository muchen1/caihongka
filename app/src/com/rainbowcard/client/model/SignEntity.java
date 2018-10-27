package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016-5-9.
 */
public class SignEntity {
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public SignInfo signInfo;

    public class SignInfo{
        @Expose
        @SerializedName("signDays")
        public ArrayList<String> signDays;
        @Expose
        @SerializedName("signNumbers")
        public String signNumbers;
        @Expose
        @SerializedName("continueSignDay")
        public int continueSignDay;
        @Expose
        @SerializedName("getPoint")
        public int getPoint;
        @Expose
        @SerializedName("enable")
        public int enable;
    }
}
