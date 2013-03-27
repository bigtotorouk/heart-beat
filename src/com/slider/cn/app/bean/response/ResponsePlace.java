package com.slider.cn.app.bean.response;

import java.util.List;

public class ResponsePlace {
	private List<String> beats;
	private String city;
	private String country;
	private String lat;
	private String lng;
	private String location;
	private String name;
	private String places_id;
	private String places_ref;
	private String resource_uri;
	public List<String> getBeats() {
		return beats;
	}
	public void setBeats(List<String> beats) {
		this.beats = beats;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPlaces_id() {
		return places_id;
	}
	public void setPlaces_id(String places_id) {
		this.places_id = places_id;
	}
	public String getPlaces_ref() {
		return places_ref;
	}
	public void setPlaces_ref(String places_ref) {
		this.places_ref = places_ref;
	}
	public String getResource_uri() {
		return resource_uri;
	}
	public void setResource_uri(String resource_uri) {
		this.resource_uri = resource_uri;
	}
	@Override
	public String toString() {
		return "ResponsePlace [beats=" + beats + ", city=" + city
				+ ", country=" + country + ", lat=" + lat + ", lng=" + lng
				+ ", location=" + location + ", name=" + name + ", places_id="
				+ places_id + ", places_ref=" + places_ref + ", resource_uri="
				+ resource_uri + "]";
	}
	
}
