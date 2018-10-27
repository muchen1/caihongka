package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.TicketStateModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 18-3-6.
 */
public class CapitalDetailListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<TicketStateModel.TicketStateEntity> mTicketStateEntitys;


    public CapitalDetailListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mTicketStateEntitys = new ArrayList<TicketStateModel.TicketStateEntity>();
    }

    public void setTicketStateEntitys(List<TicketStateModel.TicketStateEntity> list) {
        mTicketStateEntitys.clear();
        mTicketStateEntitys.addAll(list);
        notifyDataSetChanged();
    }


    public void addTicketStateEntitys(List<TicketStateModel.TicketStateEntity> list) {
        mTicketStateEntitys.addAll(list);
        notifyDataSetChanged();
    }

    public List<TicketStateModel.TicketStateEntity> getTicketStateEntitys(){
        return  mTicketStateEntitys;
    }
    @Override
    public int getCount() {
        return mTicketStateEntitys.size();
    }

    @Override
    public Object getItem(int i) {
        return mTicketStateEntitys.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.item_ticket_state_list, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final TicketStateModel.TicketStateEntity ticketStateEntity = mTicketStateEntitys.get(i);
        holder.titleTv.setText(ticketStateEntity.describe);
        holder.dateTv.setText(ticketStateEntity.createdAt);
        if(ticketStateEntity.isAdd == 1) {
            holder.countTv.setText(String.format(mContext.getString(R.string.add_money), new
                    DecimalFormat("##,###,###,###,##0.00").format(ticketStateEntity.details)));
            holder.countTv.setTextColor(mContext.getResources().getColor(R.color.money_color));
        }else {
            holder.countTv.setText(String.format(mContext.getString(R.string.reduce_money), new
                    DecimalFormat("##,###,###,###,##0.00").format(ticketStateEntity.details)));
            holder.countTv.setTextColor(mContext.getResources().getColor(R.color.app_black));
        }

        return view;
    }


    static class ViewHolder {
        @InjectView(R.id.title_text)
        TextView titleTv;
        @InjectView(R.id.date_text)
        TextView dateTv;
        @InjectView(R.id.count_text)
        TextView countTv;
        ViewHolder(View view){
            ButterKnife.inject(this, view);
        }
    }
}
