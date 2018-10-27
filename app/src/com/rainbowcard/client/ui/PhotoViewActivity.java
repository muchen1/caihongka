package com.rainbowcard.client.ui;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rainbowcard.client.R;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.widget.FixedViewPager;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.UnderlinePageIndicator;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by kleist on 14-9-11.
 */
public class PhotoViewActivity extends MyBaseActivity {

    @InjectView(R.id.viewpager)
    FixedViewPager mViewpager;
    @InjectView(R.id.indicator)
    UnderlinePageIndicator mIndicator;

    private String[] mPicUrls;
    private int mPos;
    private PhotoViewPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_photoview);
        ButterKnife.inject(this);

        mPicUrls = getIntent().getStringArrayExtra(Constants.KEY_PIC_URLS);
        mPos = getIntent().getIntExtra(Constants.KEY_PIC_POS, 0);
        if (mPicUrls == null) {
            finish();
        }

        initView();
    }

    private void initView() {
        mAdapter = new PhotoViewPagerAdapter();
        mViewpager.setAdapter(mAdapter);
        mViewpager.setCurrentItem(mPos);
        mIndicator.setViewPager(mViewpager, mPos);
    }

    private class PhotoViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mPicUrls.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(PhotoViewActivity.this);
            if (!TextUtils.isEmpty(mPicUrls[position])) {
                Picasso.with(PhotoViewActivity.this)
//                        .load(String.format(getString(R.string.img_url),mPicUrls[position]))
                        .load(mPicUrls[position])
                        .into(photoView);
            }else {
                photoView.setImageResource(R.drawable.detail_default);
            }
            container.addView(photoView);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
