package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.IllegalEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2016-6-22.
 */
public class IllegalEntityAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<IllegalEntity> mIllegalEntitys;

    public IllegalEntityAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mIllegalEntitys = new ArrayList<IllegalEntity>();
    }

    public void setmIllegalEntitys(List<IllegalEntity> list){
        mIllegalEntitys.clear();
        mIllegalEntitys.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mIllegalEntitys.size();
    }

    @Override
    public IllegalEntity getItem(int position) {
        return mIllegalEntitys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.item_illegal_list,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        IllegalEntity illegalEntity = mIllegalEntitys.get(position);
        viewHolder.date.setText(illegalEntity.date);
        viewHolder.addr.setText(illegalEntity.area);
        viewHolder.illegal.setText(illegalEntity.act);
        viewHolder.fen.setText(illegalEntity.fen);
        viewHolder.money.setText(illegalEntity.money);

        if("1".equals(illegalEntity.handled)){
            viewHolder.handled.setText("已处理");
            viewHolder.handled.setTextColor(mContext.getResources().getColor(R.color.tab_unselected_text));
        }else {
            viewHolder.handled.setText("未处理");
            viewHolder.handled.setTextColor(mContext.getResources().getColor(R.color.money_color));
        }

        return convertView;
    }

    static class ViewHolder{
        @InjectView(R.id.date)
        TextView date;
        @InjectView(R.id.handled)
        TextView handled;
        @InjectView(R.id.addr)
        TextView addr;
        @InjectView(R.id.illegal)
        TextView illegal;
        @InjectView(R.id.fen)
        TextView fen;
        @InjectView(R.id.money)
        TextView money;
        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
