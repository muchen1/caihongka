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
import com.rainbowcard.client.model.MarkerInfoUtil;
import com.rainbowcard.client.utils.DensityUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 16-10-31.
 */
public class RecommendListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<MarkerInfoUtil> mMarkerInfos;
    private String mKeyword;

    private int serviceType;

    public RecommendListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mMarkerInfos = new ArrayList<MarkerInfoUtil>();
    }

    public void setBranchList(List<MarkerInfoUtil> list) {
        mMarkerInfos.clear();
        mMarkerInfos.addAll(list);
        notifyDataSetChanged();
    }

    public void setType(int type){
        serviceType = type;
    }
    public void clear(){
        mMarkerInfos.clear();
        notifyDataSetChanged();
    }


    public void addBranchList(List<MarkerInfoUtil> list) {
        mMarkerInfos.addAll(list);
        notifyDataSetChanged();
    }

    public List<MarkerInfoUtil> getBranchList(){
        return  mMarkerInfos;
    }
    @Override
    public int getCount() {
        return mMarkerInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return mMarkerInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.item_recommend_list, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final MarkerInfoUtil markerInfo = mMarkerInfos.get(i);
        holder.nameTv.setText(markerInfo.name);
        holder.addrTv.setText(markerInfo.addr);
//        if(markerInfo.renbaoUse == 1){
//            holder.priceTv.setVisibility(View.INVISIBLE);
//        }else {
//            holder.priceTv.setVisibility(View.VISIBLE);
//            holder.priceTv.setText(String.format(mContext.getString(R.string.price),markerInfo.serviceEntitys.get(0).money));
//        }

        holder.priceTv.setText(String.format(mContext.getString(R.string.price),markerInfo.serviceEntitys.get(0).money));

        if(TextUtils.isEmpty(markerInfo.imgUrl)){
            holder.shopIcon.setImageResource(R.drawable.shop_default);
        }else {
            Picasso.with(mContext)
                    .load(String.format(mContext.getString(R.string.img_url),markerInfo.imgUrl))
                    .resize(DensityUtil.dip2px(mContext,110),DensityUtil.dip2px(mContext,110))
                    .centerCrop()
                    .placeholder(R.drawable.shop_default)
                    .error(R.drawable.shop_default).into(holder.shopIcon);
        }

        if(markerInfo.serviceEntitys.size() > 0 && markerInfo.serviceEntitys.size() <=2){
            if(markerInfo.serviceEntitys.get(0).serviceType == 1){
                holder.washIcon.setVisibility(View.VISIBLE);
                holder.filmIcon.setVisibility(View.GONE);
                holder.rinseIcon.setVisibility(View.GONE);
            }else if(markerInfo.serviceEntitys.get(0).serviceType == 2){
                holder.washIcon.setVisibility(View.GONE);
                holder.filmIcon.setVisibility(View.VISIBLE);
                holder.rinseIcon.setVisibility(View.GONE);
            }else {
                holder.washIcon.setVisibility(View.GONE);
                holder.filmIcon.setVisibility(View.GONE);
                holder.rinseIcon.setVisibility(View.VISIBLE);
            }
        }else if(markerInfo.serviceEntitys.size() > 2 && markerInfo.serviceEntitys.size() <= 4){
            if(markerInfo.serviceEntitys.get(0).serviceType == 1 && markerInfo.serviceEntitys.get(2).serviceType == 2){
                holder.washIcon.setVisibility(View.VISIBLE);
                holder.filmIcon.setVisibility(View.VISIBLE);
                holder.rinseIcon.setVisibility(View.GONE);
            }else if(markerInfo.serviceEntitys.get(0).serviceType == 1 && markerInfo.serviceEntitys.get(2).serviceType == 4) {
                    holder.washIcon.setVisibility(View.VISIBLE);
                    holder.filmIcon.setVisibility(View.GONE);
                    holder.rinseIcon.setVisibility(View.VISIBLE);
            }else if(markerInfo.serviceEntitys.get(0).serviceType == 2 && markerInfo.serviceEntitys.get(2).serviceType == 1){
                holder.washIcon.setVisibility(View.VISIBLE);
                holder.filmIcon.setVisibility(View.VISIBLE);
                holder.rinseIcon.setVisibility(View.GONE);
            }else if(markerInfo.serviceEntitys.get(0).serviceType == 4 && markerInfo.serviceEntitys.get(2).serviceType == 1){
                holder.washIcon.setVisibility(View.VISIBLE);
                holder.filmIcon.setVisibility(View.GONE);
                holder.rinseIcon.setVisibility(View.VISIBLE);
            }else {
                holder.washIcon.setVisibility(View.GONE);
                holder.filmIcon.setVisibility(View.VISIBLE);
                holder.rinseIcon.setVisibility(View.VISIBLE);
            }
        }else if(markerInfo.serviceEntitys.size() > 4 && markerInfo.serviceEntitys.size() <=6){
            holder.washIcon.setVisibility(View.VISIBLE);
            holder.filmIcon.setVisibility(View.VISIBLE);
            holder.rinseIcon.setVisibility(View.VISIBLE);
        }else {
            holder.washIcon.setVisibility(View.GONE);
            holder.filmIcon.setVisibility(View.GONE);
            holder.rinseIcon.setVisibility(View.GONE);
        }


        holder.orderTv.setText(String.format(mContext.getString(R.string.order),markerInfo.orderNum));
        holder.distanceTv.setText(String.format(mContext.getString(R.string.distance),markerInfo.distance));
        holder.commentTv.setText(String.format(mContext.getString(R.string.comment),markerInfo.star));


        return view;
    }


    static class ViewHolder {
        @InjectView(R.id.tv_name)
        TextView nameTv;
        @InjectView(R.id.tv_addr)
        TextView addrTv;
        @InjectView(R.id.shop_icon)
        ImageView shopIcon;
        @InjectView(R.id.wash_icon)
        ImageView washIcon;
        @InjectView(R.id.rinse_icon)
        ImageView rinseIcon;
        @InjectView(R.id.film_icon)
        ImageView filmIcon;
        @InjectView(R.id.tv_comment)
        TextView commentTv;
        @InjectView(R.id.tv_order)
        TextView orderTv;
        @InjectView(R.id.tv_distance)
        TextView distanceTv;
        @InjectView(R.id.tv_price)
        TextView priceTv;

        ViewHolder(View view){
            ButterKnife.inject(this, view);
        }
    }
}
