package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by gc on 2017-1-13.
 */
public class RecordModel {
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public ArrayList<RecordEntity> data;
}
