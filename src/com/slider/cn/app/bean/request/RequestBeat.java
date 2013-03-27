package com.slider.cn.app.bean.request;

public class RequestBeat {
    private String title;
    private double price;
    private String currency;
    private String description;
    private String photo_urls;
    private String place;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPhoto_urls() {
		return photo_urls;
	}
	public void setPhoto_urls(String photo_urls) {
		this.photo_urls = photo_urls;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
}
