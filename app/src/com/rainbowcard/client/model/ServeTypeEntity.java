package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2017-1-4.
 */
public class ServeTypeEntity {
    @Expose
    @SerializedName("service_type")
    public int serviceType;
    @Expose
    @SerializedName("service_name")
    public String serviceName;
    @Expose
    @SerializedName("money")
    public int money;
    @Expose
    @SerializedName("cash")
    public String cash;
    @Expose
    @SerializedName("service_son_type")
    public int serviceSonType;
    @Expose
    @SerializedName("count")
    public String count;
    @Expose
    @SerializedName("service_activity")
    public String serviceActivity;
}
