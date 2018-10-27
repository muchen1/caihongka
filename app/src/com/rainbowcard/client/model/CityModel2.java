package com.rainbowcard.client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CityModel2 {
	@Expose
	@SerializedName("city_name")
	public String name;
	@Expose
	@SerializedName("province_name")
	public String provinceName;
	@Expose
	@SerializedName("car_engine_len")
	public int carEngineLen;
	@Expose
	@SerializedName("car_class_len")
	public int carClassLen;
	@Expose
	@SerializedName("pre_car_num")
	public String preCarNum;
	@Expose
	@SerializedName("query_city_code")
	public String cityCode;
	@Expose
	@SerializedName("source_type")
	public int sourceType;

}
