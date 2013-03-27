package com.slider.cn.app.google.place;

import java.util.List;

public class PlaceDetailMeta {
	private String name;
	private String vicinity;
	private List<String> types;
	private String formatted_phone_number;
	private String formatted_address;
	private List<AddressComponents> address_components;
	private Geometry geometry;
	private float rating; // float type
	private String url;
	private String icon;
	private String reference;
	private String id;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVicinity() {
		return vicinity;
	}
	public void setVicinity(String vicinity) {
		this.vicinity = vicinity;
	}
	public List<String> getTypes() {
		return types;
	}
	public void setTypes(List<String> types) {
		this.types = types;
	}
	public String getFormatted_phone_number() {
		return formatted_phone_number;
	}
	public void setFormatted_phone_number(String formatted_phone_number) {
		this.formatted_phone_number = formatted_phone_number;
	}
	public String getFormatted_address() {
		return formatted_address;
	}
	public void setFormatted_address(String formatted_address) {
		this.formatted_address = formatted_address;
	}
	public List<AddressComponents> getAddress_components() {
		return address_components;
	}
	public void setAddress_components(List<AddressComponents> address_components) {
		this.address_components = address_components;
	}
	public Geometry getGeometry() {
		return geometry;
	}
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}
	public float getRating() {
		return rating;
	}
	public void setRating(float rating) {
		this.rating = rating;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "PlaceDetailMeta {name=" + name + ", vicinity=" + vicinity
				+ ", types=" + types + ", formatted_phone_number="
				+ formatted_phone_number + ", formatted_address="
				+ formatted_address + ", address_components="
				+ address_components + ", geometry=" + geometry + ", rating="
				+ rating + ", url=" + url + ", icon=" + icon + ", reference="
				+ reference + ", id=" + id + "}";
	}
}
