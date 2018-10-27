package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by gc on 2016-10-31.
 */
public class MarkerInfoUtil implements Serializable{
    private static final long serialVersionUID = 8633299996744734593L;

    @Expose
    public boolean isSelect = false;

    @Expose
    @SerializedName("shop_id")
    public String shopId;
    @Expose
    @SerializedName("lat")
    public double latitude;//纬度
    @Expose
    @SerializedName("lng")
    public double longitude;//经度
    @Expose
    @SerializedName("shop_name")
    public String name;//名字
    @Expose
    @SerializedName("phone")
    public String phone;//网点电话
    @Expose
    @SerializedName("site")
    public String addr;//地址
    @Expose
    @SerializedName("province_id")
    public String provinceId;
    @Expose
    @SerializedName("city_id")
    public String cityId;
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
    @SerializedName("default_weight")
    public String defaultWeight;
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
    public String imgUrl;//图片
    @Expose
    @SerializedName("renbao_use")
    public int renbaoUse;
    @Expose
    @SerializedName("service")
    public ArrayList<ServiceEntity> serviceEntitys;

    public class ServiceEntity implements Serializable{
        @Expose
        @SerializedName("service_type")
        public int serviceType;
        @Expose
        @SerializedName("service_name")
        public String serviceName;
        @Expose
        @SerializedName("money")
        public String money;
        @Expose
        @SerializedName("cash")
        public String cash;
        @Expose
        @SerializedName("service_son_type")
        public int serviceSonType;
    }
}
