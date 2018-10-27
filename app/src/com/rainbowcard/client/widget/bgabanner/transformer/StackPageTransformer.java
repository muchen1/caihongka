package com.rainbowcard.client.widget.bgabanner.transformer;

import android.support.v4.view.ViewCompat;
import android.view.View;

/**
 * Created by gc on 2018/1/3
 */
public class StackPageTransformer extends BGAPageTransformer {

    @Override
    public void handleInvisiblePage(View view, float position) {
    }

    @Override
    public void handleLeftPage(View view, float position) {
    }

    @Override
    public void handleRightPage(View view, float position) {
        ViewCompat.setTranslationX(view, -view.getWidth() * position);
    }

}