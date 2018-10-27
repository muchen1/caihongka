package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.CardEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2016-6-22.
 */
public class MyCardListAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<CardEntity> mCardEntitys;

    public MyCardListAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mCardEntitys = new ArrayList<CardEntity>();
    }

    public void setCardEntitys(List<CardEntity> list){
        mCardEntitys.clear();
        mCardEntitys.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mCardEntitys.size();
    }

    @Override
    public CardEntity getItem(int position) {
        return mCardEntitys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.item_mycard_list,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CardEntity cardEntity = mCardEntitys.get(position);
        viewHolder.numTv.setText(cardEntity.num);

        return convertView;
    }

    static class ViewHolder{
        @InjectView(R.id.num)
        TextView numTv;
        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
