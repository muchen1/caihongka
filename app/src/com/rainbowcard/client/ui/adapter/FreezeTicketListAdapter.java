package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.TicketFreezeModel;
import com.rainbowcard.client.model.TicketStateModel;
import com.rainbowcard.client.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 18-3-6.
 */
public class FreezeTicketListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<TicketFreezeModel.TicketFreezeEntity> mTicketFreezeEntitys;


    public FreezeTicketListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mTicketFreezeEntitys = new ArrayList<TicketFreezeModel.TicketFreezeEntity>();
    }

    public void setTicketFreezeEntitys(List<TicketFreezeModel.TicketFreezeEntity> list) {
        mTicketFreezeEntitys.clear();
        mTicketFreezeEntitys.addAll(list);
        notifyDataSetChanged();
    }


    public void addTicketFreezeEntitys(List<TicketFreezeModel.TicketFreezeEntity> list) {
        mTicketFreezeEntitys.addAll(list);
        notifyDataSetChanged();
    }

    public List<TicketFreezeModel.TicketFreezeEntity> getTicketFreezeEntitys(){
        return  mTicketFreezeEntitys;
    }
    @Override
    public int getCount() {
        return mTicketFreezeEntitys.size();
    }

    @Override
    public Object getItem(int i) {
        return mTicketFreezeEntitys.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.item_ticket_freeze_list, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final TicketFreezeModel.TicketFreezeEntity ticketFreezeEntity = mTicketFreezeEntitys.get(i);
        holder.startDate.setText(ticketFreezeEntity.startDate);
        holder.endDate.setText(ticketFreezeEntity.endDate);
        holder.countTv.setText(String.format(mContext.getString(R.string.discount_count), ticketFreezeEntity.financeCoupon));
        return view;
    }


    static class ViewHolder {
        @InjectView(R.id.start_date)
        TextView startDate;
        @InjectView(R.id.end_date)
        TextView endDate;
        @InjectView(R.id.count_text)
        TextView countTv;
        ViewHolder(View view){
            ButterKnife.inject(this, view);
        }
    }
}
