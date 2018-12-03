package com.rainbowcard.client.ui.viewholder;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.InsurancePriceModel;
import com.rainbowcard.client.ui.ShopDetailActivity;
import com.squareup.picasso.Picasso;

public class InsurancePriceListHolder extends BaseViewHolder<InsurancePriceModel.PriceEntity>
        implements View.OnClickListener {

    // 保险公司的icon
    private ImageView mInsuranceCompanyIcon;
    // loading布局
    private ViewGroup mLoadingLayout;
    // 请求完的结果布局
    private ViewGroup mResultLayout;
    // 结果布局中的折扣价格
    private TextView mResultDiscoutText;
    // 结果布局中的新价格
    private TextView mResultNewPriceText;
    // 结果布局中的旧价格
    private TextView mResultOldPriceText;

    public InsurancePriceListHolder(View itemView) {
        super(itemView);
        mInsuranceCompanyIcon = findView(R.id.item_insurance_price_companyicon);
        mLoadingLayout = findView(R.id.item_insurance_price_loading);
        mResultLayout = findView(R.id.item_insurance_price_result);
        mResultDiscoutText = findView(R.id.item_insurance_price_discount);
        mResultNewPriceText = findView(R.id.item_insurance_price_new);
        mResultOldPriceText = findView(R.id.item_insurance_price_old);

        mResultLayout.setOnClickListener(this);

    }

    @Override
    public void onBind(int position, InsurancePriceModel.PriceEntity d) {
        // todo icon没有加入
        Log.e("daipeng", "onBind=" + d.showResult);
        Picasso.with(mInsuranceCompanyIcon.getContext())
                .load(d.iconurl)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.detail_default)
                .error(R.drawable.detail_default).into(mInsuranceCompanyIcon);
        mLoadingLayout.setVisibility(d.showResult ? View.INVISIBLE : View.VISIBLE);
        mResultLayout.setVisibility(d.showResult ? View.VISIBLE : View.INVISIBLE);
        mResultOldPriceText.setText(d.priceOld);
        mResultNewPriceText.setText(d.priceNew);
        mResultDiscoutText.setText(d.priceDiscout);
    }

    @Override
    public void onClick(View v) {
        if (onViewHolderClickListener != null) {
            onViewHolderClickListener.onClick(InsurancePriceListHolder.this, v.getId());
        }
        switch (v.getId()) {
            case R.id.item_insurance_price_result:
        }
    }
}
