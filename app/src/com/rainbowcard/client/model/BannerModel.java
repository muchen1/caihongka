package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by gc on 2017-1-11.
 */
public class BannerModel {
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public BannerData data;

    public class BannerData{
        @Expose
        @SerializedName("site_head")
        public ArrayList<BannerEntity> siteHead;
        @Expose
        @SerializedName("site_middle")
        public ArrayList<BannerEntity> siteMiddle;
        @Expose
        @SerializedName("site_shop_index")
        public ArrayList<BannerEntity> siteShopIndex;
        @Expose
        @SerializedName("site_main_navigate")
        public ArrayList<BannerEntity> siteMainNavigate;
    }
}
