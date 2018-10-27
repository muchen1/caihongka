package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.BannerEntity;
import com.rainbowcard.client.utils.DensityUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gc on 2016/9/12.
 */
public class GridViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<BannerEntity> mDatas;
    private LayoutInflater inflater;
    /**
     * 页数下标,从0开始(当前是第几页)
     */
    private int curIndex;
    /**
     * 每一页显示的个数
     */
    private int pageSize;

    public GridViewAdapter(Context context, List<BannerEntity> datas, int curIndex, int pageSize) {
        mContext = context;
        inflater = LayoutInflater.from(context);
        mDatas = datas;
        this.curIndex = curIndex;
        this.pageSize = pageSize;
    }

    /**
     * 先判断数据集的大小是否足够显示满本页？mDatas.size() > (curIndex+1)*pageSize,
     * 如果够，则直接返回每一页显示的最大条目个数pageSize,
     * 如果不够，则有几项返回几,(mDatas.size() - curIndex * pageSize);(也就是最后一页的时候就显示剩余item)
     */
    @Override
    public int getCount() {
        return mDatas.size() > (curIndex + 1) * pageSize ? pageSize : (mDatas.size() - curIndex * pageSize);

    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position + curIndex * pageSize);
    }

    @Override
    public long getItemId(int position) {
        return position + curIndex * pageSize;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_gridview, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tv = (TextView) convertView.findViewById(R.id.textView);
            viewHolder.iv = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        /**
         * 在给View绑定显示的数据时，计算正确的position = position + curIndex * pageSize，
         */
        int pos = position + curIndex * pageSize;
        if(pos == 7 && mDatas.size() > 8){
            viewHolder.tv.setText("更多");
            Picasso.with(mContext)
                    .load(R.drawable.more)
                    .resize(DensityUtil.dip2px(mContext,30),DensityUtil.dip2px(mContext,30))
                    .centerCrop()
                    .placeholder(R.drawable.shop_default)
                    .error(R.drawable.shop_default).into(viewHolder.iv);
        }else {
            viewHolder.tv.setText(mDatas.get(pos).title);
            if(TextUtils.isEmpty(mDatas.get(pos).img)){
                viewHolder.iv.setImageResource(R.drawable.shop_default);
            }else {
                Picasso.with(mContext)
                        .load(String.format(mContext.getString(R.string.img_url),mDatas.get(pos).img))
                        .resize(DensityUtil.dip2px(mContext,30),DensityUtil.dip2px(mContext,30))
                        .centerCrop()
                        .placeholder(R.drawable.shop_default)
                        .error(R.drawable.shop_default).into(viewHolder.iv);
            }
        }
        return convertView;
    }


    class ViewHolder {
        public TextView tv;
        public ImageView iv;
    }
}