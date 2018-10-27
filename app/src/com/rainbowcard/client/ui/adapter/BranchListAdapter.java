package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
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
public class BranchListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<MarkerInfoUtil> mMarkerInfos;
    private String mKeyword;

    private int serviceType;
    private int isRb;

    public BranchListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mMarkerInfos = new ArrayList<MarkerInfoUtil>();
    }

    public void setBranchList(List<MarkerInfoUtil> list,int flag) {
        mMarkerInfos.clear();
        mMarkerInfos.addAll(list);
        isRb = flag;
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
            view = mLayoutInflater.inflate(R.layout.item_branch_list, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final MarkerInfoUtil markerInfo = mMarkerInfos.get(i);
        holder.nameTv.setText(markerInfo.name);
        holder.addrTv.setText(markerInfo.addr);
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

        switch (serviceType){
            case 1:
                holder.warkIcon.setImageResource(R.drawable.hp_wash_words);
                if(markerInfo.serviceEntitys.size() < 2){
                    holder.dollyTv.setText(String.format(mContext.getString(R.string.price), markerInfo.serviceEntitys.get(0).money));
                }else {
                    if(markerInfo.serviceEntitys.get(0).serviceSonType == 1){
                        holder.dollyTv.setText(String.format(mContext.getString(R.string.dolly_money), markerInfo.serviceEntitys.get(0).money));
                        holder.cartTv.setText(String.format(mContext.getString(R.string.cart_money), markerInfo.serviceEntitys.get(1).money));
                    }else {
                        holder.cartTv.setText(String.format(mContext.getString(R.string.cart_money), markerInfo.serviceEntitys.get(0).money));
                        holder.dollyTv.setText(String.format(mContext.getString(R.string.dolly_money), markerInfo.serviceEntitys.get(1).money));
                    }
                }
                if(isRb == 1){
                    holder.dollyTv.setVisibility(View.INVISIBLE);
                }else {
                    holder.dollyTv.setVisibility(View.VISIBLE);
                }
                break;
            case 2:
                holder.warkIcon.setImageResource(R.drawable.hp_coating_words);
                if(markerInfo.serviceEntitys.size() < 2){
                    holder.dollyTv.setText(String.format(mContext.getString(R.string.price), markerInfo.serviceEntitys.get(0).money));
                }else {
                    if(markerInfo.serviceEntitys.get(0).serviceSonType == 1){
                        holder.dollyTv.setText(String.format(mContext.getString(R.string.dolly_money), markerInfo.serviceEntitys.get(0).money));
                        holder.cartTv.setText(String.format(mContext.getString(R.string.cart_money), markerInfo.serviceEntitys.get(1).money));
                    }else {
                        holder.cartTv.setText(String.format(mContext.getString(R.string.cart_money), markerInfo.serviceEntitys.get(0).money));
                        holder.dollyTv.setText(String.format(mContext.getString(R.string.dolly_money), markerInfo.serviceEntitys.get(1).money));
                    }
                }
                break;
            case 4:
                holder.warkIcon.setImageResource(R.drawable.hp_cleanwords);
                holder.dollyTv.setText(String.format(mContext.getString(R.string.price),markerInfo.serviceEntitys.get(0).money));
                break;
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
        @InjectView(R.id.wark_icon)
        ImageView warkIcon;
        @InjectView(R.id.tv_dolly)
        TextView dollyTv;
        @InjectView(R.id.tv_cart)
        TextView cartTv;
        @InjectView(R.id.tv_comment)
        TextView commentTv;
        @InjectView(R.id.tv_order)
        TextView orderTv;
        @InjectView(R.id.tv_distance)
        TextView distanceTv;

        ViewHolder(View view){
            ButterKnife.inject(this, view);
        }
    }
}
