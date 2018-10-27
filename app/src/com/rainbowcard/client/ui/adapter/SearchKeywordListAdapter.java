package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.baidu.mapapi.search.core.PoiInfo;
import com.rainbowcard.client.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 16-10-31.
 */
public class SearchKeywordListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<PoiInfo> mKeywordInfos;
    private String mKeyword;

    public SearchKeywordListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mKeywordInfos = new ArrayList<PoiInfo>();
    }

    public void setConsumptions(List<PoiInfo> list) {
        mKeywordInfos.clear();
        mKeywordInfos.addAll(list);
        notifyDataSetChanged();
    }

    public void clear(){
        mKeywordInfos.clear();
        notifyDataSetChanged();
    }

    public void setKeyword(String keyword){
        mKeyword = keyword;
    }

    public void addConsumptions(List<PoiInfo> list) {
        mKeywordInfos.addAll(list);
        notifyDataSetChanged();
    }

    public List<PoiInfo> getKeywords(){
        return  mKeywordInfos;
    }
    @Override
    public int getCount() {
        return mKeywordInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return mKeywordInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.item_keywords_list, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final PoiInfo keywordInfo = mKeywordInfos.get(i);

        if(!TextUtils.isEmpty(keywordInfo.name)){
            SpannableStringBuilder style=new SpannableStringBuilder(keywordInfo.name);
            int fstart=keywordInfo.name.indexOf(mKeyword);
            int fend=fstart + mKeyword.length();
            if(fend > keywordInfo.name.length() || fstart < 0){
                holder.titleTv.setText(keywordInfo.name);
            }else {
                style.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.detail_fgm_select)), fstart, fend, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                holder.titleTv.setText(style);
            }
        }
        if(!TextUtils.isEmpty(keywordInfo.address)){
            SpannableStringBuilder style=new SpannableStringBuilder(keywordInfo.address);
            int fstart=keywordInfo.address.indexOf(mKeyword);
            int fend=fstart + mKeyword.length();
            if(fend > keywordInfo.name.length() || fstart < 0){
                holder.addrTv.setText(keywordInfo.address);
            }else {
                style.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.detail_fgm_select)), fstart, fend, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                holder.addrTv.setText(style);
            }
        }
        return view;
    }


    static class ViewHolder {
        @InjectView(R.id.title)
        TextView titleTv;
        @InjectView(R.id.addr)
        TextView addrTv;

        ViewHolder(View view){
            ButterKnife.inject(this, view);
        }
    }
}
