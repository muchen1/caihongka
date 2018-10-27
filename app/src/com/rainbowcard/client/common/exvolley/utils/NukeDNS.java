package com.rainbowcard.client.common.exvolley.utils;

import android.os.Build;
import android.util.Log;

import com.rainbowcard.client.common.utils.DLog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;

/**
 * Created by gc on 15/7/14.
 */
public class NukeDNS {
    public static void nuke() {
        if (Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT) {
            nuck21();
        } else {
            nuckPre();
        }
    }

    public static void nuckPre() {
        try {
            Class inetAddressClass = InetAddress.class;
            Field field = inetAddressClass.getDeclaredField("addressCache");
            field.setAccessible(true);
            Object object = field.get(inetAddressClass);
            Class cacheClass = object.getClass();
            Method putMethod = cacheClass.getDeclaredMethod("put",
                    String.class, InetAddress[].class);
            putMethod.setAccessible(true);
            String str = "42.121.4.223";
            String[] ipStr = str.split("\\.");
            byte[] ipBuf = new byte[4];
            for (int i = 0; i < 4; i++) {
                ipBuf[i] = (byte) (Integer.parseInt(ipStr[i]) & 0xff);
            }
            putMethod.invoke(object, "m.betterwood.com",
                    new InetAddress[] { InetAddress.getByAddress(ipBuf) });
        } catch (Exception e) {
            DLog.e(Log.getStackTraceString(e));
        }
    }
    public static void nuck21() {
        try {
            Class inetAddressClass = InetAddress.class;
            Field field = inetAddressClass.getDeclaredField("addressCache");
            field.setAccessible(true);
            Object object = field.get(inetAddressClass);
            Class cacheClass = object.getClass();
            Method putMethod = cacheClass.getDeclaredMethod("put",
                    String.class, int.class, InetAddress[].class);
            putMethod.setAccessible(true);
            String str = "42.121.4.223";
            String[] ipStr = str.split("\\.");
            byte[] ipBuf = new byte[4];
            for (int i = 0; i < 4; i++) {
                ipBuf[i] = (byte) (Integer.parseInt(ipStr[i]) & 0xff);
            }
            putMethod.invoke(object, "m.betterwood.com", 0,
                    new InetAddress[] { InetAddress.getByAddress(ipBuf) });
        } catch (Exception e) {
            DLog.e(Log.getStackTraceString(e));
        }

    }
}
