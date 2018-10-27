package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.InviterModel;
import com.rainbowcard.client.model.TicketStateModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 18-3-30.
 */
public class InviterListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<InviterModel.InviterEntity> mInviterEntitys;


    public InviterListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mInviterEntitys = new ArrayList<InviterModel.InviterEntity>();
    }

    public void setInviterEntitys(List<InviterModel.InviterEntity> list) {
        mInviterEntitys.clear();
        mInviterEntitys.addAll(list);
        notifyDataSetChanged();
    }


    public void addInviterEntitys(List<InviterModel.InviterEntity> list) {
        mInviterEntitys.addAll(list);
        notifyDataSetChanged();
    }

    public List<InviterModel.InviterEntity> getInviterEntitys(){
        return  mInviterEntitys;
    }
    @Override
    public int getCount() {
        return mInviterEntitys.size();
    }

    @Override
    public Object getItem(int i) {
        return mInviterEntitys.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.item_inviter_list, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final InviterModel.InviterEntity inviterEntity = mInviterEntitys.get(i);
        holder.titleTv.setText(inviterEntity.phone);
        holder.dateTv.setText(inviterEntity.financeTime);
        return view;
    }


    static class ViewHolder {
        @InjectView(R.id.title_text)
        TextView titleTv;
        @InjectView(R.id.date_text)
        TextView dateTv;
        ViewHolder(View view){
            ButterKnife.inject(this, view);
        }
    }
}
