package com.rainbowcard.client.ui.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.InsuranceChoiceModel;
import com.rainbowcard.client.ui.viewholder.InsuranceChoiceChildItemHolder;
import com.rainbowcard.client.ui.viewholder.InsuranceChoiceHeaderItemHolder;
import com.rainbowcard.client.widget.TextChoiceDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InsuranceChoiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        InsuranceChoiceChildItemHolder.ChildItemChoiceClickListener,
        InsuranceChoiceHeaderItemHolder.HeaderItemSwitchClickListener {

    public static final int ITEM_TYPE_HEADER = 0;
    public static final int ITEM_TYPE_CHILD = 1;

    private List<InsuranceChoiceModel.BaseEntity> mData = new ArrayList<>();
    // 其实是每个header下的子item;key是父元素item的title；value是对应的子list
    private List<InsuranceChoiceModel.BaseEntity> mChildDataDel = new ArrayList<>();

    private Context mContext;
    private LayoutInflater mInflater;

    public InsuranceChoiceAdapter(Context context, List<InsuranceChoiceModel.BaseEntity> datats) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        mData = datats;
//        parseData();

    }

    public InsuranceChoiceAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<InsuranceChoiceModel.BaseEntity> data) {
        mData = data;
//        parseData();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                View headerView = mInflater.inflate(R.layout.item_insurance_choice_header, parent, false);
                return new InsuranceChoiceHeaderItemHolder(headerView, this);
            case ITEM_TYPE_CHILD:
                View childView = mInflater.inflate(R.layout.item_insurance_choice_spinner, parent, false);
                return new InsuranceChoiceChildItemHolder(childView, this);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM_TYPE_HEADER:
                ((InsuranceChoiceHeaderItemHolder) holder).onBind(
                        (InsuranceChoiceModel.HeaderItemEntity) mData.get(position));
            case ITEM_TYPE_CHILD:
                ((InsuranceChoiceChildItemHolder) holder).onBind(
                        (InsuranceChoiceModel.ChildItemEntity) mData.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position) != null) {
            return mData.get(position).style;
        }
        return super.getItemViewType(position);
    }

    public List<InsuranceChoiceModel.BaseEntity> getmData() {
        return mData;
    }

    @Override
    public void onClick(final InsuranceChoiceModel.ChildItemEntity entity, int viewId) {
        switch (viewId) {
            case R.id.item_child_choice_text:
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

    @Override
    public void onCheckedChanged(boolean isChecked, InsuranceChoiceModel.BaseEntity entity) {
        // 如果由选中到未选中，则移除item；如果从未选择到选中则增加item
        // 如果是商业险
        if (mData.indexOf(entity) == 1) {
//            parseData();
            notifyDataSetChanged();
        }
    }

    private void parseData() {

        // 如果商业保险什么都没有选的话
        InsuranceChoiceModel.HeaderItemEntity entity = (InsuranceChoiceModel.HeaderItemEntity) mData.get(1);

        if (entity.style == ITEM_TYPE_HEADER && !entity.isuranceSwitch) {
            mChildDataDel.addAll(mData.subList(2, mData.size()));
            mData.removeAll(mChildDataDel);
        } else if (entity.style == ITEM_TYPE_HEADER && entity.isuranceSwitch) {
            mData.addAll(mChildDataDel);
            mChildDataDel.clear();
        }
//        InsuranceChoiceModel.BaseEntity beginEntity = null;
//        InsuranceChoiceModel.BaseEntity endEntity = null;
//
//        // data变了之后先对data进行分析一下，对mChildDataDel进行赋值
//        for (int i = 0; i < mData.size(); i++) {
//            for (int j = i; j < mData.size(); j++) {
//                InsuranceChoiceModel.BaseEntity entity = mData.get(i);
//                if (entity.style == ITEM_TYPE_HEADER) {
//                    if (beginEntity == null) {
//                        beginEntity = entity;
//                    } else {
//                        endEntity = entity;
//                    }
//                }
//                if (beginEntity != null && endEntity != null) {
//                    break;
//                }
//            }
//
//        }
    }

}
