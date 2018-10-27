package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2017-1-6.
 */
public class IllegalEntity {
    @Expose
    @SerializedName("date")
    public String date;
    @Expose
    @SerializedName("area")
    public String area;
    @Expose
    @SerializedName("act")
    public String act;
    @Expose
    @SerializedName("fen")
    public String fen;
    @Expose
    @SerializedName("money")
    public String money;
    @Expose
    @SerializedName("handled")
    public String handled;
    @Expose
    @SerializedName("code")
    public String code;
}
