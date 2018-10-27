package com.rainbowcard.client.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.rainbowcard.client.R;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.model.DiscountEntity;
import com.rainbowcard.client.ui.adapter.UsableDiscountListAdapter;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.widget.ScrollToFooterLoadMoreListView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by gc on 2017-3-22.
 */
public class UsableDiscountActivity extends MyBaseActivity{

    private ArrayList<DiscountEntity> discountEntities = new ArrayList<DiscountEntity>();

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.lv_discount)
    ScrollToFooterLoadMoreListView mDiscountLv;
    @InjectView(R.id.nonuse_btn)
    RelativeLayout nounseBtn;

    UsableDiscountListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usable_discount);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(UsableDiscountActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(UsableDiscountActivity.this,true);
        discountEntities = (ArrayList<DiscountEntity>) getIntent().getSerializableExtra(Constants.KEY_USABLE_DISCOUNT);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    void initView(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.usable_discount));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adapter = new UsableDiscountListAdapter(UsableDiscountActivity.this);
        adapter.setDiscountEntitys(discountEntities);
        mDiscountLv.setAdapter(adapter);
        nounseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Constants.KEY_DISCOUNT_ENTITY, new DiscountEntity());
                setResult(Constants.REQUEST_PAY, intent);
                finish();
            }
        });
        mDiscountLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(Constants.KEY_DISCOUNT_ENTITY, discountEntities.get(position));
                setResult(Constants.REQUEST_PAY, intent);
                finish();
            }
        });
        loadMore(false);

    }

    private void loadMore(boolean toLoad){
        if(toLoad){
            mDiscountLv.setOnScrollToRefreshListener(new ScrollToFooterLoadMoreListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                }
            });
        }else {
            mDiscountLv.setOnScrollToRefreshListener(new ScrollToFooterLoadMoreListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                    mDiscountLv.refreshComplete();
                }
            });
        }
    }

}
