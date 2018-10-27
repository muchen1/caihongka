package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2017-1-18.
 */
public class CommentEntity {
    @Expose
    @SerializedName("star")
    public String star;
    @Expose
    @SerializedName("content")
    public String content;
    @Expose
    @SerializedName("created_at")
    public String createdAt;
    @Expose
    @SerializedName("user_phone")
    public String userPhone;
}
