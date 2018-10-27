package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by gc on 2018-3-6.
 */
public class TicketStateModel {
    @Expose
    @SerializedName("status")
    public int status;
    @SerializedName("data")
    public ArrayList<TicketStateEntity> data;

    public class TicketStateEntity{

        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("is_add")
        public int isAdd;
        @Expose
        @SerializedName("describe")
        public String describe;
        @Expose
        @SerializedName("created_at")
        public String createdAt;
        @Expose
        @SerializedName("details")
        public float details;
        @Expose
        @SerializedName("name")
        public String name;
    }
}
