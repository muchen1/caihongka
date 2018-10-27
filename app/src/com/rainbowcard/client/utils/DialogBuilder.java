package com.rainbowcard.client.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rainbowcard.client.R;


/**
 * Created by gc on 14-8-28.
 */
public class DialogBuilder {



    public static interface OnValueSelectedListener {
        public void onSelect(int pos, String value);
    }


    public static Dialog buildOneButtonDialog(Context context, String text, final OnConfirmListener listener) {
        return buildOneButtonDialog(context, text, listener, true);
    }

    public static Dialog buildOneButtonDialog(Context context, String text, final OnConfirmListener listener, boolean cancelable) {
        final Dialog dialog = new Dialog(context, R.style.common_dialog);
        dialog.setContentView(R.layout.dialog_one_button_alert);

        TextView textView = (TextView) dialog.findViewById(R.id.tv_content);
        Button button = (Button) dialog.findViewById(R.id.btn);

        textView.setText(text == null ? "" : text);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onConfirm();
                }
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onConfirm();
                }
            }
        });

        dialog.setCancelable(cancelable);

        return dialog;
    }

    public static interface OnConfirmListener {
        public void onConfirm();
    }

    public static Dialog buildOneButtonDialog(Context context, String title, String text, final OnDialogChangeListener listener, boolean cancelable) {

        final Dialog dialog = new Dialog(context, R.style.common_dialog);
        dialog.setContentView(R.layout.dialog_one_button_with_title);

        TextView textView = (TextView) dialog.findViewById(R.id.tv_content);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tv_title);
        Button button = (Button) dialog.findViewById(R.id.btn);

        textView.setText(text == null ? "" : text);
        tvTitle.setText(text == null ? "" : title);


        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onConfirm();
                }
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onCancel();
                }
            }
        });

        dialog.setCancelable(cancelable);

        return dialog;
    }

    public static interface OnDialogChangeListener {
        public void onConfirm();
        public void onCancel();
    }

    public static Dialog buildTwoButtonDialog(Context context, String title, String text, final OnDialogChangeListener listener, boolean cancelable) {
        return buildTwoButtonDialog(context, title, text, null, null, listener, cancelable);
    }

    public static Dialog buildTwoButtonDialog(Context context, String title, String text,
                                              String confirmButtonText, String cancelButtonText,
                                              final OnDialogChangeListener listener, boolean cancelable) {

        final Dialog dialog = new Dialog(context, R.style.common_dialog);
        dialog.setContentView(R.layout.dialog_two_button_with_title);

        TextView textView = (TextView) dialog.findViewById(R.id.tv_content);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tv_title);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);

        if (!TextUtils.isEmpty(confirmButtonText)) {
            btnConfirm.setText(confirmButtonText);
        }

        if (!TextUtils.isEmpty(cancelButtonText)) {
            btnCancel.setText(cancelButtonText);
        }

        if (TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(title);
        }

        textView.setText(text == null ? "" : text);


        btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onConfirm();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onCancel();
                }
            }
        });

        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onCancel();
                }
            }
        });


        return dialog;
    }
}
