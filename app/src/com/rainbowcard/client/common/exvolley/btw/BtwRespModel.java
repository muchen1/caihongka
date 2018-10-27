package com.rainbowcard.client.common.exvolley.btw;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by kleist on 14-8-16.
 */
public class BtwRespModel<T> {

    @Expose
    @SerializedName("status")
    public int errorCode;

    @Expose
    @SerializedName("message")
    public String errorMessage = "";

    @Expose
    public T result;
}
