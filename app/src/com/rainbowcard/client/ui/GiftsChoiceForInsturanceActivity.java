package com.rainbowcard.client.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 选完车险之后的赠送礼包选择界面
 */

public class GiftsChoiceForInsturanceActivity extends MyBaseActivity implements View.OnClickListener {

    @InjectView(R.id.nav_back)
    RelativeLayout mBackBtn;
    @InjectView(R.id.midle_title)
    TextView mHeaderTitle;
    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;

    @InjectView(R.id.item_header_gift_list)
    LinearLayout mGiftListLayout;
    @InjectView(R.id.item_middle_old_price)
    TextView mOldPriceTextView;
    @InjectView(R.id.item_middle_new_price)
    TextView mNewPriceTextView;
    @InjectView(R.id.item_middle_jqx_price)
    TextView mJqxPriceTextView;
    @InjectView(R.id.item_middle_ccs_price)
    TextView mCsxPriceTextView;
    @InjectView(R.id.item_bottom_totalprice)
    TextView mTotalPriceTextView;
    @InjectView(R.id.item_bottom_next)
    TextView mNextBt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_gifts_for_insurance);
        parseIntent(getIntent());
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(GiftsChoiceForInsturanceActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(GiftsChoiceForInsturanceActivity.this,true);
        initView();
        initData();
    }

    private void initView() {
        mNextBt.setOnClickListener(this);
    }

    private void initData() {

    }

    private void parseIntent(Intent intent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_bottom_next:
                // 跳转到下一个页面
                break;
        }
    }
}
