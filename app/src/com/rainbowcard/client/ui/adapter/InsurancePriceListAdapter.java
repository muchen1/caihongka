package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.InsurancePriceModel;
import com.rainbowcard.client.ui.viewholder.BaseViewHolder;
import com.rainbowcard.client.ui.viewholder.InsurancePriceListHolder;

import java.util.List;

public class InsurancePriceListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<InsurancePriceModel.PriceEntity> mDatas;
    private Context mContext;
    private LayoutInflater mInflater;

    public InsurancePriceListAdapter(Context context, List<InsurancePriceModel.PriceEntity> data) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        mDatas = data;
    }

    public InsurancePriceListAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<InsurancePriceModel.PriceEntity> data) {
        mDatas = data;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_insurance_price, parent, false);
        return new InsurancePriceListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.bind(position, mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        if (mDatas != null) {
            mDatas.size();
        }
        return 0;
    }

}
