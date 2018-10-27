package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.DiscountEntity;
import com.rainbowcard.client.model.MessageEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 17-5-4.
 */
public class MessageListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<MessageEntity> mMessageEntitys;


    public MessageListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mMessageEntitys = new ArrayList<MessageEntity>();
    }

    public void setMessageEntitys(List<MessageEntity> list) {
        mMessageEntitys.clear();
        mMessageEntitys.addAll(list);
        notifyDataSetChanged();
    }


    public void addMessageEntitys(List<MessageEntity> list) {
        mMessageEntitys.addAll(list);
        notifyDataSetChanged();
    }

    public List<MessageEntity> getMessageEntitys(){
        return  mMessageEntitys;
    }
    @Override
    public int getCount() {
        return mMessageEntitys.size();
    }

    @Override
    public Object getItem(int i) {
        return mMessageEntitys.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.item_message_list, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final MessageEntity messageEntity = mMessageEntitys.get(i);
        holder.titleTv.setText(messageEntity.title);
        holder.timeTv.setText(messageEntity.startTime);
        holder.textTv.setText(messageEntity.text);

        return view;
    }


    static class ViewHolder {
        @InjectView(R.id.tv_title)
        TextView titleTv;
        @InjectView(R.id.tv_text)
        TextView textTv;
        @InjectView(R.id.tv_time)
        TextView timeTv;
        @InjectView(R.id.iv_read)
        ImageView readIv;
        ViewHolder(View view){
            ButterKnife.inject(this, view);
        }
    }
}
