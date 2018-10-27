package com.rainbowcard.client.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.rainbowcard.client.R;
import com.rainbowcard.client.base.Config;
import com.rainbowcard.client.common.utils.DLog;


/**
 * 关于时间的获取和操作接口。
 * 
 * 该类包含了一些常用的工具方法： 1. app内部使用unix timestamp作为时间的标示方式，因为某些情况下时间与时区相关，因此获取当前时间
 * 应当使用timeSince1970()或者daySince1970()方法。 2. 显示时间使用format()方法。
 */
@SuppressLint("SimpleDateFormat")
public class TimeUtil {
	public static long CHECKIN_DATE;
    public static int DAY_TIME = 1000 * 60 * 60 * 24; //一天的毫秒数
	public static final long SECOND = 1000;
	public static final long MINUTE = 60 * SECOND;
	public static final long HOUR = 60 * MINUTE;
	public static final long DAY = 24 * HOUR;
	public static final long WEEK = DAY * 7;
	public static final long MONTH = DAY * 30;
	public static final long HALF_MONTH = MONTH / 2;

	public static final String DATE_FORMATE_ALL = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMATE_MOUTH_DAY_HOUR_MINUTE = "MM/dd HH:mm";
	public static final String DATE_FORMATE_YEAR_MOUTH_DAY = "yyyy-MM-dd";
	public static final String DATE_FORMATE_TRANSACTION = "dd/MM/yyyy, hh:mm";
	public static final String DATE_FORMATE_DAY_HOUR_MINUTE = "MM/dd HH:mm";
	public static final String DATE_FORMATE_HOUR_MINUTE = "HH:mm";
	public static final String DATE_FORMATE_HOUR_MINUTE_SECOND = "HH:mm:ss";

	public static final String BEIJING_TIMEZONE = "GMT+8:00";

	public static final String DATE_DEFAULT_FORMATE = DATE_FORMATE_YEAR_MOUTH_DAY;

	public static SimpleDateFormat dateFormat = new SimpleDateFormat();

	public static Calendar newCalendar() {
		return Calendar.getInstance(TimeZone.getTimeZone(BEIJING_TIMEZONE));
	}

	public static long timeSince1970() {
		Calendar calendar = Calendar.getInstance(TimeZone
				.getTimeZone(BEIJING_TIMEZONE));
		return calendar.getTimeInMillis();
	}

	public static long daySince1970() {
		Calendar calendar = Calendar.getInstance(TimeZone
				.getTimeZone(BEIJING_TIMEZONE));
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	public static long getTime(String s) {
		return getTime(s, DATE_DEFAULT_FORMATE);
	}

	public static long getTime(String s, String pattern) {

		if (TextUtils.isEmpty(pattern)) {
			pattern = DATE_DEFAULT_FORMATE;
		}

		dateFormat.applyPattern(pattern);
		dateFormat.setTimeZone(TimeZone.getTimeZone(BEIJING_TIMEZONE));

		Date date = new Date();
		try {
			date = dateFormat.parse(s);
		} catch (ParseException e) {
			DLog.e(Log.getStackTraceString(e));
		}
		return date.getTime();

	}

	public static int daysBetween(long fromTime, long toTime) {
		return (int) ((toTime - fromTime) / DAY);
	}

	public static int[] WEEK_NAME = new int[] { R.string.date_sunday,
			R.string.date_monday, R.string.date_tuesday,
			R.string.date_wednesday, R.string.date_thursday,
			R.string.date_friday, R.string.date_saturday, };

	public static int getWeekTimeResId(long time) {
		Calendar calendar = Calendar.getInstance(TimeZone
				.getTimeZone(BEIJING_TIMEZONE));
		calendar.setTimeInMillis(time);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		if (dayOfWeek < 0) {
			dayOfWeek = 0;
		}
		return WEEK_NAME[dayOfWeek];
	}

    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }
    public static int getWeek(String sdate) {
        // 再转换为时间
        Date date = strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int hour=c.get(Calendar.DAY_OF_WEEK) -1;
        // hour中存的就是星期几了，其范围 1~7
        // 1=星期日 7=星期六，其他类推
        return WEEK_NAME[hour];
    }

	public static String[] WEEKS = new String[] { "周日","周一","周二","周三","周四","周五","周六"};
	/**
	 * 判断当前日期是星期几
	 * @param  pTime     设置的需要判断的时间  //格式如2012-09-08
	 * @return dayForWeek 判断结果
	 * @Exception 发生异常
	 */
	//传递参数 ”2016-04-29“
	public static String getMyWeek(String pTime) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		try {

			c.setTime(format.parse(pTime));

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int hour=c.get(Calendar.DAY_OF_WEEK) -1;
		return WEEKS[hour];
	}

    public static String format(long milliseconds) {
		return format(milliseconds, DATE_DEFAULT_FORMATE);
	}

	public static String format(long milliseconds, String pattern) {

		if (TextUtils.isEmpty(pattern)) {
			pattern = DATE_DEFAULT_FORMATE;
		}

		dateFormat.applyPattern(pattern);
		dateFormat.setTimeZone(TimeZone.getTimeZone(BEIJING_TIMEZONE));

		Date d = new Date();
		d.setTime(milliseconds);
		return dateFormat.format(d);
	}

	public static String formatForShow(long milliseconds, String pattern) {

		if (milliseconds >= Config.TS_MAX) {
			return "永久有效";
		}
		if (TextUtils.isEmpty(pattern)) {
			pattern = DATE_DEFAULT_FORMATE;
		}

		dateFormat.applyPattern(pattern);
		dateFormat.setTimeZone(TimeZone.getTimeZone(BEIJING_TIMEZONE));

		Date d = new Date();
		d.setTime(milliseconds - 1);
		return dateFormat.format(d);
	}

	public static String addMonth(String date, String pattern, int add) {
		Date dd = null;
		dateFormat.applyPattern(pattern);
		try {
			dd = dateFormat.parse(date);
		} catch (ParseException e) {
			DLog.e(Log.getStackTraceString(e));
		}
		Calendar temp = Calendar.getInstance();
		temp.setTime(dd);
		temp.add(Calendar.MONTH, add);

		return dateFormat.format(temp.getTimeInMillis());
	}

	public static String myDateFormat(String date) {
		String y = date.substring(0, 4);
		String m = date.substring(4, 6);
		String d = date.substring(6, 8);
		StringBuilder sb = new StringBuilder();
		return sb.append(y).append("-").append(m).append("-").append(d)
				.toString();

	}
    public static String getMyTime(String time){
        String hour = time.substring(0,2);
        String minute = time.substring(2,4);
		StringBuilder sb = new StringBuilder();
        return sb.append(hour).append("：").append(minute).toString();
    }

	public static String getDate(Calendar c, String separator) {
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		StringBuilder sb = new StringBuilder();
		sb.append(c.get(Calendar.YEAR));
		sb.append(separator);
		if (month < 10) {
			sb.append(0);// 添0补位
		}
		sb.append(month);// 月份从0开始
		sb.append(separator);
		if (day < 10) {
			sb.append(0);// 添0补位
		}
		sb.append(day);
		return sb.toString();
	}
    public static String formatBeginDate(String dateStr) {
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
			DLog.e(Log.getStackTraceString(e));
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
        String newDate = sdf.format(date);
        return newDate;
    }
    public static String formatDate(String dateStr) {
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
			DLog.e(Log.getStackTraceString(e));
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
        String newDate = sdf.format(date);
        return newDate;
    }
	public static String formatDateConversion(String dateStr) {
		DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
		Date date = null;
		try {
			date = simpleDateFormat.parse(dateStr);
		} catch (ParseException e) {
			DLog.e(Log.getStackTraceString(e));
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
		String newDate = sdf.format(date);
		return newDate;
	}
    public static String formatBeginTime(String dateStr) {
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
			DLog.e(Log.getStackTraceString(e));
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String newDate = sdf.format(date);
        return newDate;
    }
	public static String getMonthAndDay(Calendar c) {
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		StringBuilder sb = new StringBuilder();
		sb.append(c.get(Calendar.YEAR));
		sb.append("年");
		sb.append(month);// 月份从0开始
		sb.append("月");
		sb.append(day);
        sb.append("日");
		return sb.toString();
	}

    public static boolean compare_date(String DATE1, String DATE2) {


        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException exception) {
			DLog.e(Log.getStackTraceString(exception));
        }
        return true;
    }

	public static boolean countDown(String date){
		if(getTime(date,"yyyy-MM-dd HH:mm:ss") - System.currentTimeMillis() > DAY_TIME){
			return false;
		}
		return true;
	}

	public static boolean noStart(String date){
		if(getTime(date,"yyyy-MM-dd HH:mm:ss") > System.currentTimeMillis()){
			return true;
		}
		return false;
	}

	public static long countDownTime(String date){
		return getTime(date,"yyyy-MM-dd HH:mm:ss") - System.currentTimeMillis();
	}

}
