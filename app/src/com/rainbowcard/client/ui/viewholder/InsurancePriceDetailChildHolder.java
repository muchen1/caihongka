package com.rainbowcard.client.ui.viewholder;

import android.view.View;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.InsurancePriceDetailModel;

public class InsurancePriceDetailChildHolder extends BaseViewHolder<InsurancePriceDetailModel.ChildEntity> {

    private TextView mInsuranceText;
    private TextView mInsurancePrice;

    public InsurancePriceDetailChildHolder(View itemView) {
        super(itemView);
        mInsurancePrice = findView(R.id.item_inprice_detail_child_price);
        mInsuranceText = findView(R.id.item_inprice_detail_child_name);
    }

    public InsurancePriceDetailChildHolder(View itemView, OnViewHolderClickListener l) {
        super(itemView, l);
        mInsurancePrice = findView(R.id.item_inprice_detail_child_price);
        mInsuranceText = findView(R.id.item_inprice_detail_child_name);
    }

    @Override
    public void onBind(int position, InsurancePriceDetailModel.ChildEntity d) {
        mInsuranceText.setText(d.commonText);
        mInsurancePrice.setText(d.insurancePrice);
    }
}
