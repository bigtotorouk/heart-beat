package com.google.map.test.xml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

public class PullParseService {
	public List<PointInf> parseDateSource(InputStream inputStream) throws Exception{
		List<PointInf> pointInfs = null;
		PointInf pointInf = null;
		XmlPullParser parse = Xml.newPullParser();
		parse.setInput(inputStream, "utf-8");
		
		int event = parse.getEventType();// Returns the type of the current event (START_TAG, END_TAG, TEXT, etc.）
		
		while(event!=XmlPullParser.END_DOCUMENT){
			
			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				pointInfs = new ArrayList<PointInf>();//初始化books集合
				break;
			case XmlPullParser.START_TAG:
				String tag = parse.getName();
				if(tag.equals("pointinfo")){
					pointInf = new PointInf();
					//book.setId(Integer.parseInt(parse.getAttributeValue(0)));
					//或者这样也可以的
					pointInf.setType(Integer.parseInt(parse.getAttributeValue(null, "type")));
					pointInf.setLat(Float.parseFloat(parse.getAttributeValue(null, "lat")));
					pointInf.setLon(Float.parseFloat(parse.getAttributeValue(null, "lon")));
					Log.d("service", "lat: "+pointInf.getLat());
					Log.d("service", "lon: "+pointInf.getLon());
					pointInf.setTitle(parse.getAttributeValue(null, "title"));
					pointInf.setSnippt(parse.getAttributeValue(null, "snippt"));
					
				}
				/*if(pointInf!=null){
					*//**
					 * note： 这里的if ...else if.... 不能改为 if。。。 if。。。。(原因是换成后者会出现异常，这个可能与XmlPullParser的getName()的内部实现有联系:
					 * 具体的原因是KXmlParse的nextText()方法本身会调用next()函数具体的看源码，我也没有好好看)
					 *//*
					if(tag.equals("lat")){
						pointInf.setLat(Integer.parseInt(parse.getAttributeValue(null, "lat")));
						System.out.println(pointInf.getLat());
					}else if(tag.equals("lon")){
						pointInf.setLon(Integer.parseInt(parse.getAttributeValue(null, "lon")));
						System.out.println(pointInf.getLon());
					}else if(tag.equals("title")){
						pointInf.setTitle(parse.getAttributeValue(null, "title"));
						System.out.println(pointInf.getTitle());
					}else if(tag.equals("snippt")){
						pointInf.setSnippt(parse.getAttributeValue(null, "snippt"));
						System.out.println(pointInf.getSnippt());
					}
				}*/
				break;
			case XmlPullParser.END_TAG:
				if(parse.getName().equals("pointinfo")){
					Log.d("service", pointInf.toString());
					pointInfs.add(pointInf);
					pointInf = null;
				}
				break;
			default:
				break;
			}
			event = parse.next();//进入到下一个元素并触发相应事件
		}
		return pointInfs;
	}
}
