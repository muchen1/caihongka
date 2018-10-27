package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gc on 2017-12-6.
 */
public class HotGoodEntity implements Serializable{
    @Expose
    @SerializedName("id")
    public String id;
    @Expose
    @SerializedName("goods_name")
    public String goodsName;
    @Expose
    @SerializedName("goods_type")
    public int goodsType;  //1)券 2)券包 3)实物 4)第三方兑换
    @Expose
    @SerializedName("goods_img")
    public String goodsImg;
    @Expose
    @SerializedName("total")
    public int total;
    @Expose
    @SerializedName("remnant")
    public int remnant;
    @Expose
    @SerializedName("money")
    public float money;
    @Expose
    @SerializedName("integral")
    public int integral;
    @Expose
    @SerializedName("user_integral")
    public int userIntegral;
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("sign")
    public String sign;
    @Expose
    @SerializedName("goods_group")
    public int goodsGroup;  //1秒杀 2商城
    @Expose
    @SerializedName("goods_info")
    public String goodsInfo;
    @Expose
    @SerializedName("show_buy")
    public boolean showBut;
}
