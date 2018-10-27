package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.CardEntity;
import com.rainbowcard.client.model.RecordEntity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017-1-9.
 */
public class RecordListAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<RecordEntity> mRecordEntitys;

    public RecordListAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mRecordEntitys = new ArrayList<RecordEntity>();
    }

    public void setmRecordEntitys(List<RecordEntity> list){
        mRecordEntitys.clear();
        mRecordEntitys.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mRecordEntitys.size();
    }

    @Override
    public RecordEntity getItem(int position) {
        return mRecordEntitys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.item_record_list,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        RecordEntity recordEntity = mRecordEntitys.get(position);
        viewHolder.numTv.setText(recordEntity.num);
        viewHolder.dateTv.setText(recordEntity.date);
        viewHolder.payTv.setText(new
                DecimalFormat("##,###,###,###,##0.00").format(recordEntity.payMoney));
        viewHolder.rechargeTv.setText(new
                DecimalFormat("##,###,###,###,##0.00").format(recordEntity.recharge));
        viewHolder.payTypeTv.setText(recordEntity.payType);

        return convertView;
    }

    static class ViewHolder{
        @InjectView(R.id.num)
        TextView numTv;
        @InjectView(R.id.date)
        TextView dateTv;
        @InjectView(R.id.recharge)
        TextView rechargeTv;
        @InjectView(R.id.pay)
        TextView payTv;
        @InjectView(R.id.pay_type)
        TextView payTypeTv;
        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
