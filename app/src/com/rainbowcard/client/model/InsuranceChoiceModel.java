package com.rainbowcard.client.model;

import android.util.SparseArray;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class InsuranceChoiceModel implements Serializable {

    public static String INTENT_KEY_FOR_THIS = "InsuranceChoiceModel";

    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public List<BaseEntity> data;

    /**
     * 基础entity，为抽象类
     */
    public static abstract class BaseEntity {

        // 表示样式，0表示header，1表示child样式
        @Expose
        @SerializedName("style")
        public int style;

        // 表示文案
        @Expose
        @SerializedName("insurance_name")
        public String insuranceName;

    }

    /**
     * header对应的entity
     */
    public static class HeaderItemEntity extends BaseEntity {

        // 表示本次续保的生效日期
        @Expose
        @SerializedName("insurance_switch")
        public boolean isuranceSwitch;
    }

    /**
     * 带有switch开关的itemEntity
     */
    public static class ChildItemEntity extends BaseEntity {

        // 表示该保险的key
        @Expose
        @SerializedName("insurance_key")
        public String insuranceKey;

        // 表示当前item是否选择了投保的总开关状体
        @Expose
        @SerializedName("insurance_status")
        public boolean insuranceStatus;

        // 表示上一年的保险状态的key值
        @Expose
        @SerializedName("insurance_statustextkey")
        public int insuranceStatusTextKey;

        // 表示上不计免赔是否展示0表示为选中状态，1表示选中状态，2表示不显示状态
        @Expose
        @SerializedName("insurance_bjmp_status")
        public int insuranceBjmpStatus;

        // 表示不计免赔的key
        @Expose
        @SerializedName("insurance_bjmpkey")
        public String insuranceBjmpKey;

        // 表示该保险可选择的投保金额
        @Expose
        @SerializedName("insurance_allprice")
        public SparseArray<String> insuranceAllPrice;


    }
}
