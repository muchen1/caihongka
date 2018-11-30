package com.rainbowcard.client.ui.viewholder;

import android.view.View;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.InsurancePriceDetailModel;

public class InsurancePriceDetailBottomHolder extends BaseViewHolder<InsurancePriceDetailModel.ChildEntity> {

    private TextView mInsuranceText;
    private TextView mInsurancePrice;

    public InsurancePriceDetailBottomHolder(View itemView) {
        super(itemView);
        mInsuranceText = findView(R.id.item_inprice_detail_groupbottom_name);
        mInsurancePrice = findView(R.id.item_inprice_detail_groupbottom_price);
    }

    public InsurancePriceDetailBottomHolder(View itemView, OnViewHolderClickListener l) {
        super(itemView, l);
        mInsuranceText = findView(R.id.item_inprice_detail_groupbottom_name);
        mInsurancePrice = findView(R.id.item_inprice_detail_groupbottom_price);
    }

    @Override
    public void onBind(int position, InsurancePriceDetailModel.ChildEntity d) {
        mInsurancePrice.setText(d.insurancePrice);
        mInsuranceText.setText(d.commonText);
    }

}
