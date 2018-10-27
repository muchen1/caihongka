package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.rainbowcard.client.R;
import com.rainbowcard.client.model.GiveEntity;
import com.rainbowcard.client.model.PointsEntity;
import com.rainbowcard.client.model.PointsModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2016-6-22.
 */
public class PointsAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<PointsEntity> mPointsEntitys;
    private List<GiveEntity> mGiveEntitys;
    private int clickTemp = 0;//默认不选中
//    private int clickTemp = 0;
    private double userRate;

    public PointsAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mPointsEntitys = new ArrayList<PointsEntity>();
        mGiveEntitys = new ArrayList<GiveEntity>();
    }

    public void setmPointsEntitys(List<PointsEntity> list){
        mPointsEntitys.clear();
        mPointsEntitys.addAll(list);
        notifyDataSetChanged();
    }

    public void setmGiveEntitys(List<GiveEntity> list){
        mGiveEntitys.clear();
        mGiveEntitys.addAll(list);
        notifyDataSetChanged();
    }

    public void setPointsEntitys(List<PointsEntity> list, List<GiveEntity> list2){
        mPointsEntitys.clear();
        mPointsEntitys.addAll(list);
        mGiveEntitys.clear();
        mGiveEntitys.addAll(list2);
        notifyDataSetChanged();
    }

    public void setSeclection(int position){
        clickTemp = position;
        notifyDataSetChanged();
    }

    public void setRate(double rate){
        userRate = rate;
    }

    @Override
    public int getCount() {
        return mPointsEntitys.size();
    }

    @Override
    public PointsEntity getItem(int position) {
        return mPointsEntitys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.item_points_list,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PointsEntity mPointsEntity = mPointsEntitys.get(position);
        viewHolder.priceTv.setText(String.format(mContext.getString(R.string.recharge_price),new
                DecimalFormat("##,###,###,###,##0").format(mPointsEntity.price)));
//        viewHolder.pointsTv.setText(String.format(mContext.getString(R.string.recharge_points),mPointsEntity.points));
//        viewHolder.pointsTv.setVisibility(View.GONE);
        if(userRate == 0){
            viewHolder.pointsTv.setVisibility(View.GONE);
        }else {
            viewHolder.pointsTv.setVisibility(View.VISIBLE);
        }

        if(mGiveEntitys != null && !mGiveEntitys.isEmpty()){
            for (int i = 0;i<mGiveEntitys.size();i++){
                if(mGiveEntitys.get(i).min <= mPointsEntity.price && mPointsEntity.price < mGiveEntitys.get(i).max){
                    viewHolder.pointsTv.setText(String.format(mContext.getString(R.string.give_money),(int) (mPointsEntity.price * mGiveEntitys.get(i).rebate / 100)));
                    viewHolder.pointsTv.setVisibility(View.VISIBLE);
                    break;
                }else {
                    viewHolder.pointsTv.setText("");
                    viewHolder.pointsTv.setVisibility(View.GONE);
                }
            }
        }else {
            viewHolder.pointsTv.setVisibility(View.GONE);
        }

//        viewHolder.pointsTv.setText(String.format(mContext.getString(R.string.give_money),(int) (mPointsEntity.price * mPointsEntity.points / 100)));
        if(clickTemp == position){
            viewHolder.pointsLayout.setBackgroundResource(R.drawable.bg_layout_select);
            viewHolder.priceTv.setTextColor(mContext.getResources().getColor(R.color.app_white));
            viewHolder.pointsTv.setTextColor(mContext.getResources().getColor(R.color.app_white));
        }else {
            viewHolder.pointsLayout.setBackgroundResource(R.drawable.bg_layout_focused);
            viewHolder.priceTv.setTextColor(mContext.getResources().getColor(R.color.app_black));
            viewHolder.pointsTv.setTextColor(mContext.getResources().getColor(R.color.stroke));
        }

        return convertView;
    }

    static class ViewHolder{
        @InjectView(R.id.rl_points)
        RelativeLayout pointsLayout;
        @InjectView(R.id.price_text)
        TextView priceTv;
        @InjectView(R.id.points_text)
        TextView pointsTv;
//        @InjectView(R.id.givepoints_text)
//        TextView givePoints;
        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
