package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.CashRecordModel;
import com.rainbowcard.client.model.TicketStateModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 18-3-6.
 */
public class CashRecordListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<CashRecordModel.CashRecordEntity> mCashRecordEntitys;


    public CashRecordListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mCashRecordEntitys = new ArrayList<CashRecordModel.CashRecordEntity>();
    }

    public void setCashRecordEntitys(List<CashRecordModel.CashRecordEntity> list) {
        mCashRecordEntitys.clear();
        mCashRecordEntitys.addAll(list);
        notifyDataSetChanged();
    }


    public void addCashRecordEntitys(List<CashRecordModel.CashRecordEntity> list) {
        mCashRecordEntitys.addAll(list);
        notifyDataSetChanged();
    }

    public List<CashRecordModel.CashRecordEntity> getCashRecordEntitys(){
        return  mCashRecordEntitys;
    }
    @Override
    public int getCount() {
        return mCashRecordEntitys.size();
    }

    @Override
    public Object getItem(int i) {
        return mCashRecordEntitys.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.item_cash_record_list, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final CashRecordModel.CashRecordEntity cashRecordEntity = mCashRecordEntitys.get(i);
        holder.dateTv.setText(cashRecordEntity.createdAt);
        if(cashRecordEntity.status == 1) {
            holder.titleTv.setText("退款中");
            holder.countTv.setText(String.format(mContext.getString(R.string.balance_text), cashRecordEntity.money));
            holder.countTv.setTextColor(mContext.getResources().getColor(R.color.app_black));
            holder.icon.setImageResource(R.drawable.me_refund);
        }else if(cashRecordEntity.status == 2){
            holder.titleTv.setText("已到账");
            holder.countTv.setText(String.format(mContext.getString(R.string.balance_text), cashRecordEntity.money));
            holder.countTv.setTextColor(mContext.getResources().getColor(R.color.money_color));
            holder.icon.setImageResource(R.drawable.me_arrival);
        }else {
            holder.titleTv.setText("退款失败");
            holder.countTv.setText(String.format(mContext.getString(R.string.balance_text), cashRecordEntity.money));
            holder.countTv.setTextColor(mContext.getResources().getColor(R.color.app_gray));
            holder.icon.setImageResource(R.drawable.me_refund_failure);
        }

        return view;
    }


    static class ViewHolder {
        @InjectView(R.id.icon)
        ImageView icon;
        @InjectView(R.id.title_text)
        TextView titleTv;
        @InjectView(R.id.date_text)
        TextView dateTv;
        @InjectView(R.id.count_text)
        TextView countTv;
        ViewHolder(View view){
            ButterKnife.inject(this, view);
        }
    }
}
