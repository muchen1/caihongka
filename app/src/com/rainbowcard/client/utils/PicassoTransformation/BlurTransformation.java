package com.rainbowcard.client.utils.PicassoTransformation;

import android.content.Context;
import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;
import com.rainbowcard.client.R;
import com.rainbowcard.client.common.utils.CanvasUtils;
import com.rainbowcard.client.common.utils.FastBlur;

/**
 * Created by kleist on 14-9-11.
 */
public class BlurTransformation implements Transformation {

    private String mKey;
    private Context mContext;

    public BlurTransformation(Context context, String key) {
        mKey = key;
        mContext = context;
    }

    @Override
    public Bitmap transform(Bitmap source) {

        Bitmap bitmap = Bitmap.createScaledBitmap(source, source.getWidth() / 2, source.getHeight() / 2, false);
        bitmap = FastBlur.doBlur(bitmap, 20, true);
        bitmap = CanvasUtils.masking(bitmap, mContext.getResources().getColor(R.color.black_masking));

        if (bitmap != source) {
            source.recycle();
        }
        return bitmap;
    }

    @Override
    public String key() {
        return mKey;
    }
}
