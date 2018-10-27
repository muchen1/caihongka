package com.rainbowcard.client.model;


public class SimpleResult<T> extends BaseResult{
	private static final long serialVersionUID = 1L;
	protected T result;
	protected T recommend;  //有多个继续添加
	
	

	public T getRecommend() {
		return recommend;
	}

	public void setRecommend(T recommend) {
		this.recommend = recommend;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}


}
