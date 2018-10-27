package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ProvinceModel2 {

	@Expose
	@SerializedName("status")
	public int status;
	@Expose
	@SerializedName("data")
	public ArrayList<ProvinceEntity> data;
}
