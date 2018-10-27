package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.DiscountEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 17-3-16.
 */
public class DiscountListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<DiscountEntity> mDiscountEntitys;
    private int clickPosition = -1;
    private int clickCount = 0;
    private boolean isDown = false;

    private int userMode = 3;


    public DiscountListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mDiscountEntitys = new ArrayList<DiscountEntity>();
    }

    public void setDiscountEntitys(List<DiscountEntity> list) {
        mDiscountEntitys.clear();
        mDiscountEntitys.addAll(list);
        notifyDataSetChanged();
    }

    public void setUserMode(int status){
        userMode = status;
        notifyDataSetChanged();
    }

    public void addDiscountEntitys(List<DiscountEntity> list) {
        mDiscountEntitys.addAll(list);
        notifyDataSetChanged();
    }

    public List<DiscountEntity> getDiscountEntitys(){
        return  mDiscountEntitys;
    }
    @Override
    public int getCount() {
        return mDiscountEntitys.size();
    }

    @Override
    public Object getItem(int i) {
        return mDiscountEntitys.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.item_discount_list, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final DiscountEntity discountEntity = mDiscountEntitys.get(i);
        holder.nameTv.setText(discountEntity.title);
        holder.dateTv.setText(String.format(mContext.getString(R.string.discount_date),discountEntity.startTime,discountEntity.endTime));
//        holder.dateTv.setText(String.format(mContext.getString(R.string.discount_valid),discountEntity.endTime));
        switch (discountEntity.moneyType){
            case 1:
                holder.moneyTv.setText(String.format(mContext.getString(R.string.price),discountEntity.money));
                break;
            case 2:
                holder.moneyTv.setText(String.format(mContext.getString(R.string.sub_break),discountEntity.money));
                break;
        }
//        if(discountEntity.usedMoney.equals("0")){
//            holder.restrictTv.setText("");
//        }else {
//            holder.restrictTv.setText(String.format(mContext.getString(R.string.usable),discountEntity.usedMoney));
//        }
        holder.restrictTv.setText(String.format(mContext.getString(R.string.usable),discountEntity.usedMoney));


        holder.ruleDetail.setText(discountEntity.rule.replace("\\n","\n"));
        holder.ruleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickPosition == i){
                    clickCount ++;
                    if(clickCount % 2 == 0) {
                        isDown = false;
                    }else {
                        isDown = true;
                    }
                }else {
                    isDown = false;
                }
                clickPosition = i;
                notifyDataSetChanged();
            }
        });

        if(clickPosition == i){
            if (!isDown) {
                holder.bottomLayout.setVisibility(View.VISIBLE);
                holder.iconIv.setImageResource(R.drawable.up);
            }else {
                holder.bottomLayout.setVisibility(View.GONE);
                holder.iconIv.setImageResource(R.drawable.down);
            }
        }else {
            holder.bottomLayout.setVisibility(View.GONE);
            holder.iconIv.setImageResource(R.drawable.down);
        }

        if(userMode == 3){
            holder.nameTv.setTextColor(mContext.getResources().getColor(R.color.stroke));
            holder.moneyTv.setTextColor(mContext.getResources().getColor(R.color.app_white));
            holder.restrictTv.setTextColor(mContext.getResources().getColor(R.color.app_white));
//            holder.rightLayout.setBackgroundColor(mContext.getResources().getColor(R.color.stroke));
            holder.rightLayout.setBackgroundResource(R.drawable.shape_top_right_corner_blue);
            holder.line.setBackgroundColor(mContext.getResources().getColor(R.color.stroke));
            holder.statusTv.setVisibility(View.GONE);
            if(discountEntity.willExpired){
                holder.pastTv.setVisibility(View.VISIBLE);
            }else {
                holder.pastTv.setVisibility(View.GONE);
            }
        }else {
            holder.nameTv.setTextColor(mContext.getResources().getColor(R.color.app_gray));
            holder.moneyTv.setTextColor(mContext.getResources().getColor(R.color.app_gray));
            holder.restrictTv.setTextColor(mContext.getResources().getColor(R.color.app_gray));
//            holder.rightLayout.setBackgroundColor(mContext.getResources().getColor(R.color.app_gray_masking));
            holder.rightLayout.setBackgroundResource(R.drawable.shape_top_right_corner_gray);
            holder.line.setBackgroundColor(mContext.getResources().getColor(R.color.app_gray));
//            holder.statusTv.setVisibility(View.VISIBLE);
            holder.pastTv.setVisibility(View.GONE);
            if(userMode == 4){
                holder.statusTv.setText("已使用");
            }else {
                holder.statusTv.setText("已失效");
            }
        }

        return view;
    }


    static class ViewHolder {
        @InjectView(R.id.tv_name)
        TextView nameTv;
        @InjectView(R.id.tv_date)
        TextView dateTv;
        @InjectView(R.id.tv_money)
        TextView moneyTv;
        @InjectView(R.id.tv_restrict)
        TextView restrictTv;
        @InjectView(R.id.tv_rule_detail)
        TextView ruleDetail;
        @InjectView(R.id.bottom_layout)
        RelativeLayout bottomLayout;
        @InjectView(R.id.rule_btn)
        RelativeLayout ruleBtn;
        @InjectView(R.id.tv_status)
        TextView statusTv;
        @InjectView(R.id.tv_past)
        TextView pastTv;
        @InjectView(R.id.right_layout)
        RelativeLayout rightLayout;
        @InjectView(R.id.line)
        View line;
        @InjectView(R.id.icon)
        ImageView iconIv;
        ViewHolder(View view){
            ButterKnife.inject(this, view);
        }
    }
}
