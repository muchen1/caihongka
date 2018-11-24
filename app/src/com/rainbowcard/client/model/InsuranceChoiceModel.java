package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
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
    public abstract class BaseEntity {

        // 表示样式，0表示header，1表示child样式
        @Expose
        @SerializedName("style")
        public int style;

        // 表示文案
        @Expose
        @SerializedName("insurance_type")
        public String insuranceType;

    }

    /**
     * header对应的entity
     */
    public class HeaderItemEntity extends BaseEntity {

        // 表示本次续保的生效日期
        @Expose
        @SerializedName("insurance_switch")
        public boolean isuranceSwitch;
    }

    /**
     * 带有switch开关的itemEntity
     */
    public class ChildItemEntity extends BaseEntity {

        // 表示当前item是否选择了投保的总开关状体
        @Expose
        @SerializedName("insurance_status")
        public boolean insuranceStatus;

        // 表示上一年的保险状态
        @Expose
        @SerializedName("insurance_statustext")
        public String insuranceStatusText;

        // 表示上不计免赔是否展示
        @Expose
        @SerializedName("insurance_bjmp")
        public boolean insuranceBjmp;

        // 表示该保险可选择的投保金额
        @Expose
        @SerializedName("insurance_allprice")
        public String[] insuranceAllPrice;

    }
}
