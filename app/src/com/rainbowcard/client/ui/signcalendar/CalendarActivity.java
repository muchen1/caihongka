package com.rainbowcard.client.ui.signcalendar;

import android.app.Dialog;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.SignEntity;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.TimeUtil;
import com.rainbowcard.client.widget.HeadControlPanel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class CalendarActivity extends MyBaseActivity {
	@InjectView(R.id.head_layout)
	HeadControlPanel mHeadControlPanel;
	@InjectView(R.id.nav_back)
	LinearLayout backBtn;
	@InjectView(R.id.popupwindow_calendar)
	SignCalendar calendar;
	@InjectView(R.id.popupwindow_calendar_month)
	TextView popupwindow_calendar_month;
	@InjectView(R.id.number)
	TextView number;
	@InjectView(R.id.sign_text)
	TextView signTv;
	@InjectView(R.id.btn_signIn)
	Button btn_signIn;

	int getPoints;


	private String token;
	private String uid;
	private String date = null;// 设置默认选中的日期  格式为 “2014-04-05” 标准DATE格式   
	private List<String> list = new ArrayList<String>(); //设置标记列表
	boolean isinput=false;
	private String date1 = null;//单天日期
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);
		ButterKnife.inject(this);
		//透明状态栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		//透明导航栏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

		token = "Bearer " + MyConfig.getSharePreStr(this, Constants.ACCRESSTOKEN, Constants.ACCESS_TOKEN);
		uid = MyConfig.getSharePreStr(this,Constants.USERINFO,Constants.UID);
		
        SimpleDateFormat    formatter    =   new    SimpleDateFormat    ("yyyy-MM-dd");
		Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间       
		date1 =formatter.format(curDate);
		initView();
	}

	void initView(){
		mHeadControlPanel.initHeadPanel();
		mHeadControlPanel.setMiddleTitle(getString(R.string.sign));
		backBtn.setVisibility(View.VISIBLE);
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		popupwindow_calendar_month.setText(calendar.getCalendarYear() + "年"
				+ calendar.getCalendarMonth() + "月");
		if (null != date) {
			int years = Integer.parseInt(date.substring(0,
					date.indexOf("-")));
			int month = Integer.parseInt(date.substring(
					date.indexOf("-") + 1, date.lastIndexOf("-")));
			popupwindow_calendar_month.setText(years + "年" + month + "月");

			calendar.showCalendar(years, month);
			calendar.setCalendarDayBgColor(date,
					R.drawable.calendar_date_focused);
		}
		query();

		//监听当前月份
		calendar.setOnCalendarDateChangedListener(new SignCalendar.OnCalendarDateChangedListener() {
			public void onCalendarDateChanged(int year, int month) {
				popupwindow_calendar_month
						.setText(year + "年" + month + "月");
			}
		});
	}

	void refreshView(SignEntity.SignInfo info){
		if(isinput){
			btn_signIn.setText("签到成功");
			btn_signIn.setBackgroundResource(R.drawable.button_gray);
			btn_signIn.setTextColor(getResources().getColor(R.color.sign_read));
			btn_signIn.setEnabled(false);
		}
		btn_signIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Date today= calendar.getThisday();
//				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
           /* calendar.removeAllMarks();
           list.add(df.format(today));
           calendar.addMarks(list, 0);*/
				//将当前日期标示出来
//           	add(df.format(today));
				//calendar.addMark(today, 0);
				sign();

//				calendar.setCalendarDayBgColor(today, R.drawable.bg_sign_today);
				btn_signIn.setText("签到成功");
				btn_signIn.setBackgroundResource(R.drawable.button_gray);
				btn_signIn.setTextColor(getResources().getColor(R.color.sign_read));
				btn_signIn.setEnabled(false);
			}
		});
		SpannableString styledText = new SpannableString(String.format(getString(R.string.sign_count),info.signNumbers));
		styledText.setSpan(new TextAppearanceSpan(this, R.style.style0), 4, 4 + info.signNumbers.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		number.setText(styledText, TextView.BufferType.SPANNABLE);
		if(info.continueSignDay == Constants.CONTINUESIGNDAY){
			signTv.setText(getString(R.string.sign_text1));
		}else {
			int leng = String.valueOf(info.continueSignDay).length();
			SpannableString styledText2 = new SpannableString(String.format(getString(R.string.sign_text2), info.continueSignDay));
			styledText2.setSpan(new TextAppearanceSpan(this, R.style.style1), 9,9 + leng, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			signTv.setText(styledText2, TextView.BufferType.SPANNABLE);
		}
	}

	private void showDialog() {
		final Dialog dialog = new Dialog(CalendarActivity.this,R.style.loading_dialog);
		dialog.setContentView(R.layout.ui_sign_success_dialog);
		Window dialogWindow = dialog.getWindow();
		Display display = getWindowManager().getDefaultDisplay();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.y = -80;
		lp.width = (int)(display.getWidth() * 0.95);
		dialogWindow.setAttributes(lp);
		TextView pointsTv = (TextView) dialog.findViewById(R.id.points);
		pointsTv.setText(String.format(getString(R.string.pay_points),getPoints));

		dialog.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				query();
				dialog.dismiss();
			}
		});
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	void sign(){
		withBtwVolley().load(API.API_USER_SIGH)
				.method(Request.Method.POST)
				.setHeader("Authorization", token)
				.setParam("uid", uid)
				.setRetrys(0)
				.setUIComponent(this)
				.setTimeout(10000)
				.setResponseHandler(new BtwVolley.ResponseHandler<String>() {
					@Override
					public void onStart() {
						getUIUtils().loading();
					}

					@Override
					public void onFinish() {
						getUIUtils().dismissLoading();
					}

					@Override
					public void onResponse(String resp) {
						showDialog();
					}

					@Override
					public void onBtwError(BtwRespError<String> error) {

					}

					@Override
					public void onNetworkError(VolleyUtils.NetworkError error) {

					}

					@Override
					public void onRefreToken() {

					}
				}).excute();
	}
	

	 public void query(){
		 withBtwVolley().load(API.API_GET_SIGN_DETAIL)
				 .method(Request.Method.GET)
				 .setHeader("Authorization", token)
				 .setParam("uid", uid)
				 .setRetrys(0)
				 .setUIComponent(this)
				 .setTimeout(10000)
				 .setResponseHandler(new BtwVolley.ResponseHandler<SignEntity>() {
					 @Override
					 public void onStart() {
						 getUIUtils().loading();
					 }

					 @Override
					 public void onFinish() {
						 getUIUtils().dismissLoading();

					 }

					 @Override
					 public void onResponse(SignEntity resp) {
						 getPoints = resp.signInfo.getPoint;
						 for (String person : resp.signInfo.signDays)
						 {
							 list.add(TimeUtil.myDateFormat(person));
							 Log.d("GCCCCCC","calendar"+date1+"???"+TimeUtil.myDateFormat(person));
							 if(date1.equals(TimeUtil.myDateFormat(person))){
								 isinput=true;
							 }
						 }
						 calendar.addMarks(list, 0);
						 refreshView(resp.signInfo);
					 }

					 @Override
					 public void onBtwError(BtwRespError<SignEntity> error) {
					 }

					 @Override
					 public void onNetworkError(VolleyUtils.NetworkError error) {
					 }

					 @Override
					 public void onRefreToken() {

					 }
				 }).excute(SignEntity.class);
	    }
	
	  @Override
	    protected void onDestroy(){
	        super.onDestroy();
	    }

}
