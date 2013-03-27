package com.google.map.test.xml;

public class PointInf {
	int type;
	float lat;	//经度
	float lon;	//纬度
	String title;
	String snippt;
	
	public PointInf(){
		
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public float getLat() {
		return lat;
	}
	public void setLat(float lat) {
		this.lat = lat;
	}
	public float getLon() {
		return lon;
	}
	public void setLon(float lon) {
		this.lon = lon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSnippt() {
		return snippt;
	}

	public void setSnippt(String snippt) {
		this.snippt = snippt;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(type+" ").append(lat+" ").append(lon+"");
		return sb.toString();
	}
}
