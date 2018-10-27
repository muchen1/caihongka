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
import com.rainbowcard.client.model.TicketStateModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 18-3-6.
 */
public class InviteTicketListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<TicketStateModel.TicketStateEntity> mTicketStateEntitys;


    public InviteTicketListAdapter(Context context) {
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
        holder.titleTv.setText(ticketStateEntity.name);
        holder.dateTv.setText(ticketStateEntity.createdAt);

        SpannableString sp = new SpannableString(String.format(mContext.getResources().getString(R.string.gain_count),ticketStateEntity.details));

        String memberStateMent = String.format(mContext.getResources().getString(R.string.gain_count),ticketStateEntity.details);

        //变色
        sp.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.money_color)), memberStateMent.indexOf( String.valueOf(ticketStateEntity.details)),
                memberStateMent.indexOf(String.valueOf(ticketStateEntity.details)) + String.valueOf(ticketStateEntity.details).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //加粗
        sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), memberStateMent.indexOf( String.valueOf(ticketStateEntity.details)),
                memberStateMent.indexOf(String.valueOf(ticketStateEntity.details)) + String.valueOf(ticketStateEntity.details).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.countTv.setText(sp);

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
