package com.rainbowcard.client.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.model.CardStoreModel;
import com.rainbowcard.client.model.MarkerInfoUtil;
import com.rainbowcard.client.ui.adapter.MyTypeStatusAdapter;
import com.rainbowcard.client.ui.fragment.OffLineFragment;
import com.rainbowcard.client.ui.fragment.OnLineFragment;
import com.rainbowcard.client.utils.SimpleFragmentSwitcher;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.widget.HorizontalListView;
import com.rainbowcard.client.widget.LoadingFrameLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2016-10-25.
 */
public class GetRainbowCardActivity extends MyBaseActivity{

    @InjectView(R.id.fl_loading)
    LoadingFrameLayout mFlLoading;
    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.lv_type_status)
    HorizontalListView mLvTypeStatus;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;


    MyTypeStatusAdapter mMyTypeStatusAdapter;
    private FragmentManager mFagmentManager;
    private FragmentTransaction mFragmentTransaction;
    private SimpleFragmentSwitcher mFragmentSwitcher;
    private OnLineFragment mOnLineFragment;
    private OffLineFragment mOffLineFragment;

    public List<CardStoreModel.CardStoreEntity> infos = new ArrayList<CardStoreModel.CardStoreEntity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_rainbow);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(GetRainbowCardActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(GetRainbowCardActivity.this,true);
        mFagmentManager = getFragmentManager();
        mFragmentTransaction = mFagmentManager.beginTransaction();

        setTabSelection(0);
        init();
    }

    private void init(){
        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.get_rainbow));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mFlLoading.setRetryButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loadTeamInfo();
            }
        });
        mMyTypeStatusAdapter = new MyTypeStatusAdapter(this);
        mMyTypeStatusAdapter.setMPosition(0);
        mLvTypeStatus.setAdapter(mMyTypeStatusAdapter);
        mLvTypeStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMyTypeStatusAdapter.setMPosition(position);
                setTabSelection(position);
            }
        });

    }

    public void setShops(ArrayList<CardStoreModel.CardStoreEntity> list){
        infos.clear();
        infos.addAll(list);
    }

    private void setTabSelection(int index) {
        // 每次选中之前先清楚掉上次的选中状态
//        clearSelection();
        // 开启一个Fragment事务
        FragmentTransaction transaction = mFagmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
//        hideFragments(transaction);
        switch (index) {
            case 1:
                if (mOffLineFragment == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    mOffLineFragment = OffLineFragment.newInstance();
                }
                transaction.replace(R.id.fragment_content, mOffLineFragment);
                transaction.addToBackStack(null);
                break;
            case 0:
                if (mOnLineFragment == null) {
                    // 如果ContactsFragment为空，则创建一个并添加到界面上
                    mOnLineFragment = OnLineFragment.newInstance();
                }
                transaction.replace(R.id.fragment_content, mOnLineFragment);
                transaction.addToBackStack(null);
                break;
        }
        transaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (mOnLineFragment != null) {
            transaction.hide(mOnLineFragment);
        }
        if (mOffLineFragment != null) {
            transaction.hide(mOffLineFragment);
        }

    }

    private void changeFragment(int pos) {
        mFragmentSwitcher.showFragment(R.id.fragment_content, pos);
    }

    private void setFragments() {
        mFragmentSwitcher = new SimpleFragmentSwitcher(getFragmentManager()) {
            @Override
            public Fragment getItem(int pos) {
                switch (pos) {
                    case 0:
                        return OnLineFragment.newInstance();
                    case 1:
                        return OffLineFragment.newInstance();
                    default:
                        return null;
                }
            }
        };
        changeFragment(0);
    }

    public boolean isFinish = false;

    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if(isFinish){
                finish();
            }else {
                mOnLineFragment.view.setVisibility(View.GONE);
                mOnLineFragment.mRootScrollView.fullScroll(ScrollView.FOCUS_UP);
                isFinish = true;
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
                finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
