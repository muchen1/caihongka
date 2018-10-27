package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2018-5-3.
 */
public class AutonymModel {
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public AutonymEntity data;

    public class AutonymEntity{
        @Expose
        @SerializedName("real_name")
        public String realName;
        @Expose
        @SerializedName("id_card")
        public String idCard;
        @Expose
        @SerializedName("id")
        public String id;
    }
}
