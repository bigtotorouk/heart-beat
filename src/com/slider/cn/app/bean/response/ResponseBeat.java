package com.slider.cn.app.bean.response;

import java.util.List;

public class ResponseBeat {
	private String id;
	private ResponseUser creator;
	private String title;
    private double price;
    private String currency;
    private String description;
    private ResponseBeatPlace place;
    private String upload_time;
    private List<String> hearts;
    private List<String> photo_urls;
    private List<String> comments; //"comments": ["12"],
    private String resource_uri;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ResponseUser getCreator() {
		return creator;
	}
	public void setCreator(ResponseUser creator) {
		this.creator = creator;
	}
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
	public ResponseBeatPlace getPlace() {
		return place;
	}
	public void setPlace(ResponseBeatPlace place) {
		this.place = place;
	}
	public String getUpload_time() {
		return upload_time;
	}
	public void setUpload_time(String upload_time) {
		this.upload_time = upload_time;
	}
	public List<String> getHearts() {
		return hearts;
	}
	public void setHearts(List<String> hearts) {
		this.hearts = hearts;
	}
	public List<String> getPhoto_urls() {
		return photo_urls;
	}
	public void setPhoto_urls(List<String> photo_urls) {
		this.photo_urls = photo_urls;
	}
	
	public List<String> getComments() {
		return comments;
	}
	public void setComments(List<String> comments) {
		this.comments = comments;
	}
	public String getResource_uri() {
		return resource_uri;
	}
	public void setResource_uri(String resource_uri) {
		this.resource_uri = resource_uri;
	}
	@Override
	public String toString() {
		return "ResponseBeat [id=" + id + ", creator=" + creator + ", title="
				+ title + ", price=" + price + ", currency=" + currency
				+ ", description=" + description + ", place=" + place
				+ ", upload_time=" + upload_time + ", hearts=" + hearts
				+ ", photo_urls=" + photo_urls + ", comments=" + comments
				+ ", resource_uri=" + resource_uri + "]";
	}
	
}
