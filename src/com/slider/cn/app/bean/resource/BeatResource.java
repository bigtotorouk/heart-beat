package com.slider.cn.app.bean.resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.heart.beat.cn.util.ConstantsI;
import com.slider.cn.app.bean.request.RequestBeat;
import com.slider.cn.app.bean.response.ResponseBeat;
import com.slider.cn.app.bean.response.ResponseBeats;
import com.slider.cn.app.exception.NetException;
import com.slider.cn.app.httpurlconnectionnet.FileEntity;
import com.slider.cn.app.httpurlconnectionnet.HttpClientUtil;
import com.slider.cn.app.httpurlconnectionnet.HttpJsonUtil;
import com.slider.cn.app.httpurlconnectionnet.HttpResult;
import com.slider.cn.app.httpurlconnectionnet.HttpUtil;

/**
 * 资源beat访问类
 * @author ken
 *
 */
public class BeatResource {
	private final String TAG = "BeatResource";
	
	private static final String GET_RECENT = ConstantsI.SERVER_HOST+"/beat/recent/?format=json";
	private static final String GET_HOT = ConstantsI.SERVER_HOST+"/beat/hot/?format=json";
	private static final String GET_FRIEND = ConstantsI.SERVER_HOST+"/accounts/feeds/?format=json";
	private static final String POST_BEAT = ConstantsI.SERVER_HOST+"/api/v1/beat/?format=json";
	
	private static final String GET_BEAT = "http://192.168.1.66/api/v1/beat/2/?format=json";
	private static final String GET_BEATS = "http://192.168.1.66/api/v1/beat/?format=json";
	private static final String GET_BEATS2 = "http://192.168.1.66/api/v1/beat/?format=json";
	private static final String POST_IMAGE = "http://192.168.1.66/beat/2/photo_upload/";
	private static final String GET_BEATS_USER = "http://192.168.1.66/accounts/3/beats/";
	private Gson gson;
	private Context mContext;
	
	public BeatResource(Context context){
		mContext = context;
		gson = new Gson();
	}

	
	public String convertUrlFromId(int beatId){
		return "/api/v1/beat/"+beatId+"/";
	}
	
	/**
	 * test pass
	 * @param url
	 * @throws NetException 
	 */
	public ResponseBeat getBeat(int beatId,boolean requireLogin) throws NetException{
		String beatUrl = ConstantsI.SERVER_HOST+ConstantsI.BEAT_RESOURCE+beatId+"/?format=json";
		return getBeat(beatUrl, requireLogin);
	}
	public ResponseBeat getBeat(String beatUrl,boolean requireLogin) throws NetException{
		HttpResult response = HttpJsonUtil.getInstance().connectGet(beatUrl,mContext,requireLogin);
		System.out.println("getBeat: "+response);
		ResponseBeat beat = null;
		if(response!=null&&response.isCodeOk()){
			try {
				beat= gson.fromJson(response.getResult(), ResponseBeat.class);
			} catch (JsonSyntaxException e) {
				throw new NetException(NetException.ERROR_JSONSYNTEX);
			}
		}
		if(!response.isCodeOk()){
			throw new NetException(NetException.ERROR_SERVER);
		}
		System.out.println("getBeat:  "+beat);
		return beat;
	}
	/**
	 * test pass 2012-7-20
	 * @param beat
	 * @return beatUrl  http://192.168.1.66/api/v1/beat/120/?format=json
	 * @throws NetException 
	 */
	public String postBeat(RequestBeat beat) throws NetException{
		String json = gson.toJson(beat);
		HttpJsonUtil http = HttpJsonUtil.getInstance();
		HttpResult response = http.connectPost(POST_BEAT,mContext, json);
		System.out.println("postBeat is "+response);
		if (response!=null&&response.isCodeOk()) {
			//result=http://192.168.1.66/api/v1/beat/120/
			return response.getResult()+ConstantsI.QUERY_FORMAT;
		}else {
			return null;
		}
		
	}
	/**
	 * 这里我仍然实现的是上传多个files。事实上这个项目要求说每次只上传一个image
	 * @param beatUrl
	 * @param files
	 * @return
	 * @throws NetException 
	 */
	public boolean postImage(int beatId,List<FileEntity> files) throws NetException{
		String url = ConstantsI.SERVER_HOST+ConstantsI.BEAT_RESOURCE+beatId+"/photo_upload/";
		url= url.replace("api/v1/", "");
		System.out.println("postImage  "+url);
		for(FileEntity file: files){
			List<FileEntity> images = new ArrayList<FileEntity>();
			images.add(file);
			HttpResult response = HttpJsonUtil.getInstance().connectPost(url, mContext,images);
			System.out.println("postImage "+response);
			if (response!=null&&response.isCodeOk()) {
				//return true;
			}else {
				return false;
			}
		}
		return true;
	}
	/**
	 * 该接口现在无用
	 * @return
	 * @throws NetException 
	 */
	/*public ResponseBeats getBeats() throws NetException{
		
		String result = HttpJsonUtil.getInstance().connectGet(GET_BEATS,mContext,false);
		System.out.println("getBeats: resulst "+result);
		ResponseBeats beats = gson.fromJson(result, ResponseBeats.class);
		System.out.println("getBeats: "+beats);
		return beats;
	}*/
	/**
	 * 
	 * @param ids
	 * @param requireLogin 是否要求cookie，也是否要求用户登录
	 * @return
	 * @throws NetException
	 */
	public ResponseBeats getBeats(List<String> ids,boolean requireLogin) throws NetException{
		if(ids==null||ids.size()==0)
			return null;
		/* 把ids封装到url */
		StringBuilder sb = new StringBuilder(ConstantsI.SERVER_HOST+"/api/v1/beat/set/");
		for(String id : ids){
			sb.append(id+",");
		}
		sb.deleteCharAt(sb.length()-1).append("/?format=json");	
		String url = sb.toString();
		
		HttpResult response = HttpJsonUtil.getInstance().connectGet(url,mContext,requireLogin);
		System.out.println("getBeats: resulst "+response);
		ResponseBeats beats = null;
		if (response!=null&&response.isCodeOk()){
			try {
				beats = gson.fromJson(response.getResult(), ResponseBeats.class);
			} catch (JsonSyntaxException e) {
				// TODO: handle exception
			}
		}
		
		System.out.println("getBeats: "+beats);
		return beats;
	}
	public ResponseBeats getRecent(boolean requireLogin) throws NetException{
		HttpResult response = HttpJsonUtil.getInstance().connectGet(GET_RECENT,mContext,false);
		if(response!=null&&response.isCodeOk()){
			System.out.println("getBeats: resulst "+response);
			List<String> ids = new ArrayList<String>();
			try {
				JSONArray json = new JSONArray(response.getResult());
				for(int i = 0;i<json.length();i++){
					String id = json.getString(i);
					ids.add(id);
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("getRecent ids "+ids);
			ResponseBeats beats = getBeats(ids,requireLogin);
			return beats;
		}else {
			return null;
		}
	}
	public ResponseBeats getFriends(boolean requireLogin) throws NetException{
		HttpResult response = HttpJsonUtil.getInstance().connectGet(GET_FRIEND,mContext,requireLogin);
		if(response!=null&&response.isCodeOk()){
			System.out.println("getFriends: resulst "+response);
			List<String> ids = new ArrayList<String>();
			try {
				JSONArray json = new JSONArray(response.getResult());
				for(int i = 0;i<json.length();i++){
					String id = json.getString(i);
					ids.add(id.split(":")[2]);
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("getFriends ids "+ids);
			ResponseBeats beats = getBeats(ids,requireLogin);
			return beats;
		}else {
			return null;
		}
		
	}
	public ResponseBeats getHot(boolean requireLogin) throws NetException{
		HttpResult response = HttpJsonUtil.getInstance().connectGet(GET_HOT,mContext,false);
		if(response!=null&&response.isCodeOk()){
			System.out.println("getBeats: resulst "+response);
			List<String> ids = new ArrayList<String>();
			try {
				JSONArray json = new JSONArray(response.getResult());
				for(int i = 0;i<json.length();i++){
					String id = json.getString(i);
					ids.add(id);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("getBeats ids "+ids);
			ResponseBeats beats = getBeats(ids,requireLogin);
			return beats;
		}else {
			return null;
		}
	
	}
	/**
	 * 通过user的id，获得该用户分享的beat
	 * @param context
	 * @param userId
	 * @return
	 * @throws NetException
	 */
	public ResponseBeats getBeats(Context context,String userId) throws NetException {
		String url = ConstantsI.SERVER_HOST+"/accounts/"+userId+"/beats/";
		HttpResult response = HttpJsonUtil.getInstance().connectGet(url, context, true);
		if(response!=null&&response.isCodeOk()){
			System.out.println("getBeats: resulst "+response);
			List<String> ids = new ArrayList<String>();
			try {
				JSONArray json = new JSONArray(response.getResult());
				for(int i = 0;i<json.length();i++){
					String id = json.getString(i);
					ids.add(id);
				}
			} catch (JSONException e) {
				throw new NetException(NetException.ERROR_JSONSYNTEX);
			}
			System.out.println("getBeats ids "+ids);
			ResponseBeats beats = getBeats(ids,false);
			return beats;
		}else {
			return null;
		}
	}
	/**
	 * 对某个beat添加赞的功能。关键字：beatId
	 * 这里使用httpclient，由于使用httpurlconnection.getinputstream()时候，出现filenotfound的异常
	 * @param beatId
	 * @return
	 * @throws NetException
	 */
	public boolean addHeart(String beatId) throws NetException{
		String url = ConstantsI.SERVER_HOST+"/beat/"+beatId+"/heartbeat/";
		
		//HttpResult response = HttpUtil.getInstance().connectPost(url, mContext, null, true);
		HttpResult response = HttpClientUtil.getInstance().connectPost(url, mContext, HttpClientUtil.CONTENT_URLENCODE,"");
		Log.d(TAG, "HTTPRESULT "+response);
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
	/**
	 * 对某个beat添加赞的功能。关键字：beatId
	 * @param beatId
	 * @return
	 * @throws NetException
	 */
	public boolean removeHeart(String beatId) throws NetException{
		String url = ConstantsI.SERVER_HOST+"/beat/"+beatId+"/heartbeat/";
		HashMap<String, String> params = new HashMap<String, String>();
		String entitiy = "action=remove";
		HttpResult response = HttpClientUtil.getInstance().connectPost(url, mContext, HttpClientUtil.CONTENT_URLENCODE,entitiy);
		System.out.println("AccountResource addFriend result " + response);
		if(response!=null&&response.isCodeOk()){
			return true;
		}else{
			return false;
		}
	}
}
