package com.rainbowcard.client.wxapi.model;

/**
 * Created by gc on 14/10/20.
 */
public class WXPayEvent {
    public int type;
    public String errStr;
    public int errCode;

    public WXPayEvent(int type, int errCode, String errStr) {
        this.type = type;
        this.errStr = errStr;
        this.errCode = errCode;
    }

}
