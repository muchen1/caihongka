package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.CommentEntity;
import com.rainbowcard.client.model.RecordEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017-1-9.
 */
public class CommentListAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<CommentEntity> mCommentEntitys;

    public CommentListAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mCommentEntitys = new ArrayList<CommentEntity>();
    }

    public void setmCommentEntitys(List<CommentEntity> list){
        mCommentEntitys.clear();
        mCommentEntitys.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mCommentEntitys.size();
    }

    @Override
    public CommentEntity getItem(int position) {
        return mCommentEntitys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.item_comment_list,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CommentEntity commentEntity = mCommentEntitys.get(position);
        viewHolder.numTv.setText(encryptPhone(commentEntity.userPhone));
        viewHolder.dateTv.setText(commentEntity.createdAt);
        viewHolder.contentTv.setText(commentEntity.content);

        return convertView;
    }

    static class ViewHolder{
        @InjectView(R.id.num)
        TextView numTv;
        @InjectView(R.id.date)
        TextView dateTv;
        @InjectView(R.id.content)
        TextView contentTv;
        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    private String encryptPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return phone;
        }

        char[] chars = phone.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (i < 3 || len - i <= 4) {
                continue;
            }
            chars[i] = '*';
        }

        return String.valueOf(chars);
    }
}
