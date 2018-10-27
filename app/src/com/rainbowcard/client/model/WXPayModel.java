package com.rainbowcard.client.model;

import java.io.Serializable;

/**
 * Created by gc on 2016-11-24.
 */
public class WXPayModel implements Serializable {
    public int status;
    public String message;
    public WXPayData data;
}
