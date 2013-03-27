package com.slider.cn.app.bean.request;

public class RequestProfile {
	private String display_name;// 显示名称(Nickname) 
	//private String avatar;// 头像 
	private String country;// 用户所属国家缩写代码 3 可选 
	private String state;// 州 20 可选 
	private String city;// 城市 30 可选 
	private String address;// 地址 200 可选 
	private String zipcode;
	public String getDisplay_name() {
		return display_name;
	}
	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	} 
}
