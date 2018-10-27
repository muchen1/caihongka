package com.rainbowcard.client.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.rainbowcard.client.R;

/**
 * Created by kleist on 15/4/16.
 */
public class MaterialUIUtils {
    private Context context;

    private Dialog mLoadingDialog;

    public MaterialUIUtils(Context context) {
        this.context = context;
    }

    public void loading() {
        loading(true);
    }

    public void loading(boolean isCancelable) {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.material_loading_dialog, null);// 得到加载view
        CircleProgressBar progressBar = (CircleProgressBar) v.findViewById(R.id.pb_loading);
        progressBar.setColorSchemeResources(android.R.color.holo_blue_light);

        mLoadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        mLoadingDialog.setCancelable(isCancelable);// 判断是否可用“返回键”取消
        mLoadingDialog.setCanceledOnTouchOutside(isCancelable);
        mLoadingDialog.setContentView(v, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        mLoadingDialog.show();
    }

    public void dismissLoading() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    public void loading(boolean isCancelable, int msg) {
        loading(isCancelable);
    }

    public void loading(int msg) {
        loading();
    }
}
