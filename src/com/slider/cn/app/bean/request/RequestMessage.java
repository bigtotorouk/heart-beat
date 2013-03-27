package com.slider.cn.app.bean.request;

public class RequestMessage {
	private String creator;//read only?
	private String recipient;// 接收者 
	private String title;// 标题 char(32)
	private String body;// 内容 char(256)
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getRecipient() {
		return recipient;
	}
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
}
