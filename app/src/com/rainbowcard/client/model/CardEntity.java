package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2017-1-9.
 */
public class CardEntity {
    @Expose
    @SerializedName("card_id")
    public String cardId;
    @Expose
    @SerializedName("card_number")
    public String num;
    @Expose
    @SerializedName("card_count")
    public String count;
    @Expose
    @SerializedName("card_balance")
    public String balance;
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("name")
    public String name;
    @Expose
    @SerializedName("phone")
    public String phone;
}
