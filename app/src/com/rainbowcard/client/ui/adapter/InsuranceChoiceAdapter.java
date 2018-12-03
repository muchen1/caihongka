package com.rainbowcard.client.ui.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.InsuranceChoiceModel;
import com.rainbowcard.client.ui.viewholder.InsuranceChoiceChildItemHolder;

import java.util.ArrayList;
import java.util.List;

public class InsuranceChoiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        InsuranceChoiceChildItemHolder.ChildItemChoiceClickListener {

    private List<InsuranceChoiceModel.ChildItemEntity> mData = new ArrayList<>();

    private Context mContext;
    private LayoutInflater mInflater;

    public InsuranceChoiceAdapter(Context context, List<InsuranceChoiceModel.ChildItemEntity> datats) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        mData = datats;

    }

    public InsuranceChoiceAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<InsuranceChoiceModel.ChildItemEntity> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View childView = mInflater.inflate(R.layout.item_insurance_choice_spinner, parent, false);
        return new InsuranceChoiceChildItemHolder(childView, this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((InsuranceChoiceChildItemHolder) holder).onBind(
                (InsuranceChoiceModel.ChildItemEntity) mData.get(position));
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public List<InsuranceChoiceModel.ChildItemEntity> getmData() {
        return mData;
    }

    @Override
    public void onClick(final InsuranceChoiceModel.ChildItemEntity entity, int viewId) {
        switch (viewId) {
            case R.id.item_child_choice_text:
                notifyItemChanged(mData.indexOf(entity));
                // break
                break;
            case R.id.item_insurance_choice_child_rootview:
                // 点击整个item，回调回来之后进行notify
                notifyItemChanged(mData.indexOf(entity));
                break;
            case R.id.item_child_textview_bjmp:
                notifyItemChanged(mData.indexOf(entity));
                break;
            default:
                break;
        }
    }

}
