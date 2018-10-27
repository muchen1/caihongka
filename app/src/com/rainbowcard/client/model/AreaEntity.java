package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2017-1-18.
 */
public class AreaEntity {
    @Expose
    @SerializedName("name")
    public String name;
    @Expose
    @SerializedName("id")
    public String id;
    @Expose
    @SerializedName("cname")
    public String cname;
    @Expose
    @SerializedName("code")
    public String code;
}
