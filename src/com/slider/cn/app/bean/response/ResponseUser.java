package com.slider.cn.app.bean.response;

public class ResponseUser{
	private String date_joined ;
    private String email ;
    private String first_name ;
    private String id ;
    private String last_login ;
    private String last_name ;
    private String resource_uri ;
    private String username ;
	public String getDate_joined() {
		return date_joined;
	}
	public void setDate_joined(String date_joined) {
		this.date_joined = date_joined;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLast_login() {
		return last_login;
	}
	public void setLast_login(String last_login) {
		this.last_login = last_login;
	}
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	public String getResource_uri() {
		return resource_uri;
	}
	public void setResource_uri(String resource_uri) {
		this.resource_uri = resource_uri;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@Override
	public String toString() {
		return "User [date_joined=" + date_joined + ", email=" + email
				+ ", first_name=" + first_name + ", id=" + id + ", last_login="
				+ last_login + ", last_name=" + last_name + ", resource_uri="
				+ resource_uri + ", username=" + username + "]";
	}
}