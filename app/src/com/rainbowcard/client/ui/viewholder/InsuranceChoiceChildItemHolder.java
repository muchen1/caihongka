package com.rainbowcard.client.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    // 下拉选择按钮
    public ImageView mItemStatusArrow;

    // 点击下拉选择框
    public ChildItemChoiceClickListener mListener;

    private InsuranceChoiceModel.ChildItemEntity mEntity;

    public InsuranceChoiceChildItemHolder(View itemView, ChildItemChoiceClickListener l) {
        super(itemView);
        mItemStatus = (TextView) itemView.findViewById(R.id.item_child_choice_text);
        mItemTitle = (TextView) itemView.findViewById(R.id.item_child_textview_title);
        mItemBjmp = (TextView) itemView.findViewById(R.id.item_child_textview_bjmp);
        mItemImgStatus = (ImageView) itemView.findViewById(R.id.item_child_enable);
        mItemStatusArrow = (ImageView) itemView.findViewById(R.id.item_child_choice_arrow);
        mRootView = itemView.findViewById(R.id.item_insurance_choice_child_rootview);
        mListener = l;

        mRootView.setOnClickListener(this);
        mItemStatus.setOnClickListener(this);
        mItemBjmp.setOnClickListener(this);
        mItemStatusArrow.setOnClickListener(this);
    }

    public void onBind(final InsuranceChoiceModel.ChildItemEntity entity) {
        mEntity = entity;
        mItemTitle.setText(entity.insuranceName);
        mItemStatus.setText(entity.insuranceAllPrice.get(entity.insuranceStatusTextKey));
        mItemBjmp.setVisibility(entity.insuranceBjmpStatus == 2 ? View.INVISIBLE : View.VISIBLE);
        mItemBjmp.setTextColor(entity.insuranceBjmpStatus == 1 ? mItemBjmp.getResources().getColor(R.color.app_blue)
                : mItemBjmp.getResources().getColor(R.color.app_gray_masking));
        mItemBjmp.setBackgroundResource(entity.insuranceBjmpStatus == 1 ? R.drawable.bg_insurance_choice_bjmp_enable
                : R.drawable.bg_insurance_choice_bjmp_disable);
        mItemImgStatus.setImageResource(entity.insuranceStatus ? R.drawable.icon_right_enable : R.drawable.icon_right_disable);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 点击选择框的时候回调出来，显示选择框
            case R.id.item_child_choice_text:
            case R.id.item_child_choice_arrow:
                // 直接把这个传递回去，用来弹出选择框

                String[] showText = new String[mEntity.insuranceAllPrice.size()];
                for (int i = 0; i < mEntity.insuranceAllPrice.size(); i++) {
                    showText[i] = mEntity.insuranceAllPrice.valueAt(i);
                    Log.e("daipeng", "showText===" + showText[i]);

                }
                Log.e("daipeng", "showText===" + showText.length);
                new TextChoiceDialog(itemView.getContext(), new TextChoiceDialog.OnTextChoiceListener() {
                    @Override
                    public void onTextChoice(String text) {
                        mEntity.insuranceStatusTextKey = mEntity.insuranceAllPrice.keyAt(mEntity
                                .insuranceAllPrice.indexOfValue(text));
                        mEntity.insuranceStatus = mEntity.insuranceStatusTextKey == 0 ? false : true;
                        // 回调回去
                        if (mListener != null) {
                            mListener.onClick(mEntity, R.id.item_child_choice_text);
                        }
                    }
                }, showText).show();

                break;
            // 点击整个Item，处理状态，并且回调回去
            case R.id.item_insurance_choice_child_rootview:
                // 如果是整个item的点击事件，则改变相应item的view的状态，并且回调回去
                // 如果本来总开关是开启状态,则把所有的都设置为关闭，只在这里设置entity的值，回调到adapter的时候再刷新数据
                if (mEntity.insuranceStatus) {
                    mEntity.insuranceStatus = false;
                    mEntity.insuranceBjmpStatus = 0;
                    // 0的位置应该是 不投保 方案
                    mEntity.insuranceStatusTextKey = mEntity.insuranceAllPrice.keyAt(0);

                } else {
                    // 如果原来是关闭的，就把所有的开启
                    mEntity.insuranceStatus = true;
                    mEntity.insuranceBjmpStatus = 1;
                    // 默认拿到第一个，应该是 投保 文案，或者是 金额
                    mEntity.insuranceStatusTextKey = mEntity.insuranceAllPrice.keyAt(1);
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
                    mEntity.insuranceBjmpStatus = mEntity.insuranceBjmpStatus == 1 ? 0 : 1;
                } else {
                    // 如果原来是关闭的，就把所有的开启
                    mEntity.insuranceStatus = true;
                    mEntity.insuranceBjmpStatus = 1;
                    // 默认拿到第一个，应该是 投保 文案，或者是 金额
                    mEntity.insuranceStatusTextKey = mEntity.insuranceAllPrice.keyAt(1);
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
