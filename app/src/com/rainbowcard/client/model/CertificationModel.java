package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gc on 2018-3-8.
 */
public class CertificationModel {

    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("data")
    public CertificationEntivity data;

    public class CertificationEntivity{
        @Expose
        @SerializedName("need_certification")
        public int needCertification;   //0不需要验证1需要实名验证
        @Expose
        @SerializedName("need_money")
        public float needMoney;      //需要多少钱需要验证
    }
}
