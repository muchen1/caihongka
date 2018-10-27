package com.rainbowcard.client.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.rainbowcard.client.R;
import com.rainbowcard.client.common.model.PayType;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by kleist on 14-10-16.
 */
public class PayTypeAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int mCurrentItem;
    private List<PayType> mPayTypes;

    public PayTypeAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mCurrentItem = -1;
        mPayTypes = new ArrayList<PayType>();
    }

    public void setPayTypes(List<PayType> payTypes) {
        mPayTypes.clear();
        mPayTypes.addAll(payTypes);
        notifyDataSetChanged();
    }

    public int getCurrentItem() {
        return mCurrentItem;
    }

    public void setCurrentItem(int item) {
        mCurrentItem = item;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mPayTypes.size();
    }

    @Override
    public Object getItem(int position) {
        return mPayTypes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_pay_type, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PayType payType = (PayType) getItem(position);
        holder.mIvIcon.setImageResource(payType.icon);
        holder.mTvName.setText(payType.name);

        if(position == 1){
            holder.mIconFlag.setVisibility(View.VISIBLE);
        }else {
            holder.mIconFlag.setVisibility(View.GONE);
        }

        if (position == mCurrentItem) {
            holder.mIvChecked.setImageResource(R.drawable.xuanzhong);
        } else {
            holder.mIvChecked.setImageResource(R.drawable.moren);
        }

        return convertView;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_pay_type.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Inmite Developers (http://inmite.github.io)
     */
    static class ViewHolder {
        @InjectView(R.id.iv_icon)
        ImageView mIvIcon;
        @InjectView(R.id.tv_name)
        TextView mTvName;
        @InjectView(R.id.iv_checked)
        ImageView mIvChecked;
        @InjectView(R.id.icon_flag)
        ImageView mIconFlag;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
