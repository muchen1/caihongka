package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by gc on 2017-8-7.
 */
public class NoticeModel {
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public NoticeData data;

    public class NoticeData{
        @Expose
        @SerializedName("travel")
        public ArrayList<TravelData> travelDatas;
        @Expose
        @SerializedName("info")
        public ArrayList<InfoData> infoDatas;
    }

    public class TravelData{
        @Expose
        @SerializedName("date")
        public String date;
        @Expose
        @SerializedName("week")
        public String week;
        @Expose
        @SerializedName("isxianxing")
        public int isxianxing;
        @Expose
        @SerializedName("xxweihao")
        public String xxweihao;
    }

    public class InfoData{
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("city_id")
        public int cityId;
        @Expose
        @SerializedName("city")
        public String city;
        @Expose
        @SerializedName("notice")
        public String notice;
        @Expose
        @SerializedName("type")
        public int type;
        @Expose
        @SerializedName("rst_type")
        public int rstType;
        @Expose
        @SerializedName("rst_content")
        public String rstContent;
        @Expose
        @SerializedName("status")
        public int status;
    }

}
