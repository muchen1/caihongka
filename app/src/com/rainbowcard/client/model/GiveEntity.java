package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2017-8-30.
 */
public class GiveEntity {
    @Expose
    @SerializedName("id")
    public int id;
    @Expose
    @SerializedName("aid")
    public int aid;
    @Expose
    @SerializedName("min")
    public int min;
    @Expose
    @SerializedName("max")
    public int max;
    @Expose
    @SerializedName("rebate")
    public int rebate;
}
