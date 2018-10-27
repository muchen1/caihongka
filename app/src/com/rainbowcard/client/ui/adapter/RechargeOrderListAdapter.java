package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.RecordEntity;
import com.rainbowcard.client.utils.Validation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 17-3-23.
 */
public class RechargeOrderListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<RecordEntity> mRecordEntitys;
    private String mKeyword;
    private SetOrderListener setOrderListener;
    private int status = 0;

    public RechargeOrderListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mRecordEntitys = new ArrayList<RecordEntity>();
    }
    public void setMPosition(int position) {
        this.status = position;
        notifyDataSetChanged();
    }
    public RechargeOrderListAdapter(Context context,SetOrderListener listener) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mRecordEntitys = new ArrayList<RecordEntity>();
        setOrderListener = listener;
    }

    public void setRecordEntitys(List<RecordEntity> list) {
        mRecordEntitys.clear();
        mRecordEntitys.addAll(list);
        notifyDataSetChanged();
    }

    public void addRecordEntitys(List<RecordEntity> list) {
        mRecordEntitys.addAll(list);
        notifyDataSetChanged();
    }

    public List<RecordEntity> getRecordEntitys(){
        return  mRecordEntitys;
    }
    @Override
    public int getCount() {
        return mRecordEntitys.size();
    }

    @Override
    public Object getItem(int i) {
        return mRecordEntitys.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.item_recharge_order_list, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final RecordEntity recordEntity = mRecordEntitys.get(i);
        if(status == 0) {
            if (Validation.isMobile(recordEntity.num)) {
                holder.nameTv.setText("账户余额");
            } else {
                holder.nameTv.setText(String.format(mContext.getString(R.string.rainbow_no), recordEntity.num));
            }
            holder.rechargeTv.setText("充值账户：");
            holder.priceTv.setText("充值金额：");
        }else {
            holder.nameTv.setText(recordEntity.userName);
            holder.rechargeTv.setText("领卡人：");
            holder.priceTv.setText("卡面金额：");
        }

        holder.price.setText(String.format(mContext.getString(R.string.price),new
                DecimalFormat("##,###,###,###,##0.00").format(recordEntity.recharge)));
        holder.payMoney.setText(String.format(mContext.getString(R.string.price),new
                DecimalFormat("##,###,###,###,##0.00").format(recordEntity.payMoney)));
        holder.discount.setText(String.format(mContext.getString(R.string.sub_price),new
                DecimalFormat("##,###,###,###,##0.00").format(recordEntity.couponMoney)));
        holder.dateTv.setText(recordEntity.date);
        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOrderListener.cancel(i);
            }
        });
        holder.payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOrderListener.pay(i);
            }
        });

        switch (recordEntity.status){
            case 1:
                holder.statusTv.setText("待支付");
                holder.cancelBtn.setVisibility(View.VISIBLE);
                holder.payBtn.setVisibility(View.VISIBLE);
                holder.needText.setText("需付款：");
                break;
            case 2:
                if(status == 0) {
                    holder.statusTv.setText("充值成功");
                }else {
                    holder.statusTv.setText("领取成功");
                }
                holder.cancelBtn.setVisibility(View.GONE);
                holder.payBtn.setVisibility(View.GONE);
                holder.needText.setText("实付款：");
                break;
        }



        return view;
    }


    static class ViewHolder {
        @InjectView(R.id.tv_name)
        TextView nameTv;
        @InjectView(R.id.recharge_text)
        TextView rechargeTv;
        @InjectView(R.id.price_text)
        TextView priceTv;
        @InjectView(R.id.status)
        TextView statusTv;
        @InjectView(R.id.price)
        TextView price;
        @InjectView(R.id.discount)
        TextView discount;
        @InjectView(R.id.pay_money)
        TextView payMoney;
        @InjectView(R.id.cancel_btn)
        TextView cancelBtn;
        @InjectView(R.id.date)
        TextView dateTv;
        @InjectView(R.id.pay_btn)
        TextView payBtn;
        @InjectView(R.id.need_text)
        TextView needText;
        ViewHolder(View view){
            ButterKnife.inject(this, view);
        }
    }

    public interface SetOrderListener{
        public void pay(int position);
        public void cancel(int position);
    }
}
