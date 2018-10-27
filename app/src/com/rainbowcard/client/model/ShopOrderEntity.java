package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gc on 2017-1-10.
 */
public class ShopOrderEntity implements Serializable{
    @Expose
    @SerializedName("shop_id")
    public String shopId;
    @Expose
    @SerializedName("shop_name")
    public String name;
    @Expose
    @SerializedName("shop_address")
    public String addr;
    @Expose
    @SerializedName("status")
    public int consumption;
    @Expose
    @SerializedName("type_text")
    public String shopType;
    @Expose
    @SerializedName("son_type_text")
    public String sonShopType;
    @Expose
    @SerializedName("money")
    public float shopPrice;
    @Expose
    @SerializedName("pay_money")
    public float payMoney;
    @Expose
    @SerializedName("code_type")
    public String codeType = "数字码";
    @Expose
    @SerializedName("code")
    public String code;
    @Expose
    @SerializedName("time")
    public String date;
    @Expose
    @SerializedName("shop_img")
    public String shopImg;
    @Expose
    @SerializedName("son_type")
    public String sonType;
    @Expose
    @SerializedName("trade_no")
    public String tradeNo;
    @Expose
    @SerializedName("card_no")
    public String cardNo;

}
