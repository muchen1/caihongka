package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.common.model.PayType;
import com.rainbowcard.client.model.ServeTypeEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 17-1-4.
 */
public class ServeTypeAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int mCurrentItem;
    private ArrayList<ServeTypeEntity> mServeTypes;

    public ServeTypeAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mCurrentItem = 0;
        mServeTypes = new ArrayList<ServeTypeEntity>();
    }

    public void setServeTypes(List<ServeTypeEntity> serveTypes) {
        mServeTypes.clear();
        mServeTypes.addAll(serveTypes);
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
        return mServeTypes.size();
    }

    @Override
    public Object getItem(int position) {
        return mServeTypes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<ServeTypeEntity> getServeTypeEntitys(){
        return mServeTypes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_serve_type, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ServeTypeEntity serveTypeEntity = (ServeTypeEntity) getItem(position);
        holder.mTvName.setText(serveTypeEntity.serviceName);
        if(TextUtils.isEmpty(serveTypeEntity.serviceActivity)) {
            holder.activityPrice.setVisibility(View.GONE);
//            if (serveTypeEntity.count.equals("0")) {
//                holder.mTvMoney.setText(String.format(mContext.getString(R.string.price), serveTypeEntity.money));
//            } else {
//                holder.mTvMoney.setText(String.format(mContext.getString(R.string.price_count), serveTypeEntity.money, serveTypeEntity.count));
//            }
            holder.mTvMoney.setText(String.format(mContext.getString(R.string.price), serveTypeEntity.money));
            if ("0".equals(serveTypeEntity.cash)) {
                holder.mTvPrice.setVisibility(View.INVISIBLE);
            } else {
                holder.mTvPrice.setVisibility(View.VISIBLE);
                holder.mTvPrice.setText(String.format(mContext.getString(R.string.cash), serveTypeEntity.cash));
                holder.mTvPrice.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG);
            }
            holder.mTvMoney.getPaint().setFlags(0);
        }else {
            holder.mTvPrice.setVisibility(View.INVISIBLE);
            holder.activityPrice.setVisibility(View.VISIBLE);
            holder.activityPrice.setText(String.format(mContext.getString(R.string.activity_price),serveTypeEntity.serviceActivity));
//            if (serveTypeEntity.count.equals("0")) {
//                holder.mTvMoney.setText(String.format(mContext.getString(R.string.price), serveTypeEntity.money));
//            } else {
//                holder.mTvMoney.setText(String.format(mContext.getString(R.string.price_count), serveTypeEntity.money, serveTypeEntity.count));
//            }
            holder.mTvMoney.setText(String.format(mContext.getString(R.string.price), serveTypeEntity.money));
            holder.mTvMoney.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG);
        }

        if (position == mCurrentItem) {
            holder.mIvChecked.setImageResource(R.drawable.xuanzhong);
            holder.mTvName.setTextColor(mContext.getResources().getColor(R.color.tab_unselected_text));
        } else {
            holder.mIvChecked.setImageResource(R.drawable.moren);
            holder.mTvName.setTextColor(mContext.getResources().getColor(R.color.gray_text));
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
        @InjectView(R.id.tv_name)
        TextView mTvName;
        @InjectView(R.id.iv_checked)
        ImageView mIvChecked;
        @InjectView(R.id.tv_money)
        TextView mTvMoney;
        @InjectView(R.id.tv_price)
        TextView mTvPrice;
        @InjectView(R.id.activity_price)
        TextView activityPrice;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
