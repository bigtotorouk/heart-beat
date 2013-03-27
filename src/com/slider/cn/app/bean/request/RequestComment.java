package com.slider.cn.app.bean.request;

public class RequestComment {
	//private String creator;  提交的时候没有必要知名creator,因为token已经表明了sessionId的
	private String beat;
	private String data; /*128 char*/
	private String reply_to;
	
	public String getBeat() {
		return beat;
	}
	public void setBeat(String beat) {
		this.beat = beat;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getReply_to() {
		return reply_to;
	}
	public void setReply_to(String reply_to) {
		this.reply_to = reply_to;
	}
}
