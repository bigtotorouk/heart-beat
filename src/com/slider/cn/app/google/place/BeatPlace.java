package com.slider.cn.app.google.place;
/**
 * beat - place
 * @author slider
 *
 */
public class BeatPlace {
	private String places_id;
	private String places_ref;
	private double lng;
	private double lat;
	private String country;
	private String city;
	private String location;
	private String name;
	private String coupons;
	
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



	public double getLng() {
		return lng;
	}



	public void setLng(double lng) {
		this.lng = lng;
	}



	public double getLat() {
		return lat;
	}



	public void setLat(double lat) {
		this.lat = lat;
	}



	public String getCountry() {
		return country;
	}



	public void setCountry(String country) {
		this.country = country;
	}



	public String getCity() {
		return city;
	}



	public void setCity(String city) {
		this.city = city;
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



	public String getCoupons() {
		return coupons;
	}



	public void setCoupons(String coupons) {
		this.coupons = coupons;
	}

	@Override
	public String toString() {
		return "BeatPlace [places_id=" + places_id + ", places_ref="
				+ places_ref + ", lng=" + lng + ", lat=" + lat + ", country="
				+ country + ", city=" + city + ", location=" + location
				+ ", name=" + name + ", coupons=" + coupons + "]";
	}
	
}
