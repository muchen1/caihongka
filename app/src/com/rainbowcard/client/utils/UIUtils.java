package com.rainbowcard.client.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rainbowcard.client.R;
import com.rainbowcard.client.base.YHApplication;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by gc on 2016/4/28.
 */
public class UIUtils {

    private static Context context = YHApplication.instance();

    public static void toast(String tip) {
        Toast.makeText(YHApplication.instance(), tip, Toast.LENGTH_SHORT).show();
    };
    public static Dialog alertSwitchHeadPortraitBottom(Context context) {
        final Dialog buttonListBottomDialog = new Dialog(context,
                R.style.alert_buttonlist_bottom);// 创建自定义样式dialog
        buttonListBottomDialog.setCancelable(true);
        buttonListBottomDialog
                .setContentView(R.layout.ui_alert_switch_headportrait_bottom);
        View blankView = buttonListBottomDialog.findViewById(R.id.blank_view);
        blankView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonListBottomDialog.dismiss();
            }
        });

        return buttonListBottomDialog;
    }

    public static Dialog alertButtonListBottom(Context context,String title,boolean isShow) {
        final Dialog buttonListBottomDialog = new Dialog(context,
                R.style.alert_buttonlist_bottom);// 创建自定义样式dialog
        buttonListBottomDialog.setCancelable(true);
        buttonListBottomDialog
                .setContentView(R.layout.ui_alert_buttonlist_bottom);
        TextView textView = (TextView) buttonListBottomDialog.findViewById(R.id.title);
        textView.setText(title);
        if(isShow){
            textView.setVisibility(View.GONE);
        }
        buttonListBottomDialog
                .findViewById(R.id.alert_buttonlist_bottom_cancel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        buttonListBottomDialog.dismiss();
                    }
                });
        buttonListBottomDialog.show();
        return buttonListBottomDialog;
    }

    public static void addButtonToButtonListBottom(final Dialog buttonListBottomDialog, String str,
                                                   final View.OnClickListener listener) {
        if (buttonListBottomDialog != null) {
            int y = context.getResources().getDimensionPixelOffset(
                    R.dimen.dp_m_y_42);
            ViewGroup vg = (ViewGroup) buttonListBottomDialog
                    .findViewById(R.id.alert_buttonlist);
            final Button button = new Button(buttonListBottomDialog.getContext());
            button.setBackgroundResource(R.drawable.btn_select_item);
            button.setText(str);
            button.setTextColor(context.getResources().getColor(R.color.app_black));
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(v);
                    }
                    buttonListBottomDialog.dismiss();
                }
            });
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, y);
            layoutParams.setMargins(0, 0, 0, 0);
            button.setLayoutParams(layoutParams);
            vg.addView(button, vg.getChildCount() - 1);
            View view = new View(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,context.getResources().getDimensionPixelSize(R.dimen.one_px));
            lp.setMargins(0,0,0,0);
            view.setLayoutParams(lp);
            view.setBackgroundColor(context.getResources().getColor(R.color.app_tiny_gray));
            vg.addView(view, vg.getChildCount() - 1);
            vg.requestLayout();
        }
    }

    public static void showDialog(String title, String tip,Activity mContext) {
        final Dialog dialog = new Dialog(mContext,R.style.loading_dialog);
        dialog.setContentView(R.layout.ui_alert_dialog);
       Window dialogWindow = dialog.getWindow();
        Display display = mContext.getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = -80;
        lp.width = (int)(display.getWidth() * 0.95);
        dialogWindow.setAttributes(lp);

        TextView alertTitle = (TextView) dialog.findViewById(R.id.alert_title);
        TextView alertTip = (TextView) dialog.findViewById(R.id.alert_tip);
        TextView alertOk = (TextView) dialog.findViewById(R.id.alert_ok);
        TextView alertCancle = (TextView) dialog.findViewById(R.id.alert_cancel);
        alertTitle.setText(title);
        alertTip.setText(tip);
//        alertOk.setText("继续");
        alertCancle.setText("取消");
        alertCancle.setVisibility(View.GONE);
        dialog.findViewById(R.id.alert_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YHApplication.instance().exit();
            }
        });

        dialog.findViewById(R.id.alert_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static void showConfirmDialog(final Activity context,final String phone){
        final Dialog dialog = new Dialog(context,R.style.loading_dialog);
        dialog.setContentView(R.layout.ui_phone_dialog);
        Window dialogWindow = dialog.getWindow();
        Display display = context.getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = -80;
        lp.width = (int)(display.getWidth() * 0.8);
        lp.height = (int)(display.getHeight() * 0.6);
        dialogWindow.setAttributes(lp);
        TextView phoneNum = (TextView) dialog.findViewById(R.id.phone_num);
        phoneNum.setText(phone);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
        TextView confirm = (TextView) dialog.findViewById(R.id.confirm);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                context.startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
    }

    public static boolean setMiuiStatusBarDarkMode(Activity activity, boolean darkmode) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean setMeizuStatusBarDarkIcon(Activity activity, boolean dark) {
        boolean result = false;
        if (activity != null) {
            try {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                activity.getWindow().setAttributes(lp);
                result = true;
            } catch (Exception e) {
            }
        }
        return result;
    }
}
