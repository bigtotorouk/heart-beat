package com.slider.cn.app.bean.response;

import java.util.List;

public class ResponseProfile {
	
    private String address ;
    private String avatar ;
    private String city ;
    private String country ;
    private String display_name ;
    private int gift_points ;
    private String id ;
    private List<String> friends ;
    private int level_points ;
    private String resource_uri ;
    private String state ;
    private ResponseUser user ;
    private ResponseUser_setting user_settings ;
    private String zipcode ;
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	public List<String> getFriends() {
		return friends;
	}
	public void setFriends(List<String> friends) {
		this.friends = friends;
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
	public String getDisplay_name() {
		return display_name;
	}
	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}
	public int getGift_points() {
		return gift_points;
	}
	public void setGift_points(int gift_points) {
		this.gift_points = gift_points;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getLevel_points() {
		return level_points;
	}
	public void setLevel_points(int level_points) {
		this.level_points = level_points;
	}
	public String getResource_uri() {
		return resource_uri;
	}
	public void setResource_uri(String resource_uri) {
		this.resource_uri = resource_uri;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public ResponseUser getUser() {
		return user;
	}
	public void setUser(ResponseUser user) {
		this.user = user;
	}
	public ResponseUser_setting getUser_settings() {
		return user_settings;
	}
	public void setUser_settings(ResponseUser_setting user_settings) {
		this.user_settings = user_settings;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	@Override
	public String toString() {
		return "UserBean [address=" + address + ", avatar=" + avatar
				+ ", city=" + city + ", country=" + country + ", display_name="
				+ display_name + ", gift_points=" + gift_points + ", id=" + id
				+ ", level_points=" + level_points + ", resource_uri="
				+ resource_uri + ", state=" + state + ", user=" + user
				+ ", user_settings=" + user_settings + ", zipcode=" + zipcode
				+ "]";
	}
}


