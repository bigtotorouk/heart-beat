package com.slider.cn.app.httpurlconnectionnet;


import java.net.HttpURLConnection;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
/**
 * 这里暂时这个类好像没有用
 * @author slider
 *
 */
public class CookieStore {
	public static void setCookie(Context context, HttpURLConnection http,String url) {
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeSessionCookie();
		CookieSyncManager.createInstance(context);
		cookieManager.setAcceptCookie(true);

		String cookieVal = "";
		String key = "";
		for (int i = 1; (key = http.getHeaderFieldKey(i)) != null; i++ ) {
		if (key.equalsIgnoreCase("set-cookie")) {
		cookieVal = http.getHeaderField(i);
		cookieManager.setCookie(url, cookieVal );
		}
		}

		CookieSyncManager.getInstance().sync();
	}

	public static boolean hasCookie(HttpURLConnection http) {
		boolean hasCookie = false;
		String cookieKey = "";

		for (int i = 1; (cookieKey = http.getHeaderFieldKey(i)) != null; i++) {
			if (cookieKey.equalsIgnoreCase("Cookie")) {
				hasCookie = true;
			}
		}

		return hasCookie;
	}
}
