package com.rainbowcard.client.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.InsuranceChoiceModel;
import com.rainbowcard.client.widget.TextChoiceDialog;

public class InsuranceChoiceChildItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    // view的根view
    private View mRootView;
    // 保险类型的文案
    public TextView mItemTitle;
    // 保险类型当前的状态
    public TextView mItemStatus;
    // 保险不计免赔的文案
    public TextView mItemBjmp;
    // 商报情况下该item是否处于可用状态
    public ImageView mItemImgStatus;

    // 点击下拉选择框
    public ChildItemChoiceClickListener mListener;

    private InsuranceChoiceModel.ChildItemEntity mEntity;

    public InsuranceChoiceChildItemHolder(View itemView, ChildItemChoiceClickListener l) {
        super(itemView);
        mItemStatus = (TextView) itemView.findViewById(R.id.item_child_choice_text);
        mItemTitle = (TextView) itemView.findViewById(R.id.item_child_textview_title);
        mItemBjmp = (TextView) itemView.findViewById(R.id.item_child_textview_bjmp);
        mItemImgStatus = (ImageView) itemView.findViewById(R.id.item_child_enable);
        mRootView = itemView.findViewById(R.id.item_insurance_choice_child_rootview);
        mListener = l;

        mRootView.setOnClickListener(this);
        mItemStatus.setOnClickListener(this);
        mItemBjmp.setOnClickListener(this);
    }

    public void onBind(final InsuranceChoiceModel.ChildItemEntity entity) {
        mEntity = entity;
        mItemTitle.setText(entity.insuranceType);
        mItemStatus.setText(entity.insuranceStatusText);
        mItemBjmp.setVisibility(entity.insuranceBjmp ? View.VISIBLE : View.INVISIBLE);
        mItemBjmp.setBackgroundResource(entity.insuranceStatus ? R.drawable.bg_insurance_choice_bjmp_enable
                : R.drawable.bg_insurance_choice_bjmp_disable);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 点击选择框的时候回调出来，显示选择框
            case R.id.item_child_choice_text:
                // 直接把这个传递回去，用来弹出选择框
                new TextChoiceDialog(itemView.getContext(), new TextChoiceDialog.OnTextChoiceListener() {
                    @Override
                    public void onTextChoice(String text) {
                         mEntity.insuranceStatusText = text;
                         mItemStatus.setText(text);
                    }
                }, mEntity.insuranceAllPrice).show();
                // 回调回去
                if (mListener != null) {
                    mListener.onClick(mEntity, R.id.item_child_choice_text);
                }
                break;
            // 点击整个Item，处理状态，并且回调回去
            case R.id.item_insurance_choice_child_rootview:
                // 如果是整个item的点击事件，则改变相应item的view的状态，并且回调回去
                // 如果本来总开关是开启状态,则把所有的都设置为关闭，只在这里设置entity的值，回调到adapter的时候再刷新数据
                if (mEntity.insuranceStatus) {
                    mEntity.insuranceStatus = false;
                    mEntity.insuranceBjmp = false;
                    // 0的位置应该是 不投保 方案
                    mEntity.insuranceStatusText = mEntity.insuranceAllPrice[0];
                } else {
                    // 如果原来是关闭的，就把所有的开启
                    mEntity.insuranceStatus = true;
                    mEntity.insuranceBjmp = true;
                    // 默认拿到第一个，应该是 投保 文案，或者是 金额
                    mEntity.insuranceStatusText = mEntity.insuranceAllPrice[1];
                }
                if (mListener != null) {
                    mListener.onClick(mEntity, R.id.item_insurance_choice_child_rootview);
                }
                break;
                // 点击不计免赔 文案
            case R.id.item_child_textview_bjmp:
                // 如果是不计免赔文案的点击，改变状态，回调回去
                // 如果原来是没有选择该item，则首先把该item选中;如果本来就是选中状态，则只改变 不计免赔 自身的状态
                if (mEntity.insuranceStatus) {
                    mEntity.insuranceBjmp = !mEntity.insuranceBjmp;
                } else {
                    // 如果原来是关闭的，就把所有的开启
                    mEntity.insuranceStatus = true;
                    mEntity.insuranceBjmp = true;
                    // 默认拿到第一个，应该是 投保 文案，或者是 金额
                    mEntity.insuranceStatusText = mEntity.insuranceAllPrice[1];
                }
                if (mListener != null) {
                    mListener.onClick(mEntity, R.id.item_child_textview_bjmp);
                }
                break;

        }
    }

    public interface ChildItemChoiceClickListener {
        void onClick(InsuranceChoiceModel.ChildItemEntity entity, int viewId);
    }
}