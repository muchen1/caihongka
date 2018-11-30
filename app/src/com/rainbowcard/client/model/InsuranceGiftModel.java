package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InsuranceGiftModel {

    @Expose
    @SerializedName("status")
    public int status;

    // 赠送礼物清单
    @Expose
    @SerializedName("gift_data")
    public List<String> giftData;

    // 保单原始金额
    @Expose
    @SerializedName("old_price")
    public String oldPrice;

    // 保单商业险折扣之后价格
    @Expose
    @SerializedName("new_price")
    public String newPrice;

    // 保单交强险价格
    @Expose
    @SerializedName("jqx_price")
    public String jqxPrice;

    // 保单车船税价格
    @Expose
    @SerializedName("ccs_price")
    public String ccsPrice;

    // 保单最终价格
    @Expose
    @SerializedName("final_price")
    public String finalPrice;
}
