package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2017-12-6.
 */
public class IntegralModel {

    @Expose
    @SerializedName("status")
    public int status;

    @Expose
    @SerializedName("data")
    public IntegralEntity data;

    public class IntegralEntity{

        @Expose
        @SerializedName("integral")
        public int integral;

        @Expose
        @SerializedName("sing_in")
        public int singIn;
    }
}
