package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.rainbowcard.client.R;
import com.rainbowcard.client.common.city.CityEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by gc on 17-3-14.
 */
public class SelectCityAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<CityEntity> mCurrentPositionList;
    private List<CityEntity> mHistoryCityList;
    private List<CityEntity> mHotCityList;
    private List<CityEntity> mAllCityList;
    private List<CityEntity> mSearchList;

    private Boolean mIsLocatePresentCity = false;
    private Boolean mIsCurrentPosition = false;
    private Boolean mIsLoadHistoryCity = false;
    private Boolean mIsLoadHotCity = false;
    private Boolean mIsLoadAllCity = false;

    private int mPresentCityId = 0;
    private final static int HEAD_TYPE_POSITION = -5;
    private final static int HEAD_TYPE_HISTORY = -2;
    private final static int HEAD_TYPE_HOT = -2;
    private final static int HEAD_TYPE_ALL = -3;
    private final static int HEAD_TYPE_SEARCH = -4;

    private Boolean mIsSearching = false;

    public SelectCityAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);

        setPreData();
    }

    public void setPresentCityId(int id) {
        mPresentCityId = id;
        notifyDataSetChanged();
    }

    public void setPresentCity(List<CityEntity> list) {
        mIsLocatePresentCity = true;
        notifyDataSetChanged();
    }
    public void setCurrentPosition(List<CityEntity> list) {
        mCurrentPositionList.clear();
        mCurrentPositionList.addAll(list);
        mIsCurrentPosition = true;
        notifyDataSetChanged();
    }

    public void setHistoryCityList(List<CityEntity> list) {
        mHistoryCityList.clear();
        mHistoryCityList.addAll(list);
        mIsLoadHistoryCity = true;
        notifyDataSetChanged();
    }

    public void setHotCityList(List<CityEntity> list) {
        mHotCityList.clear();
        mHotCityList.addAll(list);
        mIsLoadHotCity = true;
        notifyDataSetChanged();
    }

    public void setAllCityList(List<CityEntity> list) {
        mAllCityList.clear();
        mAllCityList.addAll(list);
        for (CityEntity cityEntity :mHotCityList){
            if(!mCurrentPositionList.isEmpty() && !TextUtils.isEmpty(mCurrentPositionList.get(0).name) && mCurrentPositionList.get(0).name.equals(cityEntity.name)){
                mCurrentPositionList.get(0).id = cityEntity.id;
            }
        }
        Collections.sort(mAllCityList, new Comparator<CityEntity>() {
            @Override
            public int compare(CityEntity lhs, CityEntity rhs) {
                return lhs.pinyin.compareTo(rhs.pinyin);
            }
        });
        mIsLoadAllCity = true;
        notifyDataSetChanged();
    }

    private void setPreData() {
        mCurrentPositionList = new ArrayList<CityEntity>();
        mHistoryCityList = new ArrayList<CityEntity>(3);
        mHotCityList = new ArrayList<CityEntity>();
        mAllCityList = new ArrayList<CityEntity>();
        mSearchList = new ArrayList<CityEntity>();

        mCurrentPositionList.add(new CityEntity());
        mHistoryCityList.add(new CityEntity());
        mHotCityList.add(new CityEntity());
        mAllCityList.add(new CityEntity());
    }

    public int findCitySectionByIndexChar(String c) {
        if(TextUtils.isEmpty(c)) {
            return 0;
        }
        for (int i = 0, len = mAllCityList.size(); i < len; i++) {
            CityEntity cityEntity = mAllCityList.get(i);
            if(TextUtils.isEmpty(cityEntity.pinyin)){
                return 0;
            }
            if (cityEntity.pinyin.toUpperCase().startsWith(c)) {
                return i + mCurrentPositionList.size() + mHotCityList.size();
            }
        }
        return 0;
    }

    public void search(final String text) {
        mSearchList.clear();
        mSearchList.addAll(mAllCityList);
        Collections.sort(mSearchList, new Comparator<CityEntity>() {
            @Override
            public int compare(CityEntity lhs, CityEntity rhs) {
                return rhs.name.indexOf(text) - lhs.name.indexOf(text);
            }
        });

        mIsSearching = true;

        notifyDataSetChanged();
    }

    public void clearSearch() {
        mIsSearching = false;
        notifyDataSetChanged();
    }

    @Override
    public View getHeaderView(int pos, View view, ViewGroup viewGroup) {
        HeaderViewHolder holder;
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.header_select_city_list, viewGroup, false);
            holder = new HeaderViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (HeaderViewHolder) view.getTag();
        }

        String title;
        int id = (int) getHeaderId(pos);
        switch (id) {
            case HEAD_TYPE_POSITION:
                title = mContext.getString(R.string.select_city_head_position);
                break;
//            case HEAD_TYPE_HISTORY:
//                title = mContext.getString(R.string.select_city_head_history);
//                break;
            case HEAD_TYPE_HOT:
                title = mContext.getString(R.string.select_city_head_hot);
                break;
            case HEAD_TYPE_ALL:
                title = mContext.getString(R.string.select_city_head_other);
                break;
            case HEAD_TYPE_SEARCH:
                title = mContext.getString(R.string.select_city_head_search);
                break;
            default:
                title =  String.valueOf((char) id).toUpperCase();
        }

        holder.mTvTitle.setText(title);

        return view;
    }

    private int getCityType(int position) {
        if (mIsSearching) {
            return HEAD_TYPE_SEARCH;
        }

        if(position <mCurrentPositionList.size()){
            return  HEAD_TYPE_POSITION;
        }else {
            position -= mCurrentPositionList.size();
        }

//        if (position < mHistoryCityList.size()) {
//            return HEAD_TYPE_HISTORY;
//        } else {
//            position -= mHistoryCityList.size();
//        }

        if (position < mHotCityList.size()) {
            return HEAD_TYPE_HOT;
        } else {
            return HEAD_TYPE_ALL;
        }

    }

    @Override
    public long getHeaderId(int position) {
        int type = getCityType(position);
        if (type == HEAD_TYPE_ALL) {
            CityEntity cityEntity = (CityEntity) getItem(position);
            if (TextUtils.isEmpty(cityEntity.pinyin)) {
                return type;
            } else {
                return cityEntity.pinyin.charAt(0);
            }
        } else {
            return type;
        }
    }

    @Override
    public int getCount() {
        if (mIsSearching) {
            return mSearchList.size();
        }
        return mCurrentPositionList.size() + mHotCityList.size() + mAllCityList.size();
    }

    @Override
    public Object getItem(int position) {

        if (mIsSearching) {
            return mSearchList.get(position);
        }

        int type = getCityType(position);
        switch (type) {
            case  HEAD_TYPE_POSITION:
                return mCurrentPositionList.get(position);
//            case HEAD_TYPE_HISTORY:
//                return mHistoryCityList.get(position - mCurrentPositionList.size());
            case HEAD_TYPE_HOT:
                return mHotCityList.get(position  - mCurrentPositionList.size());
            case HEAD_TYPE_ALL:
                return mAllCityList.get(position - mHotCityList.size() - mCurrentPositionList.size());
            default:
                return null;
        }
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_select_city, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        setCityItem(position, holder);

        return convertView;
    }

    private void setCityItem(int pos, ViewHolder holder) {

        if (mIsSearching) {
            CityEntity cityEntity = mSearchList.get(pos);
            holder.mTvName.setText(cityEntity.name);
            holder.mPbLoading.setVisibility(View.INVISIBLE);
            holder.mIvChecked.setVisibility(View.INVISIBLE);
            return;
        }

        int type = getCityType(pos);
        if (type == HEAD_TYPE_POSITION && !mIsCurrentPosition) {
            holder.mIvChecked.setVisibility(View.INVISIBLE);
            holder.mPbLoading.setVisibility(View.VISIBLE);
            holder.mTvName.setText(R.string.select_city_locating_position);
            return;
        }

//        if (type == HEAD_TYPE_HISTORY && !mIsLoadHistoryCity) {
//            holder.mIvChecked.setVisibility(View.INVISIBLE);
//            holder.mPbLoading.setVisibility(View.VISIBLE);
//            holder.mTvName.setText(R.string.select_city_load_history);
//            return;
//        }

        if (type == HEAD_TYPE_HOT && !mIsLoadHotCity) {
            holder.mIvChecked.setVisibility(View.INVISIBLE);
            holder.mPbLoading.setVisibility(View.VISIBLE);
            holder.mTvName.setText(R.string.select_city_load_hot);
            return;
        }

        if (type == HEAD_TYPE_ALL && !mIsLoadAllCity) {
            holder.mIvChecked.setVisibility(View.INVISIBLE);
            holder.mPbLoading.setVisibility(View.VISIBLE);
            holder.mTvName.setText(R.string.select_city_load_all);
            return;
        }

        CityEntity cityEntity = (CityEntity) getItem(pos);
        holder.mPbLoading.setVisibility(View.INVISIBLE);
        if(cityEntity != null){
            if(TextUtils.isEmpty(cityEntity.name)){
                holder.mIvChecked.setVisibility(View.INVISIBLE);
            }else {
                if (cityEntity.id == mPresentCityId) {
//                    holder.mIvChecked.setVisibility(View.VISIBLE);
                } else {
                    holder.mIvChecked.setVisibility(View.INVISIBLE);
                }
            }

            holder.mTvName.setText(cityEntity.name);
        }
    }

    static class HeaderViewHolder {
        @InjectView(R.id.tv_title)
        TextView mTvTitle;

        HeaderViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    static class ViewHolder {
        @InjectView(R.id.iv_checked)
        ImageView mIvChecked;
        @InjectView(R.id.pb_loading)
        ProgressBar mPbLoading;
        @InjectView(R.id.tv_name)
        TextView mTvName;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
