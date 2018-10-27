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
import com.rainbowcard.client.model.BannerEntity;
import com.rainbowcard.client.model.CardEntity;
import com.rainbowcard.client.utils.DensityUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017-7-6.
 */
public class ClassifyGridAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<BannerEntity> mBannerEntitys;

    private int mCurrentItem;

    public ClassifyGridAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mBannerEntitys = new ArrayList<BannerEntity>();
    }

    public void setBannerEntitys(List<BannerEntity> list){
        mBannerEntitys.clear();
        mBannerEntitys.addAll(list);
        notifyDataSetChanged();
    }

    public void setCurrentItem(int item) {
        mCurrentItem = item;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mBannerEntitys.size();
    }

    @Override
    public BannerEntity getItem(int position) {
        return mBannerEntitys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.item_gridview,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        BannerEntity bannerEntity = mBannerEntitys.get(position);

        viewHolder.tv.setText(bannerEntity.title);
        if(TextUtils.isEmpty(bannerEntity.img)){
            viewHolder.iv.setImageResource(R.drawable.shop_default);
        }else {
            Picasso.with(mContext)
                    .load(String.format(mContext.getString(R.string.img_url),bannerEntity.img))
                    .resize(DensityUtil.dip2px(mContext,30),DensityUtil.dip2px(mContext,30))
                    .centerCrop()
                    .placeholder(R.drawable.shop_default)
                    .error(R.drawable.shop_default).into(viewHolder.iv);
        }

        return convertView;
    }

    static class ViewHolder{
        @InjectView(R.id.textView)
        TextView tv;
        @InjectView(R.id.imageView)
        ImageView iv;
        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
