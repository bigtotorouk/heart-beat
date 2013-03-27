package com.slider.cn.app.httpurlconnectionnet;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
/**
 * 
 * @author slider
 *
 */
public class SharedStore {

	/**
	 * save cookie on the device
	 */
	public void save(Context context, String key, String value) {
		SharedPreferences sf = context.getSharedPreferences("setting",
				Context.MODE_PRIVATE);
		Editor editor = sf.edit();
		editor.putString(key, value);
		editor.commit();
		System.out.println("SharedStore ---> save "+value);
	}

	/**
	 * get cookie from device
	 * 
	 * @param context
	 * @return
	 */
	public String getValue(Context context, String key) {
		SharedPreferences sf = context.getSharedPreferences("setting",
				Context.MODE_PRIVATE);
		String value = sf.getString(key, "");
		System.out.println("SharedStore ---> getValue "+value);
		return value;
	}

}
