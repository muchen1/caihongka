package com.rainbowcard.client.ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    protected OnViewHolderClickListener onViewHolderClickListener;
    /**
     * notice:当list插入或删除item，需要更新，不然position就偏移了
     */
    public int position;
    public T item;

    protected <T extends View> T findView(int id) {
        return (T) itemView.findViewById(id);
    }

    public BaseViewHolder(View itemView) {
        super(itemView);
        itemView.setTag(this);
    }

    public BaseViewHolder(View itemView, OnViewHolderClickListener l) {
        super(itemView);
        this.onViewHolderClickListener = l;
    }

    public void setOnViewHolderClickListener(OnViewHolderClickListener l) {
        this.onViewHolderClickListener = l;
    }

    public void bind(int position, T d) {
        if (this.item != d && this.item != null) {
            onUnbind(this.position, this.item);
        }
        this.position = position;
        this.item = d;
        onBind(position, d);
    }

    public void onUnbind(int position, T d) {
    }

    public abstract void onBind(int position, T d);

    public boolean setCurrentPosition(int currentPosition) {
        return false;
    }

    public interface OnViewHolderClickListener {
        void onClick(BaseViewHolder viewHolder, int viewId);

        void setOnLongClick(BaseViewHolder viewHolder, int type);
    }
}
