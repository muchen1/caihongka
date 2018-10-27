package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gc on 2017-5-4.
 */
public class MessageEntity implements Serializable{

    @Expose
    @SerializedName("id")
    public int id;
    @Expose
    @SerializedName("user_id")
    public String userId;
    @Expose
    @SerializedName("extra")
    public Extra extra;
    @Expose
    @SerializedName("after_open")
    public String afterOpen;
    @Expose
    @SerializedName("is_success")
    public String isSuccess;
    @Expose
    @SerializedName("type")
    public String type;
    @Expose
    @SerializedName("start_time")
    public String startTime;
    @Expose
    @SerializedName("expire_time")
    public String expireTime;
    @Expose
    @SerializedName("title")
    public String title;
    @Expose
    @SerializedName("text")
    public String text;

    public class Extra{
        @Expose
        @SerializedName("url")
        public String url;
        @Expose
        @SerializedName("type")
        public String type;
        @Expose
        @SerializedName("content")
        public String content;
    }
}
