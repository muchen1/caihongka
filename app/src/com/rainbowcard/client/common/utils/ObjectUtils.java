package com.rainbowcard.client.common.utils;

import android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by gc on 14/10/20.
 */
public class ObjectUtils {

    public static String toString(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        sb.append(object.getClass().getSimpleName()).append('{');

        boolean firstRound = true;

        for (Field field : fields) {
            if (!firstRound) {
                sb.append(", ");
            }
            firstRound = false;
            field.setAccessible(true);
            try {
                final Object fieldObj = field.get(object);
                final String value;
                if (null == fieldObj) {
                    value = "null";
                } else {
                    value = fieldObj.toString();
                }
                sb.append(field.getName()).append('=').append('\'')
                        .append(value).append('\'');
            } catch (IllegalAccessException ignore) {
                DLog.e(Log.getStackTraceString(ignore));
                //this should never happen
            }

        }

        sb.append('}');
        return sb.toString();
    }
}
