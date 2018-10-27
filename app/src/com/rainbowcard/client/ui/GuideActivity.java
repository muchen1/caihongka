package com.rainbowcard.client.ui;

import android.content.Intent;
import android.os.Bundle;

import com.rainbowcard.client.R;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.base.MyBasicActivity;
import com.rainbowcard.client.common.city.CityEntity;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.bgabanner.BGABanner;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class GuideActivity extends MyBasicActivity {
    @InjectView(R.id.banner_guide_background)
    BGABanner mBackgroundBanner;
    @InjectView(R.id.banner_guide_foreground)
    BGABanner mForegroundBanner;

    private ArrayList<CityEntity> supportCitys = new ArrayList<CityEntity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.inject(this);
        supportCitys = (ArrayList<CityEntity>) getIntent().getSerializableExtra(Constants.KEY_CITYS);
        UIUtils.setMiuiStatusBarDarkMode(GuideActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(GuideActivity.this,true);
        setListener();
        processLogic();
    }

    private void setListener() {
        /**
         * 设置进入按钮和跳过按钮控件资源 id 及其点击事件
         * 如果进入按钮和跳过按钮有一个不存在的话就传 0
         * 在 BGABanner 里已经帮开发者处理了防止重复点击事件
         * 在 BGABanner 里已经帮开发者处理了「跳过按钮」和「进入按钮」的显示与隐藏
         */
        mForegroundBanner.setEnterSkipViewIdAndDelegate(R.id.btn_guide_enter, R.id.tv_guide_skip, new BGABanner.GuideDelegate() {
            @Override
            public void onClickEnterOrSkip() {
                Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                intent.putExtra(Constants.KEY_CITYS,supportCitys);
                startActivity(intent);
                finish();
            }
        });
    }

    private void processLogic() {
        // 设置数据源
        mBackgroundBanner.setData(R.drawable.uoko_guide_background_1, R.drawable.uoko_guide_background_2, R.drawable.uoko_guide_background_3);

        mForegroundBanner.setData(R.color.transparent, R.color.transparent, R.color.transparent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 如果开发者的引导页主题是透明的，需要在界面可见时给背景 Banner 设置一个白色背景，避免滑动过程中两个 Banner 都设置透明度后能看到 Launcher
        mBackgroundBanner.setBackgroundResource(android.R.color.white);
    }
}