package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2017-1-11.
 */
public class TokenModel {
    @Expose
    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public TokenEntity data;

    public class TokenEntity{
        @Expose
        @SerializedName("token")
        public String token;
    }
}
