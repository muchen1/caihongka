package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2016-6-22.
 */
public class PointsEntity {
    @Expose
    @SerializedName("rebate")
    public int points;
    @Expose
    @SerializedName("price")
    public int price;
}
