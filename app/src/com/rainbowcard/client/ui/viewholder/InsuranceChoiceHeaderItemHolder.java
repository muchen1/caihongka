package com.rainbowcard.client.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.InsuranceChoiceModel;

public class InsuranceChoiceHeaderItemHolder extends RecyclerView.ViewHolder {

    // 保险类型的文案
    public TextView mHeaderTitle;
    // 本大类的保险类型是否要进行续保
    public Switch mHeaderSwitch;
    // 点击开启按钮时候响应事件
    public HeaderItemSwitchClickListener mListener;

    public InsuranceChoiceHeaderItemHolder(View itemView, HeaderItemSwitchClickListener listener) {
        super(itemView);
        mHeaderSwitch = (Switch) itemView.findViewById(R.id.item_header_switch);
        mHeaderTitle = (TextView) itemView.findViewById(R.id.item_header_title);
        mListener = listener;

    }

    public void onBind(final InsuranceChoiceModel.HeaderItemEntity entity) {
        mHeaderTitle.setText(entity.insuranceType);
        mHeaderSwitch.setChecked(entity.isuranceSwitch);
        mHeaderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                entity.isuranceSwitch = isChecked;
                if (mListener != null) {
                    mListener.onCheckedChanged(isChecked, entity);
                }
            }
        });
    }

    public interface HeaderItemSwitchClickListener {
        void onCheckedChanged(boolean isChecked, InsuranceChoiceModel.BaseEntity entity);
    }

}
