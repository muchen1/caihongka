package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.model.GiveEntity;
import com.rainbowcard.client.model.HotGoodEntity;
import com.rainbowcard.client.model.MarkerInfoUtil;
import com.rainbowcard.client.model.PointsEntity;
import com.rainbowcard.client.ui.GoodsDetailActivity;
import com.rainbowcard.client.utils.DensityUtil;
import com.rainbowcard.client.utils.StringUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2016-6-22.
 */
public class GoodsAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<HotGoodEntity> mHotGoodEntitys;

    public GoodsAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mHotGoodEntitys = new ArrayList<HotGoodEntity>();
    }

    public void setHotGoodEntitys(List<HotGoodEntity> list){
        mHotGoodEntitys.clear();
        mHotGoodEntitys.addAll(list);
        notifyDataSetChanged();
    }

    public List<HotGoodEntity> getHotGoodEntitys(){
        return  mHotGoodEntitys;
    }


    @Override
    public int getCount() {
        return mHotGoodEntitys.size();
    }

    @Override
    public HotGoodEntity getItem(int position) {
        return mHotGoodEntitys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.item_goods_grid,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final HotGoodEntity hotGoodEntity = mHotGoodEntitys.get(position);

        if (!TextUtils.isEmpty(hotGoodEntity.goodsImg)) {
            Picasso.with(mContext)
                    .load(StringUtil.getUrl(hotGoodEntity.goodsImg,mContext))
                    .resize(DensityUtil.dip2px(mContext,173),DensityUtil.dip2px(mContext,97))
                    .centerCrop()
                    .placeholder(R.drawable.order_default)
                    .error(R.drawable.order_default).into(viewHolder.image);
        }else {
            viewHolder.image.setImageResource(R.drawable.order_default);
        }
        viewHolder.nameTv.setText(hotGoodEntity.goodsName);
        viewHolder.integralTv.setText(String.format(mContext.getString(R.string.integral),hotGoodEntity.integral));
        if(position % 2 == 0) {
            viewHolder.rlGoods.setPadding(24,0,0,0);
        }else {
            viewHolder.rlGoods.setPadding(0,0,24,0);
        }

        if(hotGoodEntity.remnant == 0){
            viewHolder.exchangeBtn.setBackgroundResource(R.drawable.bg_layout_focused_gray);
            viewHolder.exchangeBtn.setTextColor(mContext.getResources().getColor(R.color.app_gray));
        }else {
            viewHolder.exchangeBtn.setBackgroundResource(R.drawable.bg_layout_focused_blue);
            viewHolder.exchangeBtn.setTextColor(mContext.getResources().getColor(R.color.stroke));
        }
        /*viewHolder.exchangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hotGoodEntity.remnant != 0) {
                    Intent intent = new Intent(mContext, GoodsDetailActivity.class);
                    intent.putExtra(Constants.KEY_GOODS_ID, hotGoodEntity.id);
                    mContext.startActivity(intent);
                }
            }
        });
        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hotGoodEntity.remnant != 0) {
                    Intent intent = new Intent(mContext, GoodsDetailActivity.class);
                    intent.putExtra(Constants.KEY_GOODS_ID, hotGoodEntity.id);
                    mContext.startActivity(intent);
                }
            }
        });*/


        return convertView;
    }

    static class ViewHolder{
        @InjectView(R.id.rl_goods)
        RelativeLayout rlGoods;
        @InjectView(R.id.image)
        ImageView image;
        @InjectView(R.id.name)
        TextView nameTv;
        @InjectView(R.id.integral)
        TextView integralTv;
        @InjectView(R.id.exchange_btn)
        TextView exchangeBtn;
        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
