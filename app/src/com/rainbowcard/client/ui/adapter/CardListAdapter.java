package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.AddressEntity;
import com.rainbowcard.client.model.CardEntity;
import com.rainbowcard.client.model.IllegalEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2016-6-22.
 */
public class CardListAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<CardEntity> mCardEntitys;

    private SetUnbindListener setUnbindListener;

    private int mCurrentItem = -1;

    public CardListAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mCardEntitys = new ArrayList<CardEntity>();
    }

    public CardListAdapter(Context context, SetUnbindListener listener){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mCardEntitys = new ArrayList<CardEntity>();
        setUnbindListener = listener;
    }

    public void setCardEntitys(List<CardEntity> list){
        mCardEntitys.clear();
        mCardEntitys.addAll(list);
        notifyDataSetChanged();
    }



    public void setCurrentItem(int item) {
        mCurrentItem = item;
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
            convertView = mLayoutInflater.inflate(R.layout.item_card_list,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final CardEntity cardEntity = mCardEntitys.get(position);
        viewHolder.numTv.setText(String.format(mContext.getString(R.string.rainbow_no),cardEntity.num));
        viewHolder.countTv.setText(cardEntity.count);
        viewHolder.balanceTv.setText(cardEntity.balance);

        if(mCurrentItem == position){
            viewHolder.defaultTv.setVisibility(View.VISIBLE);
        }else {
            viewHolder.defaultTv.setVisibility(View.GONE);
        }

        viewHolder.unbindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUnbindListener.setUnbind(cardEntity.cardId);
            }
        });

        return convertView;
    }

    static class ViewHolder{
        @InjectView(R.id.num)
        TextView numTv;
        @InjectView(R.id.count)
        TextView countTv;
        @InjectView(R.id.balance)
        TextView balanceTv;
        @InjectView(R.id.default_text)
        TextView defaultTv;
        @InjectView(R.id.unbind)
        TextView unbindBtn;
        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    public interface SetUnbindListener{
        public void setUnbind(String id);
    }
}
