package com.rainbowcard.client.common.utils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.List;

import android.util.Base64;
import android.util.Log;

/**
 * Created by gc on 14-7-22.
 */
public class StringUtil {

	public static String BASE64ToUtf8(String text) {
		byte[] data = Base64.decode(text, Base64.DEFAULT);
		try {
			return new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			DLog.e(Log.getStackTraceString(e));
			return text;
		}
	}

	public static String ISO88591ToUtf8(String text) {
		try {
			return new String(text.getBytes("8859_1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			DLog.e(Log.getStackTraceString(e));
			return text;
		}
	}

	public static String unicodeToUtf8(String theString) {
		if (theString == null || theString.length() == 0) {
			return "";
		}
		char aChar;
		int len = theString.length();
		StringBuilder outBuffer = new StringBuilder(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException(
									"Malformed   \\uxxxx   encoding.");
						}
					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}

	public static String unixTimeToString(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return format.format(time);
	}

	public static String unixDateToString(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(time);
	}

	public static String unixDateToStringWithChineseCharacters(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
		return format.format(time);
	}

	public static String formatDistanceString(int distInMeters) {
		// 不够1km 显示为m, 并保留小数点后两位
		if (distInMeters < 1000) {
			return String.format("%d米", distInMeters);
		} else {
			double dist = distInMeters / 1000.00;
			if(dist > 100){
				return ">100千米";
			}
			return String.format("%.2f千米", dist);
		}
	}

    public static String join(List<String> list, String c) {
        if (list == null || list.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        int i = 0;
        int len = list.size() - 1;
        for (String str : list) {
            builder.append(str);
            i++;
            if (i < len) {
                builder.append(c);
            }
        }

        return builder.toString();
    }
}
