package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 报价页面的数据结构
 */
public class InsurancePriceModel {

    @Expose
    @SerializedName("status")
    public int status;
    // header中的汽车牌号
    @Expose
    @SerializedName("car_num")
    public String carNum;

    // header中的被保人
    @Expose
    @SerializedName("car_person")
    public String carPerson;

    // header中的汽车类型
    @Expose
    @SerializedName("car_type")
    public String carType;

    // header 中的交强险起始日期
    @Expose
    @SerializedName("carJqxDate")
    public String carJqxDate;

    // header 中的商业险起始日期
    @Expose
    @SerializedName("carSyxDate")
    public String carSyxDate;

    // middle 中已经选择的险种数量
    @Expose
    @SerializedName("insuranceNum")
    public String insuranceNum;

    // middle 中已经选择的险种明细
    @Expose
    @SerializedName("insuranceText")
    public String insuranceText;


    @Expose
    @SerializedName("list_data")
    public List<PriceEntity> listData;

    /**
     * 基础entity，为抽象类
     */
    public static class PriceEntity {

        // 表示保险公司的icon
        @Expose
        @SerializedName("iconurl")
        public String iconurl;

        // 表示保险公司名称
        @Expose
        @SerializedName("company_name")
        public String companyName;


        // 表示保险公司的唯一标识，用来请求价格
        @Expose
        @SerializedName("company_id")
        public int companyId;

        // 表示展示结果页还是loading页
        @Expose
        @SerializedName("show_result")
        public boolean showResult;

        // 价格折扣
        @Expose
        @SerializedName("price_discout")
        public String priceDiscout;

        // 新价格
        @Expose
        @SerializedName("price_new")
        public String priceNew;

        // 旧价格
        @Expose
        @SerializedName("price_old")
        public String priceOld;
    }
}
