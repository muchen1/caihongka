package com.rainbowcard.client.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by gc on 14-8-10.
 */
public class MyConfig {
	/**
	 * 
	 * @param mContext
	 *            上下文，来区别哪一个activity调用的
	 * @param whichSp
	 *            使用的SharedPreferences的名字
	 * @param field
	 *            SharedPreferences的哪一个字段
	 * @return
	 */
	// 取出whichSp中field字段对应的string类型的值
	public static String getSharePreStr(Context mContext, String whichSp,
			String field) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		String s = sp.getString(field, "");// 如果该字段没对应值，则取出字符串0
		return s;
	}

	// 取出whichSp中field字段对应的int类型的值
	public static int getSharePreInt(Context mContext, String whichSp,
			String field) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		int i = sp.getInt(field, 0);// 如果该字段没对应值，则取出0
		return i;
	}
	// 取出whichSp中field字段对应的int类型的值
	public static int getSharePreIntRb(Context mContext, String whichSp,
			String field) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		int i = sp.getInt(field, -1);// 如果该字段没对应值，则取出0
		return i;
	}
	public static long getSharePreLong(Context mContext, String whichSp,
			String field) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		long i = sp.getLong(field, 0);// 如果该字段没对应值，则取出0
		return i;
	}
	// 取出whichSp中field字段对应的double类型的值
	public static double getSharePreDouble(Context mContext, String whichSp,
			String field) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		double i = (double) sp.getFloat(field, 0);// 如果该字段没对应值，则取出0
		return i;
	}

	// 取出whichSp中field字段对应的boolean类型的值
	public static Boolean getSharePreBoolean(Context mContext, String whichSp,
											 String field) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		boolean b = sp.getBoolean(field, false);// 如果该字段没对应值，则取出false
		return b;
	}

	// 保存string类型的value到whichSp中的field字段
	public static void putSharePre(Context mContext, String whichSp,
			String field, String value) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		sp.edit().putString(field, value).commit();
	}

	// 保存int类型的value到whichSp中的field字段
	public static void putSharePre(Context mContext, String whichSp,
			String field, int value) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		sp.edit().putInt(field, value).commit();
	}
	// 保存float类型的value到whichSp中的field字段
	public static void putSharePre(Context mContext, String whichSp,
			String field, float value) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		sp.edit().putFloat(field, value).commit();
	}
	public static void putSharePre(Context mContext,String whichSp,String field,long value){
		SharedPreferences sp = (SharedPreferences)mContext.getSharedPreferences(whichSp,0);
		sp.edit().putLong(field,value).commit();
	}


	// 保存boolean类型的value到whichSp中的field字段
	public static void putSharePre(Context mContext, String whichSp,
			String field, Boolean value) {
		SharedPreferences sp = mContext.getSharedPreferences(whichSp, 0);
		sp.edit().putBoolean(field, value).commit();
	}
}
