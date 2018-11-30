package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 报价详情页的model
 */
public class InsurancePriceDetailModel {

    @Expose
    @SerializedName("status")
    public int status;

    @Expose
    @SerializedName("company_icon")
    public String companyIcon;

    @Expose
    @SerializedName("price_new")
    public int priceNew;

    @Expose
    @SerializedName("price_old")
    public int priceOld;

    @Expose
    @SerializedName("price_datas")
    public List<BaseEntity> priceDatas;



    public abstract class BaseEntity {

        // 表示样式，1表示header，2表示child样式,3表示bottom
        @Expose
        @SerializedName("style")
        public int style;

        // 表示文案
        @Expose
        @SerializedName("common_text")
        public String commonText;
    }

    public class HeaderEntity extends BaseEntity {

        // 表示保险起始日期
        @Expose
        @SerializedName("insurance_date")
        public String insuranceDate;

    }

    public class ChildEntity extends BaseEntity {

        // 表示对应险种价格
        // 表示保险起始日期
        @Expose
        @SerializedName("insurance_price")
        public String insurancePrice;
    }
}

