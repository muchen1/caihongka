package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.DiscountEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 17-3-16.
 */
public class UsableDiscountListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<DiscountEntity> mDiscountEntitys;

    public UsableDiscountListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mDiscountEntitys = new ArrayList<DiscountEntity>();
    }

    public void setDiscountEntitys(List<DiscountEntity> list) {
        mDiscountEntitys.clear();
        mDiscountEntitys.addAll(list);
        notifyDataSetChanged();
    }

    public void addDiscountEntitys(List<DiscountEntity> list) {
        mDiscountEntitys.addAll(list);
        notifyDataSetChanged();
    }

    public List<DiscountEntity> getDiscountEntitys(){
        return  mDiscountEntitys;
    }
    @Override
    public int getCount() {
        return mDiscountEntitys.size();
    }

    @Override
    public Object getItem(int i) {
        return mDiscountEntitys.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.item_usable_discount_list, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final DiscountEntity discountEntity = mDiscountEntitys.get(i);
        holder.nameTv.setText(discountEntity.title);
        holder.dateTv.setText(String.format(mContext.getString(R.string.discount_date),discountEntity.startTime,discountEntity.endTime));
        switch (discountEntity.moneyType){
            case 1:
                holder.moneyTv.setText(String.format(mContext.getString(R.string.price),discountEntity.money));
                break;
            case 2:
                holder.moneyTv.setText(String.format(mContext.getString(R.string.sub_break),discountEntity.money));
                break;
        }
        if(discountEntity.willExpired){
            holder.pastTv.setVisibility(View.VISIBLE);
        }else {
            holder.pastTv.setVisibility(View.GONE);
        }
        holder.restrictTv.setText(String.format(mContext.getString(R.string.usable),discountEntity.usedMoney));
        return view;
    }


    static class ViewHolder {
        @InjectView(R.id.tv_name)
        TextView nameTv;
        @InjectView(R.id.tv_date)
        TextView dateTv;
        @InjectView(R.id.tv_money)
        TextView moneyTv;
        @InjectView(R.id.tv_restrict)
        TextView restrictTv;
        @InjectView(R.id.tv_past)
        TextView pastTv;
        ViewHolder(View view){
            ButterKnife.inject(this, view);
        }
    }
}
