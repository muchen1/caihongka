package com.rainbowcard.client.widget;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rainbowcard.client.R;
import com.rainbowcard.client.base.Constants;


public class BottomControlPanel extends LinearLayout implements View.OnClickListener {
	private Context mContext;
//	private ImageText mShouyeBtn = null;
	private ImageText mBranchBtn = null;
	private ImageText mSetBtn = null;
	private ImageText mGerenBtn = null;
	private ImageView mOlympicBtn = null;
//	private int DEFALUT_BACKGROUND_COLOR = Color.rgb(243, 243, 243); //Color.rgb(192, 192, 192)
	private int DEFALUT_BACKGROUND_COLOR = Color.rgb(255, 255, 255); //Color.rgb(192, 192, 192)
	private BottomPanelCallback mBottomCallback = null;
	private List<View> viewList = new ArrayList<View>();

	public interface BottomPanelCallback{
		public void onBottomPanelClick(int itemId);
	}
	public BottomControlPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
//		mShouyeBtn = (ImageText)findViewById(R.id.btn_shouye);
		mBranchBtn = (ImageText)findViewById(R.id.btn_map);
		mSetBtn = (ImageText)findViewById(R.id.btn_set);
		mGerenBtn = (ImageText)findViewById(R.id.btn_geren);
		mOlympicBtn = (ImageView)findViewById(R.id.btn_olympic);
		setBackgroundColor(DEFALUT_BACKGROUND_COLOR);
//		viewList.add(mShouyeBtn);
		viewList.add(mBranchBtn);
		viewList.add(mGerenBtn);
		viewList.add(mSetBtn);
//		viewList.add(mOlympicBtn);

	}
	public void initBottomPanel(){
//		if(mShouyeBtn != null){
//			mShouyeBtn.setImage(R.drawable.message_unselected);
//			mShouyeBtn.setText("首页");
//		}
		if(mBranchBtn != null){
			mBranchBtn.setImage(R.drawable.tab_home);
			mBranchBtn.setText("首页");
		}
		if(mGerenBtn != null){
			mGerenBtn.setImage(R.drawable.tab_rainbowcard);
			mGerenBtn.setText("彩虹卡");
		}
		if(mSetBtn != null){
			mSetBtn.setImage(R.drawable.tab_user);
			mSetBtn.setText("我的");
		}
		if(mOlympicBtn != null){
			mOlympicBtn.setImageResource(R.drawable.olympic_icon);
			mOlympicBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mBottomCallback != null){
						mBottomCallback.onBottomPanelClick(16);
					}
				}
			});
		}
		setBtnListener();

	}
	private void setBtnListener(){
		int num = this.getChildCount();
		for(int i = 0; i < num; i++){
			View v = getChildAt(i);
			if(v != null){
				v.setOnClickListener(this);
			}
		}
	}
	public void setBottomCallback(BottomPanelCallback bottomCallback){
		mBottomCallback = bottomCallback;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		initBottomPanel();
		int index = -1;
		switch(v.getId()){
		case R.id.btn_map:
			index = Constants.BTN_FLAG_MAP;
			mBranchBtn.setChecked(Constants.BTN_FLAG_MAP);
			break;
		case R.id.btn_geren:
			index = Constants.BTN_FLAG_GEREN;
			mGerenBtn.setChecked(Constants.BTN_FLAG_GEREN);
			break;
		case R.id.btn_set:
			index = Constants.BTN_FLAG_SET;
			mSetBtn.setChecked(Constants.BTN_FLAG_SET);
			break;
		case R.id.btn_olympic:
			index = Constants.BTN_FLAG_OLYMPIC;
			break;
		default:break;
		}
		if(mBottomCallback != null){
			mBottomCallback.onBottomPanelClick(index);
		}
	}
	public void defaultBtnChecked(){
		if(mBranchBtn != null){
			mBranchBtn.setChecked(Constants.BTN_FLAG_MAP);
		}
	}
	public void setDefaultBtnChecked(int position){
		initBottomPanel();
		switch (position){
			case 1:
				mGerenBtn.setChecked(Constants.BTN_FLAG_GEREN);
				if(mBottomCallback != null){
					mBottomCallback.onBottomPanelClick(Constants.BTN_FLAG_GEREN);
				}
				break;
			case 2:
				mSetBtn.setChecked(Constants.BTN_FLAG_SET);
				if(mBottomCallback != null){
					mBottomCallback.onBottomPanelClick(Constants.BTN_FLAG_SET);
				}
				break;
		}
	}
	/*@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		layoutItems(left, top, right, bottom);
	}
	*//**最左边和最右边的view由母布局的padding进行控制位置。这里需对第2、3个view的位置重新设置
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 *//*
	private void layoutItems(int left, int top, int right, int bottom){
		int n = getChildCount();
		if(n == 0){
			return;
		}
		int paddingLeft = getPaddingLeft();
		int paddingRight = getPaddingRight();
		int width = right - left;
		int height = bottom - top;
		int allViewWidth = 0;
		for(int i = 0; i< n; i++){
			View v = getChildAt(i);
			allViewWidth += v.getWidth();
		}
		int blankWidth = (width - allViewWidth - paddingLeft - paddingRight) / (n - 1);

		LayoutParams params1 = (LayoutParams) viewList.get(1).getLayoutParams();
		params1.leftMargin = blankWidth;
		viewList.get(1).setLayoutParams(params1);

		LayoutParams params2 = (LayoutParams) viewList.get(2).getLayoutParams();
		params2.leftMargin = blankWidth;
		viewList.get(2).setLayoutParams(params2);

		LayoutParams params3 = (LayoutParams) viewList.get(3).getLayoutParams();
		params3.leftMargin = blankWidth;
		viewList.get(3).setLayoutParams(params3);
	}*/



}
