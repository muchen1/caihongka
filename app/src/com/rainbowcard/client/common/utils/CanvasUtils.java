package com.rainbowcard.client.common.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by gc on 14-9-2.
 */
public class CanvasUtils {
    public static Bitmap masking(Bitmap bitmap, int color) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);

        return bitmap;
    }
}
