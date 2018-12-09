package com.rainbowcard.client.ui.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.InsurancePriceDetailModel;

public class InsurancePriceDetailHeaderHolder extends BaseViewHolder<InsurancePriceDetailModel.HeaderEntity> implements View.OnClickListener {

    private TextView mHeaderTextView;
    private TextView mEditBt;
    private ImageView mEditIcon;

    public InsurancePriceDetailHeaderHolder(View itemView) {
        super(itemView);
        mHeaderTextView = findView(R.id.item_inprice_detail_groupheader_header);
        mEditBt = findView(R.id.item_inprice_detail_groupheader_edit);
        mEditIcon = findView(R.id.item_inprice_detail_groupheader_editicon);

        mEditIcon.setOnClickListener(this);
        mEditBt.setOnClickListener(this);
    }

    public InsurancePriceDetailHeaderHolder(View itemView, OnViewHolderClickListener l) {
        super(itemView, l);
        mHeaderTextView = findView(R.id.item_inprice_detail_groupheader_header);
        mEditBt = findView(R.id.item_inprice_detail_groupheader_edit);
        mEditIcon = findView(R.id.item_inprice_detail_groupheader_editicon);

        mEditIcon.setOnClickListener(this);
        mEditBt.setOnClickListener(this);
    }

    @Override
    public void onBind(int position, InsurancePriceDetailModel.HeaderEntity d) {
        mHeaderTextView.setText(String.format(mHeaderTextView.getContext().getString(R.string
                .insurance_price_detail_header_string_format), d.commonText, d.insuranceDate));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_inprice_detail_groupheader_edit:
            case R.id.item_inprice_detail_groupheader_editicon:
                break;
        }
    }
}
