package com.rainbowcard.client.model;

import android.util.SparseArray;

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

    @Expose
    @SerializedName("list_data")
    public List<PriceEntity> listData;

    @Expose
    @SerializedName("userinfo")
    public UserInfoEntity userInfoEntity;


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

    public static class UserInfoEntity {

        @Expose
        @SerializedName("CredentislasNum")
        public String credentislasNum;

        @Expose
        @SerializedName("IdType")
        public int idType;

        @Expose
        @SerializedName("ForceExpireDate")
        public String forceExpireDate;

        @Expose
        @SerializedName("RateFactor4")
        public int rateFactor4;

        @Expose
        @SerializedName("RateFactor2")
        public float rateFactor2;

        @Expose
        @SerializedName("RateFactor3")
        public float rateFactor3;

        @Expose
        @SerializedName("RateFactor1")
        public float rateFactor1;

        @Expose
        @SerializedName("HolderIdCard")
        public String holderIdCard;

        @Expose
        @SerializedName("PurchasePrice")
        public int purchasePrice;

        @Expose
        @SerializedName("NextBusinessStartDate")
        public String nextBusinessStartDate;

        @Expose
        @SerializedName("LicenseColor")
        public int licenseColor;

        @Expose
        @SerializedName("RegisterDate")
        public String registerDate;

        @Expose
        @SerializedName("NextForceStartDate")
        public String nextForceStartDate;

        @Expose
        @SerializedName("ModleName")
        public String modleName;

        @Expose
        @SerializedName("InsuredName")
        public String insuredName;

        @Expose
        @SerializedName("BusinessExpireDate")
        public String businessExpireDate;

        @Expose
        @SerializedName("PostedName")
        public String postedName;

        @Expose
        @SerializedName("FuelType")
        public int fuelType;

        @Expose
        @SerializedName("LicenseNo")
        public String licenseNo;

        @Expose
        @SerializedName("CarUsedType")
        public int carUsedType;

        @Expose
        @SerializedName("InsuredIdType")
        public int insuredIdType;

        @Expose
        @SerializedName("ProofType")
        public int proofType;

        @Expose
        @SerializedName("HolderIdType")
        public int holderIdType;

        @Expose
        @SerializedName("ClauseType")
        public int clauseType;

        @Expose
        @SerializedName("LicenseOwner")
        public String licenseOwner;

        @Expose
        @SerializedName("InsuredMobile")
        public String insuredMobile;

        @Expose
        @SerializedName("CityCode")
        public int cityCode;

        @Expose
        @SerializedName("RunRegion")
        public int runRegion;

        @Expose
        @SerializedName("HolderMobile")
        public String holderMobile;

        @Expose
        @SerializedName("IsPublic")
        public int isPublic;

        @Expose
        @SerializedName("SeatCount")
        public int seatCount;

        @Expose
        @SerializedName("EngineNo")
        public String engineNo;

        @Expose
        @SerializedName("CarVin")
        public String CarVin;

        @Expose
        @SerializedName("InsuredIdCard")
        public String insuredIdCard;
    }
}
