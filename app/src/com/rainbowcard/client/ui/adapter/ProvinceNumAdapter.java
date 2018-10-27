package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.PointsEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2016-6-22.
 */
public class ProvinceNumAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<String> mProvinceNums;
    private int clickTemp = -1;//默认不选中

    public ProvinceNumAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mProvinceNums = new ArrayList<String>();
    }

    public void setProvinceNums(List<String> list){
        mProvinceNums.clear();
        mProvinceNums.addAll(list);
        notifyDataSetChanged();
    }

    public void setSeclection(int position){
        clickTemp = position;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mProvinceNums.size();
    }

    @Override
    public String getItem(int position) {
        return mProvinceNums.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.item_province_num_list,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String mProvinceNum = mProvinceNums.get(position);
        viewHolder.provinceTv.setText(mProvinceNum);
        if(clickTemp == position){
            viewHolder.provinceLayout.setBackgroundResource(R.drawable.bg_layout_select);
            viewHolder.provinceTv.setTextColor(mContext.getResources().getColor(R.color.app_white));
        }else {
            viewHolder.provinceLayout.setBackgroundResource(R.drawable.bg_layout_focused);
            viewHolder.provinceTv.setTextColor(mContext.getResources().getColor(R.color.tab_unselected_text));
        }

        return convertView;
    }

    static class ViewHolder{
        @InjectView(R.id.rl_province)
        RelativeLayout provinceLayout;
        @InjectView(R.id.province_text)
        TextView provinceTv;
        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
