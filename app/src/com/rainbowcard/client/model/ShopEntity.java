package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by gc on 2017-1-16.
 */
public class ShopEntity {
        private static final long serialVersionUID = 8633299996744734593L;

        @Expose
        @SerializedName("lat")
        public double latitude;//纬度
        @Expose
        @SerializedName("lng")
        public double longitude;//经度
        @Expose
        @SerializedName("shop_id")
        public String shopId;
        @Expose
        @SerializedName("shop_name")
        public String name;//名字
        @Expose
        @SerializedName("site")
        public String addr;//地址
        @Expose
        @SerializedName("area_id")
        public String areaId;
        @Expose
        @SerializedName("notice")
        public String notice;
        @Expose
        @SerializedName("status")
        public String status;
        @Expose
        @SerializedName("star")
        public String star;
        @Expose
        @SerializedName("order_num")
        public String orderNum;
        @Expose
        @SerializedName("distance")
        public String distance;
        @Expose
        @SerializedName("img")
        public String[] imgUrl;//图片数组
        @Expose
        @SerializedName("service")
        public ArrayList<ServeTypeEntity> serviceEntitys;
        @Expose
        @SerializedName("is_collect")
        public boolean isCollect;

}
