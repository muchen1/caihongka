package com.rainbowcard.client.utils;

import android.content.Context;
import android.text.TextUtils;
import com.rainbowcard.client.base.Constants;

/**
 * Created by gc on 2016-5-15.
 */
public class LoginControl {
    private static LoginControl inst;
    private Context mContext;
    public static LoginControl getInstance(Context context) {
        if (inst == null)
            synchronized (LoginControl.class) {
                if (inst == null) {
                    inst = new LoginControl(context.getApplicationContext());
                }
            }
        return inst;
    }

    private LoginControl(Context context) {
        mContext = context;
    }

    // TODO 安全的验证登陆机制 用户未登录时的处理逻辑
    public boolean isLogin() {
        String uid = MyConfig.getSharePreStr(mContext, Constants.USERINFO, Constants.UID);
        if(!TextUtils.isEmpty(uid) && !uid.equals("0")){
            return true;
        }
        return false;
    }
}
