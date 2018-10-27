package com.rainbowcard.client.widget;


import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.base.Constants;

public class HeadControlPanel extends RelativeLayout {

	private Context mContext;
	public TextView mMidleTitle;
	private TextView mRightTitle;
	private ImageView logoIv;
	private static final float middle_title_size = 20f; 
	private static final float right_title_size = 17f; 
	private static final int default_background_color = Color.rgb(83, 188, 234);
	
	public HeadControlPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		mMidleTitle = (TextView)findViewById(R.id.midle_title);
		mRightTitle = (TextView)findViewById(R.id.right_title);
		logoIv = (ImageView) findViewById(R.id.logo);
		setBackgroundColor(default_background_color);
	}
	public void initHeadPanel(){
		
		if(mMidleTitle != null){
			setMiddleTitle(Constants.FRAGMENT_FLAG_MAP);
		}
	}
	public void setMiddleTitle(String s){
		if("首页".equals(s)){
			logoIv.setVisibility(VISIBLE);
			mMidleTitle.setVisibility(GONE);
		}else {
			logoIv.setVisibility(GONE);
			mMidleTitle.setVisibility(VISIBLE);
			mMidleTitle.setText(s);
			mMidleTitle.setTextSize(middle_title_size);
		}
//		mMidleTitle.setTypeface(Typeface.MONOSPACE,Typeface.BOLD_ITALIC);
	}

	public void setMyBackgroundColor(int color){
		setBackgroundColor(color);
	}
	public void setMyBackgroundResource(int resid){
		setBackgroundResource(resid);
	}


}
