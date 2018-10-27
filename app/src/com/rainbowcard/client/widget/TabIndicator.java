package com.rainbowcard.client.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


import com.rainbowcard.client.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gc on 16-4-17.
 */
public class TabIndicator extends LinearLayout {
    private List<Button> mButtons;
    private OnTabClickListener mOnTabClickListener;

    public TabIndicator(Context context) {
        super(context);
        init();
    }

    public TabIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TabIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setOnTabClickListener(OnTabClickListener listener) {
        mOnTabClickListener = listener;
    }

    public void init() {
        setOrientation(HORIZONTAL);
        mButtons = new ArrayList<Button>();
    }

    public void clearButtons() {
        for (Button button : mButtons) {
            removeView(button);
        }
        mButtons.clear();
    }

    public void addButtons(int screenWidth,String... texts) {
        clearButtons();

        if (texts.length <= 0) {
            return;
        }

        for (int i = 0; i < texts.length; i++) {
            final Button button = new Button(getContext());
            LayoutParams lp = new LayoutParams((9*screenWidth)/30, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.setMargins(5,0,5,0);
            button.setSingleLine(true);
            button.setLayoutParams(lp);

            if (i == 0) {
                button.setBackgroundResource(R.drawable.tab_button_selected_selector);
                button.setTextColor(getContext().getResources().getColor(R.color.tab_selected_text));
            } else {
                button.setBackgroundResource(R.drawable.tab_button_unselected_selector);
                button.setTextColor(getContext().getResources().getColor(R.color.black));
            }
            button.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.ios_tab_font_size));
            button.setGravity(Gravity.CENTER);
            if (texts[i] == null) {
                button.setText("");
            } else {
                button.setText(texts[i]);
            }
            button.setOnClickListener(new OnButtonClickListener(i, button));


            mButtons.add(button);
            addView(button);
        }
    }

    public void select(int pos) {
        final Button button = mButtons.get(pos);
        button.setBackgroundResource(R.drawable.tab_button_selected_selector);
        button.setTextColor(getContext().getResources().getColor(R.color.tab_selected_text));


        for (int i = 0, len = mButtons.size(); i < len; i++) {
            if (i == pos) {
                continue;
            }
            Button unselectButton = mButtons.get(i);
            unselectButton.setBackgroundResource(R.drawable.tab_button_unselected_selector);
            unselectButton.setTextColor(getContext().getResources().getColor(R.color.black));
        }
    }

    public static interface OnTabClickListener {
        public Boolean onClick(int pos, Button button);
    }

    class OnButtonClickListener implements OnClickListener {
        private int pos;
        private Button button;

        public OnButtonClickListener(int pos, Button button) {
            this.pos = pos;
            this.button = button;
        }

        @Override
        public void onClick(View view) {
            if (mOnTabClickListener != null) {
                Boolean shouldSelected = mOnTabClickListener.onClick(pos, button);
                if (shouldSelected) {
                    select(pos);
                }
            }
        }
    }
}
