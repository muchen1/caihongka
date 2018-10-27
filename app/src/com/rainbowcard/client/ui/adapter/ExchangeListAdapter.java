package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.model.ExchangeEntity;
import com.rainbowcard.client.model.HotGoodEntity;
import com.rainbowcard.client.ui.GoodsDetailActivity;
import com.rainbowcard.client.utils.DensityUtil;
import com.rainbowcard.client.utils.StringUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.rainbowcard.client.R.string.cart_money;

/**
 * Created by gc on 2017-12-7.
 */
public class ExchangeListAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<ExchangeEntity> mExchangeEntitys;

    public ExchangeListAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mExchangeEntitys = new ArrayList<ExchangeEntity>();
    }

    public void setExchangeEntitys(List<ExchangeEntity> list){
        mExchangeEntitys.clear();
        mExchangeEntitys.addAll(list);
        notifyDataSetChanged();
    }

    public List<ExchangeEntity> getExchangeEntitys(){
        return  mExchangeEntitys;
    }


    @Override
    public int getCount() {
        return mExchangeEntitys.size();
    }

    @Override
    public ExchangeEntity getItem(int position) {
        return mExchangeEntitys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.item_exchange_list,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final ExchangeEntity exchangeEntity = mExchangeEntitys.get(position);

        if (!TextUtils.isEmpty(exchangeEntity.goodsImg)) {
            Picasso.with(mContext)
                    .load(StringUtil.getUrl(exchangeEntity.goodsImg,mContext))
                    .resize(DensityUtil.dip2px(mContext,110),DensityUtil.dip2px(mContext,62))
                    .centerCrop()
                    .placeholder(R.drawable.order_default)
                    .error(R.drawable.order_default).into(viewHolder.image);
        }else {
            viewHolder.image.setImageResource(R.drawable.order_default);
        }
        viewHolder.nameTv.setText(exchangeEntity.goodsTitle);

        viewHolder.date.setText(String.format(mContext.getString(R.string.exchange_data),exchangeEntity.createdAt));


        return convertView;
    }

    static class ViewHolder{
        @InjectView(R.id.image)
        ImageView image;
        @InjectView(R.id.name)
        TextView nameTv;
        @InjectView(R.id.date)
        TextView date;
        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
