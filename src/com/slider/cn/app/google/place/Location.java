package com.slider.cn.app.google.place;

public class Location {
	private double  lat;
	private double lng;
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	@Override
	public String toString() {
		return "Location {lat=" + lat + ", lng=" + lng + "}";
	}
	
}
