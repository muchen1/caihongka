package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.InsurancePriceDetailModel;
import com.rainbowcard.client.ui.viewholder.BaseViewHolder;
import com.rainbowcard.client.ui.viewholder.InsurancePriceDetailHeaderHolder;
import com.rainbowcard.client.ui.viewholder.InsurancePriceDetailBottomHolder;
import com.rainbowcard.client.ui.viewholder.InsurancePriceDetailChildHolder;

import java.util.List;

public class InsurancePriceDetailAdapter extends RecyclerView.Adapter<BaseViewHolder> implements BaseViewHolder.OnViewHolderClickListener {

    private static final int TYPE_COUNT = 3;

    public static final int TYPE_HEADER = 1;

    public static final int TYPE_CHILD = 2;

    public static final int TYPE_BOTTOM = 3;

    private List<InsurancePriceDetailModel.BaseEntity> mDatas;

    private Context mContext;
    private LayoutInflater mInflater;

    public InsurancePriceDetailAdapter(Context context, List<InsurancePriceDetailModel.BaseEntity> datats) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        mDatas = datats;

    }

    public InsurancePriceDetailAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<InsurancePriceDetailModel.BaseEntity> data) {
        mDatas = data;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                View headerView = mInflater.inflate(R.layout.item_inprice_detail_groupheader, parent, false);
                return new InsurancePriceDetailHeaderHolder(headerView, this);
            case TYPE_CHILD:
                View childView = mInflater.inflate(R.layout.item_inprice_detail_child, parent, false);
                return new InsurancePriceDetailChildHolder(childView, this);
            case TYPE_BOTTOM:
                View bottomView = mInflater.inflate(R.layout.item_inprice_detail_groupbottom, parent, false);
                return new InsurancePriceDetailBottomHolder(bottomView, this);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.bind(position, mDatas.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        if (mDatas.get(position) != null) {
            return mDatas.get(position).style;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if (mDatas != null) {
            return mDatas.size();
        }
        return 0;
    }

    @Override
    public void onClick(BaseViewHolder viewHolder, int viewId) {

    }

    @Override
    public void setOnLongClick(BaseViewHolder viewHolder, int type) {

    }
}
