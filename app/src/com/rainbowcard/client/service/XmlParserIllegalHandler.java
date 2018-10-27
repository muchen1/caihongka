package com.rainbowcard.client.service;

import com.rainbowcard.client.model.CityModel2;
import com.rainbowcard.client.model.ProvinceModel2;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;


public class XmlParserIllegalHandler extends DefaultHandler {

	/**
	 * 存储所有的解析对象
	 *//*
	private List<ProvinceModel2> provinceList = new ArrayList<ProvinceModel2>();

	public XmlParserIllegalHandler() {

	}

	public List<ProvinceModel2> getDataList() {
		return provinceList;
	}

	@Override
	public void startDocument() throws SAXException {
		// 当读到第一个开始标签的时候，会触发这个方法
	}

	ProvinceModel2 provinceModel = new ProvinceModel2();
	CityModel2 cityModel = new CityModel2();

	@Override
	public void startElement(String uri, String localName, String qName,
							 Attributes attributes) throws SAXException {
		// 当遇到开始标记的时候，调用这个方法
		if (qName.equals("province")) {
			provinceModel = new ProvinceModel2();
			provinceModel.setName(attributes.getValue(0));
			provinceModel.setIllegal(attributes.getValue(1));
			provinceModel.setCityList(new ArrayList<CityModel2>());
		} else if (qName.equals("city")) {
			cityModel = new CityModel2();
			cityModel.setName(attributes.getValue(0));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// 遇到结束标记的时候，会调用这个方法
		if (qName.equals("city")) {
			provinceModel.getCityList().add(cityModel);
		} else if (qName.equals("province")) {
			provinceList.add(provinceModel);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
	}*/

}
