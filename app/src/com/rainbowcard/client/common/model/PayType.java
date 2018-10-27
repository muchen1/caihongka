package com.rainbowcard.client.common.model;


import com.rainbowcard.client.R;

/**
 * Created by gc on 14-10-16.
 */
public class PayType {

    public final static int TYPE_ALIPAY = 0;
    public final static int TYPE_WEIXIN = 1;
    public final static int TYPE_BESTPAY = 2;
    public final static int TYPE_BANK = 3;


    public String name;
    public int icon;
    public int type;

    public PayType(int type) {
       switch (type) {
           case TYPE_ALIPAY:
               name = "支付宝支付";
               icon = R.drawable.user_zf;
               break;
           case TYPE_WEIXIN:
               name = "微信支付";
               icon = R.drawable.user_wx;
               break;
           case TYPE_BESTPAY:
               name = "翼支付（推荐使用）";
               icon = R.drawable.user_wx;
               break;
           case TYPE_BANK:
               name = "银行汇款";
               icon = R.drawable.user_bank;
               break;
           default:
               throw new IllegalArgumentException("Illegal type");
       }

       this.type = type;
    }

}
