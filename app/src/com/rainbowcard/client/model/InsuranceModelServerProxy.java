package com.rainbowcard.client.model;

import android.text.TextUtils;

/**
 * 由于服务端接口比较大，在一次请求中把险种选择页面，报价页面的所需信息都返回了，所以做一个代理，
 * 从这个类请求保险各个页面所需要的信息
 */
public class InsuranceModelServerProxy {

    private static volatile InsuranceModelServerProxy mInstance;

    // 当前类保存的保险信息对应的车牌号
    private String mCarNum;

    // 保险选择页面的所需数据
    private InsuranceChoiceModel mInsuranceChoiceModel;

    // 报价页面所需数据
    private InsurancePriceModel mInsurancePriceModel;

    // 报价详情页面所需数据
    private InsurancePriceDetailModel mInsurancePriceDetailModel;

    private InsuranceModelServerProxy() {

    }

    public static InsuranceModelServerProxy getInstance() {
        if (mInstance == null) {
            synchronized (InsuranceModelServerProxy.class) {
                if (mInstance == null) {
                    mInstance = new InsuranceModelServerProxy();
                }
            }
        }
        return mInstance;
    }

    /**
     * 设置该车牌对应的保险选择数据及保险报价数据
     * @param carNum
     * @param model
     * @param priceModel
     */
    public void setModel(String carNum, InsuranceChoiceModel model, InsurancePriceModel priceModel) {
        this.mInsuranceChoiceModel = model;
        this.mCarNum = carNum;
        this.mInsurancePriceModel = priceModel;
    }

    /**
     * 获取保险选择页面的数据
     * @return
     */
    public InsuranceChoiceModel getInsuranceChoiceModel(String carNum) {
        if (TextUtils.equals(carNum, mCarNum)) {
            // 如果carNum没有变化，则直接返回内存数据
            return mInsuranceChoiceModel;
        }
        return null;
    }

    /**
     * 获取报价页面的数据
     * @return
     */
    public InsurancePriceModel getInsurancePriceModel(String carNum) {
        if (TextUtils.equals(carNum, mCarNum)) {
            return mInsurancePriceModel;
        }
        return null;
    }
}
