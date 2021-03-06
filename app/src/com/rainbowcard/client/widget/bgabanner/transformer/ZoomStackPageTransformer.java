package com.rainbowcard.client.widget.bgabanner.transformer;

import android.support.v4.view.ViewCompat;
import android.view.View;


/**
 * Created by gc on 2018/1/3
 */
public class ZoomStackPageTransformer extends BGAPageTransformer {

    @Override
    public void handleInvisiblePage(View view, float position) {
    }

    @Override
    public void handleLeftPage(View view, float position) {
        ViewCompat.setTranslationX(view, -view.getWidth() * position);

        ViewCompat.setPivotX(view, view.getWidth() * 0.5f);
        ViewCompat.setPivotY(view, view.getHeight() * 0.5f);
        ViewCompat.setScaleX(view, 1 + position);
        ViewCompat.setScaleY(view, 1 + position);

        if (position < -0.95f) {
            ViewCompat.setAlpha(view, 0);
        } else {
            ViewCompat.setAlpha(view, 1);
        }
    }

    @Override
    public void handleRightPage(View view, float position) {
        ViewCompat.setTranslationX(view, -view.getWidth() * position);

        ViewCompat.setPivotX(view, view.getWidth() * 0.5f);
        ViewCompat.setPivotY(view, view.getHeight() * 0.5f);
        ViewCompat.setScaleX(view, 1 + position);
        ViewCompat.setScaleY(view, 1 + position);

        if (position > 0.95f) {
            ViewCompat.setAlpha(view, 0);
        } else {
            ViewCompat.setAlpha(view, 1);
        }
    }

}