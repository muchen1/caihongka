package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2016-11-17.
 */
public class ALiPayModel {
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public ALiPayEntity data;

    public class ALiPayEntity{
        @Expose
        @SerializedName("rst")
        public String rst;

    }
}
