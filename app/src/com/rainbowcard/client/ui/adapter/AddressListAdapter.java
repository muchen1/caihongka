package com.rainbowcard.client.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.rainbowcard.client.R;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.model.AddressEntity;
import com.rainbowcard.client.ui.EditAddressActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2016/4/18.
 */
public class AddressListAdapter extends BaseAdapter {
    private Context mContext;
    private List<AddressEntity> mAddressEntityList;
    private LayoutInflater mLayoutInflater;
    private SetAddressListener setAddressListener;

    public AddressListAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mAddressEntityList = new ArrayList<AddressEntity>();
    }
    public AddressListAdapter(Context context, SetAddressListener listener){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mAddressEntityList = new ArrayList<AddressEntity>();
        setAddressListener = listener;
    }

    public void setAddresseList(List<AddressEntity> list){
        mAddressEntityList.clear();
        mAddressEntityList.addAll(list);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return mAddressEntityList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAddressEntityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View reusableView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (reusableView == null) {
            reusableView = mLayoutInflater.inflate(R.layout.item_address_list, parent, false);
            viewHolder = new ViewHolder(reusableView);
            reusableView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) reusableView.getTag();
        }
        final AddressEntity addressEntity = mAddressEntityList.get(position);
        viewHolder.tvName.setText(addressEntity.userName);
        viewHolder.tvNumber.setText(addressEntity.phone);
        viewHolder.tvAddr.setText(addressEntity.province+addressEntity.city+addressEntity.area+addressEntity.addr);
        viewHolder.defaultLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addressEntity.isDefault != Constants.IS_CHIEFLY) {
                    setAddressListener.setDefaultAddr(addressEntity.id);
                }
            }
        });
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAddressListener.delAddr(addressEntity.id);
            }
        });
        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditAddressActivity.class);
                intent.putExtra(Constants.KEY_ADDRESS_ENTITY,addressEntity);
                intent.putExtra(Constants.KEY_TYPE,1);
                mContext.startActivity(intent);
            }
        });
        viewHolder.delIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAddressListener.delAddr(addressEntity.id);
            }
        });
        viewHolder.editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditAddressActivity.class);
                intent.putExtra(Constants.KEY_ADDRESS_ENTITY,addressEntity);
                intent.putExtra(Constants.KEY_TYPE,1);
                mContext.startActivity(intent);
            }
        });
        if(addressEntity.isDefault  == Constants.IS_CHIEFLY){
//            viewHolder.tvDefault.setVisibility(View.VISIBLE);
            viewHolder.selectIv.setImageResource(R.drawable.me__btn_selected);
        }else {
//            viewHolder.tvDefault.setVisibility(View.GONE);
            viewHolder.selectIv.setImageResource(R.drawable.me_btn_default);
        }

        return reusableView;
    }

    static class ViewHolder {

        @InjectView(R.id.name)
        TextView tvName;
        @InjectView(R.id.number)
        TextView tvNumber;
        @InjectView(R.id.address)
        TextView tvAddr;
        @InjectView(R.id.default_layout)
        RelativeLayout defaultLayout;
        @InjectView(R.id.select_iv)
        ImageView selectIv;
        @InjectView(R.id.delete)
        TextView delete;
        @InjectView(R.id.edit)
        TextView edit;
        @InjectView(R.id.edit_icon)
        ImageView editIcon;
        @InjectView(R.id.del_icon)
        ImageView delIcon;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    public interface SetAddressListener{
        public void setDefaultAddr(int id);
        public void delAddr(int id);
    }
}
