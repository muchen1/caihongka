package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.FinancePeriodModel;
import com.rainbowcard.client.model.GiveEntity;
import com.rainbowcard.client.model.PointsEntity;
import com.rainbowcard.client.utils.StringUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2016-6-22.
 */
public class PeriodAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<FinancePeriodModel.FinancePeriodEntity> mFinancePeriodEntitys;
    private int clickTemp = 1;//默认不选中
    private double add;
    public int financeCouponMoney;
    public int investMoney;
    DecimalFormat df = new DecimalFormat("##,###,###,###,##0.0000");


    public PeriodAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mFinancePeriodEntitys = new ArrayList<FinancePeriodModel.FinancePeriodEntity>();
    }

    public void setFinancePeriodEntitys(List<FinancePeriodModel.FinancePeriodEntity> list,double add,int financeCouponMoney,int investMoney){
        mFinancePeriodEntitys.clear();
        mFinancePeriodEntitys.addAll(list);
        this.add = add;
        this.financeCouponMoney = financeCouponMoney;
        this.investMoney = investMoney;
        notifyDataSetChanged();
    }

    public void setSeclection(int position){
        clickTemp = position;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mFinancePeriodEntitys.size();
    }

    @Override
    public FinancePeriodModel.FinancePeriodEntity getItem(int position) {
        return mFinancePeriodEntitys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.item_period_grid,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        FinancePeriodModel.FinancePeriodEntity mFinancePeriodEntity = mFinancePeriodEntitys.get(position);
        viewHolder.periodTv.setText(String.format(mContext.getString(R.string.period_text),mFinancePeriodEntity.day));

        if(add == 0.0){
            viewHolder.giveLayout.setVisibility(View.GONE);
        }else {
            viewHolder.giveTv.setText(String.format(mContext.getString(R.string.give_text),StringUtil.subZeroAndDot(String.valueOf((int) (add * 100)))));
            viewHolder.giveLayout.setVisibility(View.VISIBLE);
        }
        double count = investMoney * mFinancePeriodEntity.yearInterest / 12 * mFinancePeriodEntity.month / financeCouponMoney + (investMoney * mFinancePeriodEntity.yearInterest / 12 * mFinancePeriodEntity.month / financeCouponMoney *add);
        DecimalFormat df = new DecimalFormat("##0.0000");
        String f1 = df.format(count);
        DecimalFormat df2 = new DecimalFormat("##0.00");
        String f2 = df2.format(count * 50.00);
//        BigDecimal bg = new BigDecimal(count);
//        double f1 = bg.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
//        viewHolder.countTv.setText(String.format(mContext.getString(R.string.count_text), StringUtil.subZeroAndDot(String.valueOf(f1)),StringUtil.subZeroAndDot(String.valueOf(f2))));
        String aa = StringUtil.subZeroAndDot(String.valueOf(f1));
//        String bb = StringUtil.subZeroAndDot(String.valueOf(f2));
        mFinancePeriodEntity.count = aa;
//        Spannable span = new SpannableString(viewHolder.countTv.getText());
//        span.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.money_color)), 5,5+aa.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        viewHolder.countTv.setText(span);
//        Spannable span2 = new SpannableString(viewHolder.countTv.getText());
//        span2.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.money_color)), 5,5+aa.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString sp = new SpannableString(String.format(mContext.getResources().getString(R.string.count_text),StringUtil.subZeroAndDot(String.valueOf(f1)),StringUtil.subZeroAndDot(String.valueOf(f2))));

        String memberStateMent = String.format(mContext.getResources().getString(R.string.count_text),StringUtil.subZeroAndDot(String.valueOf(f1)),StringUtil.subZeroAndDot(String.valueOf(f2)));

        sp.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.money_color)), memberStateMent.indexOf(StringUtil.subZeroAndDot(String.valueOf(f1))),
                memberStateMent.indexOf(StringUtil.subZeroAndDot(String.valueOf(f1))) +StringUtil.subZeroAndDot(String.valueOf(f1)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //变色
        sp.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.money_color)), memberStateMent.indexOf("¥"+StringUtil.subZeroAndDot(String.valueOf(f2))),
                memberStateMent.indexOf("¥"+StringUtil.subZeroAndDot(String.valueOf(f2))) + ("¥"+StringUtil.subZeroAndDot(String.valueOf(f2))).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        viewHolder.countTv.setText(sp);

        if(clickTemp == position){
            viewHolder.periodLayout.setBackgroundResource(R.drawable.bg_layout_focused_blue);
        }else {
            viewHolder.periodLayout.setBackgroundResource(R.drawable.bg_layout_focused);
        }

        return convertView;
    }

    static class ViewHolder{
        @InjectView(R.id.rl_period)
        RelativeLayout periodLayout;
        @InjectView(R.id.give_layout)
        LinearLayout giveLayout;
        @InjectView(R.id.period_tv)
        TextView periodTv;
        @InjectView(R.id.count_tv)
        TextView countTv;
        @InjectView(R.id.give_tv)
        TextView giveTv;
        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
