package com.slider.cn.app.bean.response;

public class ResponseComment {
	private String beat;
	private ResponseUser creator;
	private String data;
	private int id;
	private String reply_to;
	private String resource_uri;
	private String upload_time;
	public String getBeat() {
		return beat;
	}
	public void setBeat(String beat) {
		this.beat = beat;
	}
	public ResponseUser getCreator() {
		return creator;
	}
	public void setCreator(ResponseUser creator) {
		this.creator = creator;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getReply_to() {
		return reply_to;
	}
	public void setReply_to(String reply_to) {
		this.reply_to = reply_to;
	}
	public String getResource_uri() {
		return resource_uri;
	}
	public void setResource_uri(String resource_uri) {
		this.resource_uri = resource_uri;
	}
	public String getUpload_time() {
		return upload_time;
	}
	public void setUpload_time(String upload_time) {
		this.upload_time = upload_time;
	}
	@Override
	public String toString() {
		return "ResponseBeatComment [beat=" + beat + ", creator=" + creator
				+ ", data=" + data + ", id=" + id + ", reply_to=" + reply_to
				+ ", resource_uri=" + resource_uri + ", upload_time="
				+ upload_time + "]";
	}
	
}
