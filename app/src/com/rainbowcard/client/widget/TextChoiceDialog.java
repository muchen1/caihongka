package com.rainbowcard.client.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.rainbowcard.client.R;

public class TextChoiceDialog extends Dialog implements View.OnClickListener {

    private OnTextChoiceListener mListener;

    private String[] mChoiceData;

    private NumberPickerView mTextPickerView;

    private TextView mBtCancel;

    private TextView mBtOk;

    public TextChoiceDialog(@NonNull Context context, OnTextChoiceListener listener, String[] choiceData) {
        super(context);
        mListener = listener;
        mChoiceData = choiceData;
        initView();
    }

    public TextChoiceDialog(@NonNull Context context, int themeResId, OnTextChoiceListener listener, String[] choiceData) {
        super(context, themeResId);
        mListener = listener;
        mChoiceData = choiceData;
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_text_choice);
        mTextPickerView = (NumberPickerView) findViewById(R.id.dialog_text_choice_pickview);
        mBtCancel = (TextView) findViewById(R.id.dialog_text_choice_cancel);
        mBtOk = (TextView) findViewById(R.id.dialog_text_choice_ok);
        mBtOk.setOnClickListener(this);
        mBtCancel.setOnClickListener(this);
        mTextPickerView.setDisplayedValues(mChoiceData);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_text_choice_cancel:
                dismiss();
                break;
            case R.id.dialog_text_choice_ok:
                if (mListener != null) {
                    mListener.onTextChoice(mTextPickerView.getContentByCurrValue());
                }
                dismiss();
                break;
            default:
                break;
        }
    }


    public interface OnTextChoiceListener {
        void onTextChoice(String text);
    }

}
