package com.heart.beat.cn.util;

/**
 * 该类包含了该应用共享的大部分常量信息
 * @author slider
 *
 */
public interface ConstantsI {
	// server constant 
	String SERVER_HOST = "http://192.168.1.66";
	String QUERY_FORMAT = "?format=json";
	String BEAT_RESOURCE = "/api/v1/beat/";
	
	String USERNAME = "username";
	String PASSWORD = "password";
	String PASSWORD1 = "password1";
	String PASSWORD2 = "password2";
	
	/* LoginAliasActivity 的一些信号量 */
	int NOTHING = -1;
	String LOGIN_TARGET = "login target";
	int REGISTER = 0;
	int FORGOT_P0ASSWORD = 1;
	int SIGN_FACEBOOK = 2;
	int SIGN_GOOGLE = 3;
	int SIGN_TWITTER = 4;
	
}
