package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DefaultCityModel {

	@Expose
	@SerializedName("status")
	public int status;
	@Expose
	@SerializedName("data")
	public CityModel2 data;
}
