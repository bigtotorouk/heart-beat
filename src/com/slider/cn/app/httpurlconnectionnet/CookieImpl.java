package com.slider.cn.app.httpurlconnectionnet;

import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.slider.cn.app.exception.NetException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;


public class CookieImpl extends SharedStore{
	private static final String TAG = "CookieImpl";
	
	public static final String testCookie = "csrftoken=m2qP4TSIqYF0fSHmCMZpkzI6ObspODJb; expires=Thu, 18-Jul-2013 09:06:58 GMT; Max-Age=31449600; Path=/";
	
	public static final String COOKIE = "cookie";
	public static final String CSRFTOKEN = "csrftoken";
	public static final String USER_URI = "user_uri";
	public static final String PROFILE_ID = "profile_id";
	private static final String COOK_BEGIN = "start_time"; /* create-time of cookie */
	
	private final boolean DEBUG = true;
	/**
	 * key： Domain，Path，Expires，Max-Age，Secure，HttpOnly
	 */
	private String cookies;
	private String csrftoken;
	private HashMap<String, String> datas;
	
	
	public void updateCookie(Context context,String cookie){
		cookie = "csrftoken="+getCsrftoken(context)+";"+cookie;
		System.out.println("CookieImpl updateCookie: cookie is "+cookie);
		save(context,COOKIE,cookie);
		this.cookies = cookie;
		
		//parseCookie(cookie);
	}
	public void clearCookie(Context context){
		save(context,COOKIE,"");
	}
	public void updateCsrftoken(Context context,String csrftoken){
		System.out.println("CookieImpl updateCsrftoken: csrftoken is "+csrftoken);
		save(context,CSRFTOKEN,csrftoken);
		this.csrftoken = csrftoken;
	}
	public void setCsrftoken(Context context,URLConnection connection) throws NetException{
		
		csrftoken = getCsrftoken(context);
		if(csrftoken.equals("")){
			/* 因为取消了登陆注册机制，所以这里就没有必要抛出异常了 */
			//throw new NetException(NetException.ERROR_COOKIE);
		}
		connection.addRequestProperty("X-CSRFTOKEN", csrftoken);
		
	}
	public void setCookie(Context context,URLConnection connection) throws NetException{
		cookies = getCookie(context);
		if(cookies.equals(""))
			throw new NetException(NetException.ERROR_COOKIE);
		connection.setRequestProperty("Cookie",cookies);
		
	}
	
	/**
	 * 用户身份是否过期，没有过期就返回用户uri，否则返回为null
	 * @return userUri
	 */
	public String isOutdate(Context context){	
		String cookie = getCookie(context);
		if(cookie==null||cookie.equals(""))
			return null;
		else {
			String uri = getValue(context, USER_URI);
			Log.d(TAG, "isOutdate user uri is " +uri);
			return uri;
		}
		/*parseCookie(cookie);
		long max_age = Long.valueOf(datas.get("Max-Age"));
		try {
			//similar Wed, 13-Jan-2021 22:23:01 GMT
			SimpleDateFormat format = new SimpleDateFormat("EEE, dd-MMM-yyyy hh:mm:ss z");
			Date date1 =format.parse(datas.get("Expires"));
			Date date2 = new Date();
			long interval = date2.getTime()-date1.getTime();
			if(interval>max_age)
				return getValue(context, USER_URI);
		} catch (ParseException e) {
			System.out.println("could not parse time or you do not set Expires in cookie");
			e.printStackTrace();
		}
		return null;*/
	}
	
	private String getCookie(Context context){
		SharedPreferences sf = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
		return sf.getString(COOKIE, "");
	}
	private String getCsrftoken(Context context){
		SharedPreferences sf = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
		return sf.getString(CSRFTOKEN, "");
	}
	private void parseCookie(String cookie){
		Log.d("CookieImpl", "parseCookie cookie "+cookie);
		datas = new HashMap<String, String>();
		String[] arrays = cookie.split(";");
		String key,value;
		for(String e: arrays){
			if (e.indexOf("=")<0) {
				// 如果该string不包含‘=’，那就没有必要保存在hashmap里面了。这个string在Cookie只是为了说明作用.
				continue;
			}
			key = e.substring(0,e.indexOf("="));
			value = e.substring(e.indexOf("=")+1);
			datas.put(key, value);
		}
	}
	
	public static void main(String[] args) throws ParseException {

	}
	
}
