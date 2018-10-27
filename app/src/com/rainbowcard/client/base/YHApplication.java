package com.rainbowcard.client.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.support.multidex.MultiDex;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.rainbowcard.client.common.utils.DLog;
import com.rainbowcard.client.ui.LoginActivity;
import com.rainbowcard.client.ui.MainActivity;
import com.rainbowcard.client.ui.MessageActivity;
import com.rainbowcard.client.utils.LoginControl;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.SystemUtils;
import com.rainbowcard.client.widget.adlibrary.utils.DisplayUtil;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.*;
import com.umeng.socialize.Config;

import java.util.LinkedList;
import java.util.List;

public class YHApplication extends Application {
	public String mDeviceToken;
	private List<Activity> mList = new LinkedList<Activity>();
	private static YHApplication mInstance;
	private static RequestQueue mRequestQueue;

	public static YHApplication instance() {
		return mInstance;
	}

	public static RequestQueue getRequestQueue(Context context) {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(context);
		}
		return mRequestQueue;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		SDKInitializer.initialize(this);
		UMShareAPI.get(this);
		Config.DEBUG = true;
		Fresco.initialize(this);
		initDisplayOpinion();
		mInstance = this;
		PushAgent mPushAgent = PushAgent.getInstance(this);
		mPushAgent.setDisplayNotificationNumber(5);
		//注册推送服务，每次调用register方法都会回调该接口
		mPushAgent.register(new IUmengRegisterCallback() {

			@Override
			public void onSuccess(String deviceToken) {
				//注册成功会返回device token
				Log.d("GCCCCCCCC",deviceToken+"!!!!!!!!!");
				mDeviceToken = deviceToken;
			}

			@Override
			public void onFailure(String s, String s1) {

			}
		});
		UmengNotificationClickHandler umengNotificationClickHandler = new UmengNotificationClickHandler() {
			/**
			 * 友盟推送通知点击事件的操作 使用此方法会导致WebView加载url的同事调用系统浏览器加载url，
			 *
			 * @param context
			 * @param uMessage
			 */

//			public void launchApp(Context context, UMessage uMessage) {
//				super.launchApp(context,uMessage);
//				Log.d("GCCCCCCCCCCCC!!!!!!",uMessage.title);
//			}
//
//			public void openUrl(Context context, UMessage uMessage){
//				Log.d("GCCCCCCCCCCCCLLL@@@@@@@",uMessage.title);
//			}
//			public void openActivity(Context context, UMessage uMessage){
//				super.openActivity(context,uMessage);
//				Log.d("GCCCCCCCCCCCCLLL#######",uMessage.title);
//			}
			@Override
			public void dealWithCustomAction(Context context, UMessage uMessage) {

//				super.dealWithCustomAction(context, uMessage);
//				super.launchApp(context,uMessage);

				Intent intent;
				if (LoginControl.getInstance(context).isLogin()) {
					if(SystemUtils.isAppAlive(context, "com.rainbowcard.client")) {
						Intent mainIntent = new Intent(context, MainActivity.class);
						mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent = new Intent(getApplicationContext(), MessageActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra(Constants.MESSAGE_TYPE, Integer.valueOf(uMessage.extra.get("message_type")));
						Intent[] intents = {mainIntent, intent};
						context.startActivities(intents);
					}else {
						Intent launchIntent = context.getPackageManager().
								getLaunchIntentForPackage("com.rainbowcard.client");
						launchIntent.setFlags(
								Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
						Bundle args = new Bundle();
//								args.putString(Constants.KEY_URL, uMessage.extra.get("url"));
						args.putString(Constants.MESSAGE_TYPE, uMessage.extra.get("message_type"));
						launchIntent.putExtra(Constants.EXTRA_BUNDLE, args);
						context.startActivity(launchIntent);
					}
				} else {
					if(SystemUtils.isAppAlive(context, "com.rainbowcard.client")) {
						Intent mainIntent = new Intent(context, MainActivity.class);
						mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

						intent = new Intent(getApplicationContext(), LoginActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						Intent[] intents = {mainIntent, intent};
						context.startActivities(intents);
					}else {
						Intent launchIntent = context.getPackageManager().
								getLaunchIntentForPackage("com.rainbowcard.client");
						launchIntent.setFlags(
								Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
						Bundle args = new Bundle();
//								args.putString(Constants.KEY_URL, uMessage.extra.get("url"));
						args.putString(Constants.MESSAGE_TYPE, "100");
						launchIntent.putExtra(Constants.EXTRA_BUNDLE, args);
						context.startActivity(launchIntent);
					}
				}
				/*switch (Integer.valueOf(uMessage.extra.get("type"))){
					case 1:
						if(SystemUtils.isAppAlive(context, "com.rainbowcard.client")) {
							Log.d("GCCCCCCCC","jiushizheli,没退出");
							Intent mainIntent = new Intent(context, MainActivity.class);
							mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

							intent = new Intent(getApplicationContext(), SurprisedActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra(Constants.KEY_URL, uMessage.extra.get("url"));
//							startActivity(intent);

							Intent[] intents = {mainIntent, intent};

							context.startActivities(intents);
						}else {
							Log.d("GCCCCCCCC","jiushizheli,退出了，哇咔咔");
							Intent launchIntent = context.getPackageManager().
									getLaunchIntentForPackage("com.rainbowcard.client");
							launchIntent.setFlags(
									Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
							Bundle args = new Bundle();
							args.putString(Constants.KEY_URL, uMessage.extra.get("url"));
							args.putString(Constants.STATR_TYPE, uMessage.extra.get("type"));
							launchIntent.putExtra(Constants.EXTRA_BUNDLE, args);
							context.startActivity(launchIntent);
						}
						break;
					case 2:
						if (LoginControl.getInstance(context).isLogin()) {
							if(SystemUtils.isAppAlive(context, "com.rainbowcard.client")) {
								Intent mainIntent = new Intent(context, MainActivity.class);
								mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

								intent = new Intent(getApplicationContext(), MyDiscountActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								Intent[] intents = {mainIntent, intent};
								context.startActivities(intents);
							}else {
								Intent launchIntent = context.getPackageManager().
										getLaunchIntentForPackage("com.rainbowcard.client");
								launchIntent.setFlags(
										Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
								Bundle args = new Bundle();
//								args.putString(Constants.KEY_URL, uMessage.extra.get("url"));
								args.putString(Constants.STATR_TYPE, uMessage.extra.get("type"));
								launchIntent.putExtra(Constants.EXTRA_BUNDLE, args);
								context.startActivity(launchIntent);
							}
						} else {
							if(SystemUtils.isAppAlive(context, "com.rainbowcard.client")) {
								Intent mainIntent = new Intent(context, MainActivity.class);
								mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

								intent = new Intent(getApplicationContext(), LoginActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								Intent[] intents = {mainIntent, intent};
								context.startActivities(intents);
							}else {
								Intent launchIntent = context.getPackageManager().
										getLaunchIntentForPackage("com.rainbowcard.client");
								launchIntent.setFlags(
										Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
								Bundle args = new Bundle();
//								args.putString(Constants.KEY_URL, uMessage.extra.get("url"));
								args.putString(Constants.STATR_TYPE, "4");
								launchIntent.putExtra(Constants.EXTRA_BUNDLE, args);
								context.startActivity(launchIntent);
							}
						}
						break;
					case 3:
						if (LoginControl.getInstance(context).isLogin()) {
							if(SystemUtils.isAppAlive(context, "com.rainbowcard.client")) {
								Intent mainIntent = new Intent(context, MainActivity.class);
								mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

								intent = new Intent(getApplicationContext(), MessageDetailActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.putExtra(Constants.KEY_CONTENT, uMessage.extra.get("content"));
								Intent[] intents = {mainIntent, intent};
								context.startActivities(intents);
							}else {
								Intent launchIntent = context.getPackageManager().
										getLaunchIntentForPackage("com.rainbowcard.client");
								launchIntent.setFlags(
										Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
								Bundle args = new Bundle();
//								args.putString(Constants.KEY_URL, uMessage.extra.get("url"));
								args.putString(Constants.STATR_TYPE, uMessage.extra.get("type"));
								args.putString(Constants.KEY_CONTENT, uMessage.extra.get("content"));
								launchIntent.putExtra(Constants.EXTRA_BUNDLE, args);
								context.startActivity(launchIntent);
							}
						} else {
							if(SystemUtils.isAppAlive(context, "com.rainbowcard.client")) {
								Intent mainIntent = new Intent(context, MainActivity.class);
								mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

								intent = new Intent(getApplicationContext(), LoginActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								Intent[] intents = {mainIntent, intent};
								context.startActivities(intents);
							}else {
								Intent launchIntent = context.getPackageManager().
										getLaunchIntentForPackage("com.rainbowcard.client");
								launchIntent.setFlags(
										Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
								Bundle args = new Bundle();
//								args.putString(Constants.KEY_URL, uMessage.extra.get("url"));
								args.putString(Constants.STATR_TYPE, "4");
//								args.putString(Constants.KEY_CONTENT, uMessage.extra.get("content"));
								launchIntent.putExtra(Constants.EXTRA_BUNDLE, args);
								context.startActivity(launchIntent);
							}
						}
						break;
				}*/

//				Intent intent = new Intent(getApplicationContext(), LoadUriActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(intent);
			}
		};
		PushAgent.getInstance(getApplicationContext()).setNotificationClickHandler(umengNotificationClickHandler);
		mPushAgent.setPushIntentServiceClass(null);

	}

	public void addActivity(Activity activity) {
		mList.add(activity);
	}

	public void exit() {
		try {
			for (Activity activity : mList) {
				if (activity != null)
					activity.finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}


	private void initDisplayOpinion() {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		DisplayUtil.density = dm.density;
		DisplayUtil.densityDPI = dm.densityDpi;
		DisplayUtil.screenWidthPx = dm.widthPixels;
		DisplayUtil.screenhightPx = dm.heightPixels;
		DisplayUtil.screenWidthDip = DisplayUtil.px2dip(getApplicationContext(), dm.widthPixels);
		DisplayUtil.screenHightDip = DisplayUtil.px2dip(getApplicationContext(), dm.heightPixels);
	}


	public String getDeviceToken(){
		return mDeviceToken;
	}

	public String getUrl(){
		return MyConfig.getSharePreStr(mInstance, Constants.USERINFO,Constants.QINIUURL);
	}

	public String getNewsUrl(){
		return MyConfig.getSharePreStr(mInstance, Constants.USERINFO,Constants.NEWSURL);
	}

	private boolean isInLauncher() {
		ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(1);
		String activityName;
		for (int i = 0;i < runningTasks.size();i++) {
			activityName = runningTasks.get(i).baseActivity.getClassName();
			if(activityName.equals("com.betterwood.yh.homePage.activity.HomePageAct")) {
				return true;
			}
		}
		return false;
	}

    public static abstract interface EventHandler {
        public abstract void onCityComplite();

        public abstract void onNetChange();
    }

	public String getChannelName() {
		String channel = "";
		try {
			Bundle data = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData;
			Object _channel = data.get("UMENG_CHANNEL");
			channel = String.valueOf(_channel);
			DLog.i("channel =" + channel);
		} catch (PackageManager.NameNotFoundException e) {
			DLog.e(Log.getStackTraceString(e));
		}
		return channel;
	}

	public int getVersionCode(){
		PackageInfo mVersionInfo;
		int versionCode = 0;
		try {
			mVersionInfo = this.getPackageManager().getPackageInfo(this.getPackageName(),0);
			versionCode = mVersionInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}
	public String getVersionName(){
		PackageInfo mVersionInfo;
		String versionName = "1.0.0";
		try {
			mVersionInfo = this.getPackageManager().getPackageInfo(this.getPackageName(),0);
			versionName = mVersionInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

	//各个平台的配置
	{
		//微信
		PlatformConfig.setWeixin(Constants.APP_ID, Constants.APP_SECRET);
	}

}