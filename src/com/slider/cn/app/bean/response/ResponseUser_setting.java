package com.slider.cn.app.bean.response;

public class ResponseUser_setting{
	private boolean allow_all_messages ;
	private String resource_uri  ;
	private boolean share_on_facebook ;
	private boolean share_on_twitter ;
	public boolean isAllow_all_messages() {
		return allow_all_messages;
	}
	public void setAllow_all_messages(boolean allow_all_messages) {
		this.allow_all_messages = allow_all_messages;
	}
	public String getResource_uri() {
		return resource_uri;
	}
	public void setResource_uri(String resource_uri) {
		this.resource_uri = resource_uri;
	}
	public boolean isShare_on_facebook() {
		return share_on_facebook;
	}
	public void setShare_on_facebook(boolean share_on_facebook) {
		this.share_on_facebook = share_on_facebook;
	}
	public boolean isShare_on_twitter() {
		return share_on_twitter;
	}
	public void setShare_on_twitter(boolean share_on_twitter) {
		this.share_on_twitter = share_on_twitter;
	}
	@Override
	public String toString() {
		return "User_setting [allow_all_messages=" + allow_all_messages
				+ ", resource_uri=" + resource_uri + ", share_on_facebook="
				+ share_on_facebook + ", share_on_twitter=" + share_on_twitter
				+ "]";
	}
}