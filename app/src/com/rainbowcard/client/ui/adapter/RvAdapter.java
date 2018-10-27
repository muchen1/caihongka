package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.BannerEntity;
import com.rainbowcard.client.model.MarkerInfoUtil;
import com.rainbowcard.client.utils.DensityUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView的适配器
 * Created by yuandl on 2017-4-18.
 */

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.MyViewHolder> {
    private Context mContext;
    private List<BannerEntity> mDatas;

    /**
     * item的点击事件的长按事件接口
     */
    private OnItemClickListener onItemClickListener;
    /**
     * 瀑布流时的item随机高度
     */
    private List<Integer> heights = new ArrayList<>();

    /**
     * 不同的类型设置item不同的高度
     *
     * @param type
     */

    private int type = 1;

    public RvAdapter(Context context,List<BannerEntity> datas) {
        mContext = context;
        mDatas = new ArrayList<BannerEntity>();
        mDatas.addAll(datas);
        mDatas.addAll(datas);
        for (BannerEntity bannerEntity : mDatas) {
            int height = (int) (Math.random() * 100 + 300);
            heights.add(height);
        }
    }


    public void setType(int type) {
        this.type = type;
    }

    /**
     * 设置点击事件
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(contentView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Log.d("GCCCCCCCCC","???????????"+position);
        RecyclerView.LayoutParams layoutParams;
        if (type == 0) {
            layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else if (type == 1) {
            layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heights.get(position));
            layoutParams.setMargins(2, 2, 2, 2);
        }
        final BannerEntity bannerEntity = mDatas.get(position);
        holder.itemView.setLayoutParams(layoutParams);
//        holder.imageView.setImageResource(datas.get(position));
        if(TextUtils.isEmpty(bannerEntity.img)){
            holder.imageView.setImageResource(R.drawable.shop_default);
        }else {
            Picasso.with(mContext)
                    .load(String.format(mContext.getString(R.string.img_url),bannerEntity.img))
                    .resize(DensityUtil.dip2px(mContext,50),DensityUtil.dip2px(mContext,50))
                    .centerCrop()
                    .placeholder(R.drawable.shop_default)
                    .error(R.drawable.shop_default).into(holder.imageView);
        }

        holder.tv.setText("分类" + position);
        /**设置item点击监听**/
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClickListener(position, mDatas.get(position));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    /**
     * 用于缓存的ViewHolder
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        public TextView tv;
        public LinearLayout layout;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv);
            tv = (TextView) itemView.findViewById(R.id.tv);
        }
    }

    /**
     * 设置item监听的接口
     */
    public interface OnItemClickListener {
        void onItemClickListener(int position, BannerEntity data);

    }
}
