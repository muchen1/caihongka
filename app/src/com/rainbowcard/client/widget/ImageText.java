package com.rainbowcard.client.widget;


import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.utils.DensityUtil;


public class ImageText extends LinearLayout{
	private Context mContext = null;
	private ImageView mImageView = null;
	private TextView mTextView = null;
	private final static int DEFAULT_IMAGE_WIDTH = 44;
	private final static int DEFAULT_IMAGE_HEIGHT = 44;
	private int CHECKED_COLOR = Color.rgb(83, 188, 234); //选中蓝色
	private int UNCHECKED_COLOR = Color.GRAY;   //自然灰色
	public ImageText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	public ImageText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View parentView = inflater.inflate(R.layout.image_text_layout, this, true);
		mImageView = (ImageView)findViewById(R.id.image_iamge_text);
		mTextView = (TextView)findViewById(R.id.text_iamge_text);
	}
	public void setImage(int id){
		if(mImageView != null){
			mImageView.setImageResource(id);
			setImageSize(DensityUtil.dip2px(mContext,23), DensityUtil.dip2px(mContext,23));
		}
	}

	public void setText(String s){
		if(mTextView != null){
			mTextView.setText(s);
			mTextView.setTextColor(UNCHECKED_COLOR);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return true;
	}
	private void setImageSize(int w, int h){
		if(mImageView != null){
			ViewGroup.LayoutParams params = mImageView.getLayoutParams();
			params.width = w;
			params.height = h;
			mImageView.setLayoutParams(params);
		}
	}
	public void setChecked(int itemID){
		if(mTextView != null){
			mTextView.setTextColor(CHECKED_COLOR);
		}
		int checkDrawableId = -1;
		switch (itemID){
		case Constants.BTN_FLAG_MAP:
			checkDrawableId = R.drawable.tab_home_pro;
			break;
		case Constants.BTN_FLAG_SET:
			checkDrawableId = R.drawable.tab_user_pre;
			break;
//		case Constants.BTN_FLAG_YOUXI:
//			checkDrawableId = R.drawable.news_selected;
//			break;
		case Constants.BTN_FLAG_GEREN:
			checkDrawableId = R.drawable.tab_rainbowcard_click;
			break;
		default:break;
		}
		if(mImageView != null){
			mImageView.setImageResource(checkDrawableId);
		}
	}


	


}
