package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.model.ShopOrderEntity;
import com.rainbowcard.client.ui.CommentActivity;
import com.rainbowcard.client.utils.DensityUtil;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 16-10-31.
 */
public class ShopOrderListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<ShopOrderEntity> mShopOrderEntitys;
    private String mKeyword;
    SetOrderListener setOrderListener;

    public ShopOrderListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mShopOrderEntitys = new ArrayList<ShopOrderEntity>();
    }
    public ShopOrderListAdapter(Context context,SetOrderListener listener) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mShopOrderEntitys = new ArrayList<ShopOrderEntity>();
        setOrderListener = listener;
    }

    public void setShopOrderEntitys(List<ShopOrderEntity> list) {
        mShopOrderEntitys.clear();
        mShopOrderEntitys.addAll(list);
        notifyDataSetChanged();
    }

    public void addmShopOrderEntitys(List<ShopOrderEntity> list) {
        mShopOrderEntitys.addAll(list);
        notifyDataSetChanged();
    }

    public List<ShopOrderEntity> getShopOrderEntitys(){
        return  mShopOrderEntitys;
    }
    @Override
    public int getCount() {
        return mShopOrderEntitys.size();
    }

    @Override
    public Object getItem(int i) {
        return mShopOrderEntitys.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.item_shop_order_list, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final ShopOrderEntity shopOrderEntity = mShopOrderEntitys.get(i);
        holder.nameTv.setText(shopOrderEntity.name);
        switch (shopOrderEntity.consumption){
            case 1:
                holder.statusTv.setText("待使用");
                holder.commentTv.setVisibility(View.GONE);
                holder.payBtn.setVisibility(View.GONE);
                holder.cancelBtn.setVisibility(View.GONE);
                holder.needPay.setText("实付款：");
                break;
            case 2:
                holder.statusTv.setText("待评论");
                holder.commentTv.setVisibility(View.VISIBLE);
                holder.payBtn.setVisibility(View.GONE);
                holder.cancelBtn.setVisibility(View.GONE);
                holder.needPay.setText("实付款：");
                break;
            case 3:
                holder.statusTv.setText("已评价");
                holder.commentTv.setVisibility(View.GONE);
                holder.payBtn.setVisibility(View.GONE);
                holder.cancelBtn.setVisibility(View.GONE);
                holder.needPay.setText("实付款：");
                break;
            case 4:
                holder.statusTv.setText("已过期");
                holder.commentTv.setVisibility(View.GONE);
                holder.payBtn.setVisibility(View.GONE);
                holder.cancelBtn.setVisibility(View.GONE);
                holder.needPay.setText("需付款：");
                break;
            case 8:
                holder.statusTv.setText("待支付");
                holder.commentTv.setVisibility(View.GONE);
                holder.payBtn.setVisibility(View.VISIBLE);
                holder.cancelBtn.setVisibility(View.VISIBLE);
                holder.needPay.setText("需付款：");
                break;
            default:
                holder.statusTv.setText("未知");
                holder.commentTv.setVisibility(View.GONE);
                holder.payBtn.setVisibility(View.GONE);
                holder.cancelBtn.setVisibility(View.GONE);
                holder.needPay.setText("需付款：");
        }
        holder.addrTv.setText(shopOrderEntity.addr);
        if(TextUtils.isEmpty(shopOrderEntity.sonShopType)){
            holder.shopType.setText(shopOrderEntity.shopType);
        }else {
            holder.shopType.setText(String.format(mContext.getString(R.string.service_type), shopOrderEntity.shopType, shopOrderEntity.sonShopType));
        }
//        holder.shopType.setText(String.format(mContext.getString(R.string.service_type),shopOrderEntity.shopType,shopOrderEntity.sonShopType));
        holder.shopMoney.setText(String.format(mContext.getString(R.string.price),new
                DecimalFormat("##,###,###,###,##0.00").format(shopOrderEntity.shopPrice)));
        holder.condeType.setText(shopOrderEntity.codeType);
        holder.codeTv.setText(shopOrderEntity.code);
        holder.dateTv.setText(shopOrderEntity.date);
        if (!TextUtils.isEmpty(shopOrderEntity.shopImg)) {
            Picasso.with(mContext)
                    .load(String.format(mContext.getString(R.string.img_url),shopOrderEntity.shopImg))
                    .resize(DensityUtil.dip2px(mContext,70),DensityUtil.dip2px(mContext,70))
                    .centerCrop()
                    .placeholder(R.drawable.order_default)
                    .error(R.drawable.order_default).into(holder.shopIcon);
        }else {
            holder.shopIcon.setImageResource(R.drawable.order_default);
        }
        holder.payMoney.setText(String.format(mContext.getString(R.string.price),new
                DecimalFormat("##,###,###,###,##0.00").format(shopOrderEntity.payMoney)));
        holder.commentTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mContext, CommentActivity.class);
//                intent.putExtra(Constants.KEY_SHOP_IMG,shopOrderEntity.shopImg);
//                intent.putExtra(Constants.KEY_TRADE_NO,shopOrderEntity.tradeNo);
//                mContext.startActivity(intent);
                setOrderListener.comment(i);
            }
        });
        holder.payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOrderListener.pay(i);
            }
        });
        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOrderListener.cancel(i);
            }
        });

        return view;
    }


    static class ViewHolder {
        @InjectView(R.id.tv_name)
        TextView nameTv;
        @InjectView(R.id.status)
        TextView statusTv;
        @InjectView(R.id.shop_icon)
        ImageView shopIcon;
        @InjectView(R.id.tv_addr)
        TextView addrTv;
        @InjectView(R.id.shop_type)
        TextView shopType;
        @InjectView(R.id.shop_money)
        TextView shopMoney;
        @InjectView(R.id.code_type)
        TextView condeType;
        @InjectView(R.id.code)
        TextView codeTv;
        @InjectView(R.id.date)
        TextView dateTv;
        @InjectView(R.id.comment)
        TextView commentTv;
        @InjectView(R.id.pay_btn)
        TextView payBtn;
        @InjectView(R.id.cancel_btn)
        TextView cancelBtn;
        @InjectView(R.id.need_pay)
        TextView needPay;
        @InjectView(R.id.pay_money)
        TextView payMoney;
        ViewHolder(View view){
            ButterKnife.inject(this, view);
        }
    }

    public interface SetOrderListener{
        public void pay(int position);
        public void cancel(int position);
        public void comment(int position);
    }
}
