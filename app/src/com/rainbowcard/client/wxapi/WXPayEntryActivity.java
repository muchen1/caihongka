package com.rainbowcard.client.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.rainbowcard.client.base.Constants;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.wxapi.model.WXPayEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by gc on 14/10/20.
 * use for wxpay
 * don't modify name
 */
public final class WXPayEntryActivity extends MyBaseActivity implements IWXAPIEventHandler {


    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {

        Log.d("GCCCCCC!",resp.errStr+"?????"+resp.errCode+"???"+resp.openId);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            EventBus.getDefault().post(new WXPayEvent(resp.getType(), resp.errCode, resp.errStr));
        }

        finish();
    }

}
