package com.rainbowcard.client.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;

import com.edmodo.cropper.CropImageView;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.common.utils.DLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by kleist on 13-11-1.
 */
public class CropperFragment extends Fragment {

    public final static String TAG = CropperFragment.class.getName();


    private CropImageView mCropImageView;
    private Button mBtnCropper;
    private Uri mImageUri;

    private int mRequestWidth = 480;
    private int mRequestHeight = 320;
    private int mType;

    private int mShowedImgWidth = 0;
    private int mShowedImgHeight = 0;
    private int mImgWidth = 0;
    private int mImgHeight = 0;
    private int mSampleSize = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        mImageUri = Uri.parse(intent.getStringExtra(Constants.KEY_IMAGE_URI));
        mRequestHeight =  intent.getIntExtra(Constants.KEY_IMAGE_HEIGHT, 0);
        mRequestWidth = intent.getIntExtra(Constants.KEY_IMAGE_WIDTH, 0);
        mType = intent.getIntExtra("type",0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_cropper, container, false);
        mCropImageView = (CropImageView) view.findViewById(R.id.civ_photo);
        mBtnCropper = (Button) view.findViewById(R.id.btn_cropper);
        mCropImageView.setAspectRatio(mRequestWidth, mRequestHeight);
        mCropImageView.setFixedAspectRatio(true);

        mCropImageView.setImageResource(R.drawable.detail_bg);
        setImage();
        mBtnCropper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sentBackBitmap();
            }
        });

        return view;
    }

    private void setImage() {

        mCropImageView.post(new Runnable() {
            @Override
            public void run() {
                final int width = mCropImageView.getWidth();
                final int height = mCropImageView.getHeight();
                DLog.i("width = " + width + " height=" + height);

                new AsyncTask<Void, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Void... voids) {
                        if(mType == 0) {
                            return getBitmapFromUriWithLowMemory(mImageUri, width, height);
                        }else if(mType == 1) {
                            try {
                                return BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(mImageUri));
                            }
                            catch (FileNotFoundException e)
                            {
                                DLog.e(Log.getStackTraceString(e));
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);
                        setImageView(bitmap);
                    }
                }.execute();

            }
        });
    }

    private Bitmap getBitmapFromUriWithLowMemory(Uri imageUri, int width, int height) {
        InputStream in;
        try {
            in = getActivity().getContentResolver().openInputStream(imageUri);
        } catch (FileNotFoundException e) {
            DLog.e(Log.getStackTraceString(e));
            return loadBitmapFromUriDirect(imageUri);
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        mImgWidth = options.outWidth;
        mImgHeight = options.outHeight;
        DLog.i("size h=" + options.outHeight + " w=" + options.outWidth);

        int inSampleSize = calculateInSampleSize(options, width, height);
        mSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;

        try {
            in.close();
        } catch (IOException e) {
            DLog.e(Log.getStackTraceString(e));
        }

        try {
            in = getActivity().getContentResolver().openInputStream(imageUri);
        } catch (FileNotFoundException e) {
            DLog.e(Log.getStackTraceString(e));
            return loadBitmapFromUriDirect(imageUri);
        }
        Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
        DLog.i("change size to h=" + bitmap.getHeight() + " w=" + bitmap.getWidth());
        return bitmap;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int width, int height) {
        DLog.i("calculateInSampleSize");
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        return calculateInSampleSize(imageWidth, imageHeight, width, height);
    }
    private int calculateInSampleSize(int imageWidth, int imageHeight, int width, int height) {
        DLog.i("calculateInSampleSize");

        if (imageHeight < height && imageWidth < width) {
            return 1;
        }

        int scalaHeight = Math.round((float)imageHeight / (float)height);
        int scalaWidth = Math.round((float)imageWidth / (float)width);
        return scalaHeight > scalaWidth ? scalaHeight : scalaWidth;
    }

    private Bitmap loadBitmapFromUriDirect(Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
        } catch (IOException e) {
            DLog.e(Log.getStackTraceString(e));
            return null;
        }
    }

    private void setImageViewWhenPreDraw(final Uri imageUri) {
        mCropImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver vto = mCropImageView.getViewTreeObserver();
                if (!vto.isAlive()) {
                    return;
                }

                int width = mCropImageView.getMeasuredWidth();
                int height = mCropImageView.getMeasuredHeight();

                if (width <= 0 || height <= 0) {
                    return;
                }

                vto.removeGlobalOnLayoutListener(this);
                setImageView(getBitmapFromUriWithLowMemory(imageUri, width, height));
            }
        });

//        mCropImageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                ViewTreeObserver vto = mCropImageView.getViewTreeObserver();
//                if (!vto.isAlive()) {
//                    return true;
//                }
//
//                int width = mCropImageView.getMeasuredWidth();
//                int height = mCropImageView.getMeasuredHeight();
//
//                if (width <= 0 || height <= 0) {
//                    return true;
//                }
//
//                vto.removeOnPreDrawListener(this);
//                setImageView(getBitmapFromUriWithLowMemory(imageUri, width, height));
//                return true;
//            }
//        });
    }

    private void setImageView(Bitmap bitmap) {
        mShowedImgHeight = bitmap.getHeight();
        mShowedImgWidth = bitmap.getWidth();
        ExifInterface exifInterface;
        try {
            if(mType == 0) {
                exifInterface = new ExifInterface(getFilePath(mImageUri));
                mCropImageView.setImageBitmap(bitmap, exifInterface);
            }else {
                mCropImageView.setImageBitmap(bitmap);
            }
        } catch (IOException e) {
            DLog.e(Log.getStackTraceString(e));
            mCropImageView.setImageBitmap(bitmap);
        }

    }


    private String getFilePath(Uri uri) {
        Cursor cursor = getActivity().getContentResolver()
                .query(uri, new String[] {MediaStore.Images.Media.DATA}, null, null, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }

    private void sentBackBitmap() {
        Bitmap bitmap = getResizedCropperImage();
        DLog.i("result h=" + bitmap.getWidth() + " w=" + bitmap.getHeight());
        Intent intent = new Intent();
        intent.putExtra(Constants.KEY_IMAGE_PATH, saveBitmap(bitmap));
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    private Bitmap getResizedCropperImage() {
        // TODO 根据设置的大小尽量缩放裁剪原图
//        RectF rectF = mCropImageView.getActualCropRect();
//        DLog.i("crop top=" + rectF.top + " bottom=" + rectF.bottom + " left=" + rectF.left + " right=" + rectF.right);
//        Rect rect = new Rect();
//        rect.set((int) rectF.left * mSampleSize, (int) rectF.top * mSampleSize,
//                (int) rectF.right * mSampleSize, (int) (rectF.bottom * mSampleSize));
//
//        DLog.i("big to top=" + rect.top + " bottom=" + rect.bottom + " left=" + rect.left + " right=" + rect.right);
//
//        if (rect.width() < mRequestWidth || rect.height() < mRequestHeight) {
//            return loadBitmapFromUriDirect(mImageUri);
//        }
//
//        InputStream in;
//        try {
//            in = getActivity().getContentResolver().openInputStream(mImageUri);
//        } catch (FileNotFoundException e) {
//            return loadBitmapFromUriDirect(mImageUri);
//        }
//        BitmapFactory.Options options = new BitmapFactory.Options();
////        options.outHeight = mRequestHeight;
////        options.outWidth = mRequestWidth;
//
//
//        return BitmapFactory.decodeStream(in, rect, options);
        return mCropImageView.getCroppedImage();
    }

    private String saveBitmap(Bitmap bitmap) {
        File file = new File(Environment.getExternalStorageDirectory(), "tmp_avatar.jpg");
        FileOutputStream fileOutputStream = null;
        try {
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, fileOutputStream);
        } catch (IOException e) {
            DLog.e(Log.getStackTraceString(e));
        } finally {
            try {
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                DLog.e(Log.getStackTraceString(e));
            }
        }

        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        DLog.i(file.getAbsolutePath()+"%%%%%%%%%%%%%%%%%%%");
        return file.getAbsolutePath();
    }

    private Bitmap resizeBitmap(Bitmap bitmap) {
        Bitmap resizeBitmap = resizeBitmap(bitmap, mRequestWidth, mRequestHeight);

        if (resizeBitmap != bitmap && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return  resizeBitmap;
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int width, int height) {
        float bitmapWidth = bitmap.getWidth();
        float bitmapHeight = bitmap.getHeight();

        if (bitmapHeight > height && bitmapWidth > width) {
            float scaleHeight = height / bitmapHeight;
            float scaleWidth = width / bitmapWidth;
            float scale = scaleHeight < scaleWidth ? scaleHeight : scaleWidth;

            return Bitmap.createScaledBitmap(bitmap, (int) (bitmapWidth * scale), (int) (bitmapHeight * scale), true);
        } else {
            return bitmap;
        }
    }

}
