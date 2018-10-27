package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2016-11-15.
 */
public class UpdateModel {
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public UpdateEntity data;

    public class UpdateEntity{
        @Expose
        @SerializedName("url")
        public String url;   //下载地址
        @Expose
        @SerializedName("version")
        public String version;  //版本
        @Expose
        @SerializedName("must")
        public boolean must;  //是否强制升级
        @Expose
        @SerializedName("update")
        public boolean update;  //是否升级
        @Expose
        @SerializedName("remarks")
        public String remarks;  //版本介绍
    }
}
