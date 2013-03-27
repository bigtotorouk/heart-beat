package com.slider.cn.app.bean.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.slider.cn.app.bean.request.RequestProfile;
import com.slider.cn.app.bean.response.ResponseProfile;
import com.slider.cn.app.exception.NetException;
import com.slider.cn.app.httpurlconnectionnet.FileEntity;
import com.slider.cn.app.httpurlconnectionnet.HttpClientUtil;
import com.slider.cn.app.httpurlconnectionnet.HttpJsonUtil;
import com.slider.cn.app.httpurlconnectionnet.HttpResult;
import com.slider.cn.app.httpurlconnectionnet.HttpUtil;

public class AccountResource {
	private static final String ACCOUNTS_URL = "http://192.168.1.66/accounts/";
	private static final String PROFILE_URL = "http://192.168.1.66/api/v1/userprofile/";
	private static final String AVATAR_URL = "http://192.168.1.66/accounts/avatar_upload/";
	private static final String PATCH_PROFILE = "http://192.168.1.66/api/v1/userprofile/";

	private Gson gson;
	private Context mContext;
	public AccountResource(Context context){
		this.mContext = context;
		gson = new Gson();
	}
	/**
	 * Returns a 202 if succeeds, 403 if already a friend.
	 * @param id
	 * @throws NetException 
	 * @throws Exception
	 */
	public boolean addFriend(String id) throws NetException{
		String url = ACCOUNTS_URL+id+"/add_remove/";
		HttpUtil  http = HttpUtil.getInstance();
		HttpResult response = http.connectPost(url, mContext, null, true);
		System.out.println("AccountResource addFriend result " + response);
		if(response.getReponseCode()==202){
			return true;
		}else if (response.getReponseCode()==403) {
			/* 
			 * throw new NetException(response.getResult());  
			 *  */
			return false;
		}
		return false;/* 根据接口定义，返回正常情况下只有202与403 */
	}

	public boolean removeFriend(String id) throws NetException{
		String url = ACCOUNTS_URL+id+"/add_remove/";
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("action", "remove");
		HttpUtil  http = HttpUtil.getInstance();
		HttpResult response = http.connectPost(url, mContext, params, true);
		System.out.println("AccountResource addFriend result " + response);
		if(response!=null&&response.isCodeOk()){
			return true;
		}else{
			return false;
		}
	}
	public boolean postAvatar(String url,FileEntity file) throws NetException{
		if(url==null)
			url = AVATAR_URL;
		List<FileEntity> images = new ArrayList<FileEntity>();
		images.add(file);
		HttpResult response = HttpJsonUtil.getInstance().connectPost(url, mContext,images);
		System.out.println("postAvatar "+response); //test ,so you could see error-info
		if(response!=null&&response.isCodeOk()){
			return true;
		}else {
			return false;
		}
	}
	public String patchProfile(int id,RequestProfile profile) throws NetException{
		String url = PATCH_PROFILE+id+"/?format=json";
		String json = gson.toJson(profile);
		HttpClientUtil http = HttpClientUtil.getInstance();
		String result = http.connectPatch(url, mContext, json);
		System.out.println("patchProfile result "+result);
		return null;
		
	}
	/*public void removeFriend(String id){
		String url = ACCOUNTS_URL+id+"/remove/?format=json";
		HttpJsonUtil  http = HttpJsonUtil.getInstance();
		String result = http.connectPost(url, mContext, "");
		System.out.println("AccountResource addFriend result " + result);
	}*/
	public ResponseProfile getProfile(String id) throws IOException, Exception{
		String url = PROFILE_URL + id + "/?format=json";
		HttpResult response = HttpJsonUtil.getInstance().connectGet(url,mContext,true);
		ResponseProfile user = null;
		if(response!=null&&response.isCodeOk()){
			System.out.println("getProfiles: resulst "+response);
			try {
				user = gson.fromJson(response.getResult(), ResponseProfile.class);
			} catch (JsonSyntaxException e) {
				
			}
			
		}
		return user;
	}
	/**
	 * 这个接口由于返回json的数据，所以暂时搁浅
	 * @param ids
	 * @return
	 * @throws NetException 
	 * @throws IOException
	 * @throws Exception
	 */
	public List<ResponseProfile> getProfiles(List<String> ids) throws NetException{
		if(ids==null||ids.size()==0)
			return null;
		StringBuilder sb = new StringBuilder("http://192.168.1.66/api/v1/accounts/set/");
		for(String id : ids){
			sb.append(id+",");
		}
		sb.deleteCharAt(sb.length()-1).append("/?format=json");	
		String url = sb.toString();
		HttpResult response = HttpJsonUtil.getInstance().connectGet(url,mContext,true);
		/*if(response!=null&&response.isCodeOk()){
			System.out.println("getProfiles: resulst "+response);
			List<ResponseProfile> profiles = gson.fromJson(response.getResult(), respo.class);
		
		}
		
		*/
		return null;
	}
}
