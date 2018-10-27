package com.rainbowcard.client.model;

import java.io.Serializable;

public class BaseResult implements Serializable {
	private static final long serialVersionUID = 1L;
	private int errcode;
	private String errmsg;

	public boolean isSuccess() {
		return errcode == 0;
	}

	public boolean isLogOut() {
		return errcode == 100;
	}

	public int getErrcode() {
		return errcode;
	}

	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

}
