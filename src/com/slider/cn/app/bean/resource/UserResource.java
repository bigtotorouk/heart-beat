package com.slider.cn.app.bean.resource;

import java.util.HashMap;
import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.heart.beat.cn.util.ConstantsI;
import com.slider.cn.app.bean.response.ResponseProfile;
import com.slider.cn.app.exception.NetException;
import com.slider.cn.app.httpurlconnectionnet.HttpJsonUtil;
import com.slider.cn.app.httpurlconnectionnet.HttpResult;
import com.slider.cn.app.httpurlconnectionnet.HttpUtil;
/**
 * 关于用户的资源接口
 * @author ken
 *
 */
public class UserResource {
	private static final String REGISTER_URL = ConstantsI.SERVER_HOST+"/accounts/register/";
	private static final String LOGIN_URL = ConstantsI.SERVER_HOST+"/accounts/login/";
	private static final String PROFILE_URL=ConstantsI.SERVER_HOST+"/api/v1/userprofile/";///api/v1/userprofile/2/
	
	private Gson gson;
	
	public UserResource(){
		gson = new Gson();
	}
	/**
	 * 
	 * @param context
	 * @param params
	 * @return null when register failed.
	 * @throws NetException
	 */
	public ResponseProfile register(Context context,HashMap<String, String> params) throws NetException{
		getSession(REGISTER_URL,context);
		
		HttpResult response = HttpUtil.getInstance().connectPost(REGISTER_URL, context, params,false);
		ResponseProfile profile = null;
		if(response!=null&&response.isCodeOk()){
			System.out.println("register:  "+response);
			try {
				profile = gson.fromJson(response.getResult(), ResponseProfile.class);
			} catch (JsonSyntaxException e) {
				throw new NetException(NetException.ERROR_JSONSYNTEX);
			}
		}
		return profile;
	}
	/**
	 * 
	 * @param context
	 * @param username
	 * @param password
	 * @return null when login failed
	 * @throws NetException
	 */
	public ResponseProfile login(Context context,String username,String password) throws NetException{
		getSession(LOGIN_URL,context);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(ConstantsI.USERNAME, username);
		params.put(ConstantsI.PASSWORD, password);
		HttpResult response = HttpUtil.getInstance().connectPost(LOGIN_URL, context, params,false);
		ResponseProfile profile = null;
		if(response!=null&&response.isCodeOk()){
			System.out.println("login:  "+response);
			try {
				profile = gson.fromJson(response.getResult(), ResponseProfile.class);
			} catch (JsonSyntaxException e) {
				throw new NetException(NetException.ERROR_JSONSYNTEX);
			}
		}
		return profile;
	}
	
	/**
	 * 
	 * @param url
	 * @param context
	 * @return null when request failed
	 * @throws NetException
	 */
	public ResponseProfile profile(String url,Context context) throws NetException{
		ResponseProfile profile = null;
		HttpJsonUtil.getInstance().connectGet(url, context, true);
		HttpResult response = HttpJsonUtil.getInstance().connectGet(url, context, true);
		if(response!=null&&response.isCodeOk()){
			System.out.println("profile:  "+response);
			try {
				profile = gson.fromJson(response.getResult(), ResponseProfile.class);
				
			} catch (JsonSyntaxException e) {
				throw new NetException(NetException.ERROR_JSONSYNTEX);
			}
			
		}
		return profile;
	}

	/**
	 * it is invoked before you register or login
	 * @param url
	 * @param context
	 * @return
	 * @throws NetException
	 */
	private String getSession(String url,Context context) throws NetException{
		String result = null;
		HttpUtil http = HttpUtil.getInstance();
		result = http.newSession(url, context,null);
		System.out.println("getSession csrftoken :"+result);
		return result;
	}
	
	public String createProfileFromId(String id){
		return PROFILE_URL + id+"/?format=json";
	}

}
