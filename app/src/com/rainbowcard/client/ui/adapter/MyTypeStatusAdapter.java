package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gc on 16/11/3.
 */
public class MyTypeStatusAdapter extends BaseAdapter {

    private List<String> mGetTypeList = new ArrayList<String>();
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int mWindowWidth;
    private WindowManager mWindowManager;
    private int mPosition;

    public MyTypeStatusAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        mWindowWidth = mWindowManager.getDefaultDisplay().getWidth();
        mGetTypeList.add("线上领取");
        mGetTypeList.add("线下领取");
    }

    public void setMPosition(int position) {
        this.mPosition = position;
        notifyDataSetChanged();
    }

    public void setmGetTypeList(List<String> list){
        mGetTypeList.clear();
        mGetTypeList = list;
    }

    @Override
    public int getCount() {
        return mGetTypeList.size();
    }

    @Override
    public Object getItem(int i) {
        return mGetTypeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view == null) {
            holder = new ViewHolder();
            view = mLayoutInflater.inflate(R.layout.order_status_item,null);
            holder.mOrderStatus = (TextView) view.findViewById(R.id.tx_order_status);
            holder.mSelectIv = (ImageView) view.findViewById(R.id.iv_select);
            view.setTag(holder);
        }else {
            holder = (ViewHolder)view.getTag();
        }
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.mOrderStatus.getLayoutParams();
        lp.width = mWindowWidth / mGetTypeList.size();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.mSelectIv.getLayoutParams();
        layoutParams.width = mWindowWidth / mGetTypeList.size() - 40;
        layoutParams.height = DensityUtil.dip2px(mContext,2);
        holder.mOrderStatus.setText(mGetTypeList.get(i));

        if(i == mPosition) {
            holder.mOrderStatus.setTextColor(mContext.getResources().getColor(R.color.tab_unselected_text));
            holder.mSelectIv.setVisibility(View.VISIBLE);
            holder.mSelectIv.setLayoutParams(layoutParams);
        }else {
                holder.mOrderStatus.setTextColor(mContext.getResources().getColor(R.color.app_black));
            holder.mSelectIv.setVisibility(View.GONE);
        }

        return view;
    }

    private static class ViewHolder {
        private TextView mOrderStatus;
        private ImageView mSelectIv;
    }
}
