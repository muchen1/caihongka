package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.FinanceRecordModel;
import com.rainbowcard.client.utils.DensityUtil;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 18-3-9.
 */
public class FinanceRecordListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<FinanceRecordModel.FinanceRecordEntity> mFinanceRecordEntitys;

    public FinanceRecordListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mFinanceRecordEntitys = new ArrayList<FinanceRecordModel.FinanceRecordEntity>();
    }

    public void setFinanceRecordEntitys(List<FinanceRecordModel.FinanceRecordEntity> list) {
        mFinanceRecordEntitys.clear();
        mFinanceRecordEntitys.addAll(list);
        notifyDataSetChanged();
    }

    public void addFinanceRecordEntitys(List<FinanceRecordModel.FinanceRecordEntity> list) {
        mFinanceRecordEntitys.addAll(list);
        notifyDataSetChanged();
    }

    public List<FinanceRecordModel.FinanceRecordEntity> getFinanceRecordEntitys(){
        return  mFinanceRecordEntitys;
    }
    @Override
    public int getCount() {
        return mFinanceRecordEntitys.size();
    }

    @Override
    public Object getItem(int i) {
        return mFinanceRecordEntitys.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.item_finance_record_list, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final FinanceRecordModel.FinanceRecordEntity financeRecordEntity = mFinanceRecordEntitys.get(i);

        holder.numberTv.setText(String.format(mContext.getString(R.string.order_text),financeRecordEntity.tradeNo));
        switch (financeRecordEntity.status){
            case 2:
                holder.statusTv.setText("未到期");
                break;
            case 3:
                holder.statusTv.setText("已到期");
        }
        holder.priceTv.setText(String.format(mContext.getString(R.string.buy_text),new
                DecimalFormat("##,###,###,###,##0.00").format(financeRecordEntity.money)));
        holder.periodTv.setText(String.format(mContext.getString(R.string.period_text2),financeRecordEntity.period));
        holder.countTv.setText(String.format(mContext.getString(R.string.count_text1),financeRecordEntity.financeCoupon));
        holder.expireDateTv.setText(String.format(mContext.getString(R.string.expire_text),financeRecordEntity.lockAt));
        holder.createDateTv.setText(financeRecordEntity.createdAt);

        if (!TextUtils.isEmpty(financeRecordEntity.ticketIcon)) {
            Picasso.with(mContext)
                    .load(String.format(mContext.getString(R.string.img_url),financeRecordEntity.ticketIcon))
                    .resize(DensityUtil.dip2px(mContext,70),DensityUtil.dip2px(mContext,70))
                    .centerCrop()
                    .placeholder(R.drawable.order_default)
                    .error(R.drawable.order_default).into(holder.ticketIcon);
        }else {
            holder.ticketIcon.setImageResource(R.drawable.ticket);
        }

        return view;
    }


    static class ViewHolder {
        @InjectView(R.id.tv_number)
        TextView numberTv;
        @InjectView(R.id.tv_status)
        TextView statusTv;
        @InjectView(R.id.ticket_icon)
        ImageView ticketIcon;
        @InjectView(R.id.tv_price)
        TextView priceTv;
        @InjectView(R.id.tv_period)
        TextView periodTv;
        @InjectView(R.id.tv_count)
        TextView countTv;
        @InjectView(R.id.tv_expire_date)
        TextView expireDateTv;
        @InjectView(R.id.tv_create_date)
        TextView createDateTv;
        ViewHolder(View view){
            ButterKnife.inject(this, view);
        }
    }
}
