package com.slider.cn.app.google.place.resource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.HttpClient;
import org.apache.http.client.ClientProtocolException;

import android.util.Log;
import java.io.IOException;
import com.google.gson.Gson;
import com.slider.cn.app.exception.NetException;
import com.slider.cn.app.google.place.Place;
import com.slider.cn.app.google.place.PlaceDetail;
import com.slider.cn.app.httpurlconnectionnet.HttpClientUtil;

public class PlaceService{

	private static final String GOOGLE_PLACE_KEY = "AIzaSyDZw9GvxGs04O0SC_prSpyCeZt-GoCnths";
	private static final String PLACESEARCH = "https://maps.googleapis.com/maps/api/place/search/";
	private static final String PLACEDETAIL = "https://maps.googleapis.com/maps/api/place/details/";	
	private static final String PLACEPOST = "https://maps.googleapis.com/maps/api/place/add/";
	private static String testurl = "https://maps.googleapis.com/maps/api/place/search/json?location=-33.8670522,151.1957362&radius=500&types=food&name=harbour&sensor=false&key=AIzaSyDZw9GvxGs04O0SC_prSpyCeZt-GoCnths";
	public static final int RADIOUS_DEFAULT = 2000;
	
	private Gson gson;
	
	public PlaceService(){
		gson = new Gson();
	}
	
	public String addPlace(){
		String query = "json?sensor=false&key="+GOOGLE_PLACE_KEY;
		
		return null;
	}
	
	public Place reversePlaceSearch(double lat,double lon) throws NetException{
		String query = "json" + "?location="+lat+","+lon+"&radius="+RADIOUS_DEFAULT+"&types=food&name=harbour&sensor=false&key="+GOOGLE_PLACE_KEY; 
		String url = PLACESEARCH + query;
		System.out.println("reversePlaceSearch url : "+url);
		String result = null;
		
		result = new HttpTookit().trustAllHost(url);
		/*try {
			result = HttpClientUtil.getInstance().connectGet(url);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//String result = request(url);
		System.out.println("reversePlaceSearch resulst : "+result);
		Place place = null;
		try {
			place = gson.fromJson(result, Place.class);
		} catch (Exception e) {
			throw new NetException(NetException.ERROR_JSONSYNTEX);
		}
		
		return place;
	}
	public String reversePlaceDetail(String reference){
		String query = "json" + "?reference="+reference+"&sensor=true&key=" +GOOGLE_PLACE_KEY +"&language=zh-CN"; 
		String url = PLACEDETAIL+ query;
		String result = null;
		//result = new HttpTookit().trustAllHost(url); 这个也是可以的
		try {
			result = HttpClientUtil.getInstance().connectGet(url);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//String result = request(url); 无法解决ssl问题
		PlaceDetail placeDetail = gson.fromJson(result, PlaceDetail.class);
		System.out.println("place detail is " + placeDetail);
		return result;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//PlaceService placeApi = new PlaceService();
		//placeApi.reversePlaceSearch(31.199037, 121.401017);
		//placeApi.reversePlaceDetail("CnRqAAAAO6TJaSv4GZFUIvdXIMf89zkSBGYYj5HhUJ3epa26fUedN-MOIiiuO50KgeezOp0i1fMVdY0qHw2IItRgiavrpItoRlxYHe1U8G_WCRlkALIHDdWfKOxz8OVXZlYZtfoSYGqE_Mz7qiJBTK-OdzrVRBIQSFfhTJB1gsJMEmU1JPuEDxoUCz-wHXVW4R2rlndAZw5gAQbArmc");
		//System.out.println(placeApi.request(testurl));
		
	}
	
}

class HttpTookit {

	// 信任所有证书请求
	public String trustAllHost(String Url) {
		final String tag = "TAG";
		String str = "";
		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance("TLS");
			ctx.init(null, new TrustManager[] { new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) {
				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[] {};
					//return null;
				}
			} }, null);
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());

		try {
			URL url = new URL(Url);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("GET");
			connection.setReadTimeout(20000);
			connection.setConnectTimeout(20000);
			connection.connect();
			OutputStream out = connection.getOutputStream();
			out.write(Url.getBytes());
			out.flush();
			out.close();
			Log.e(tag, "Response code :" + connection.getResponseCode());

			InputStream input = connection.getInputStream();
			InputStreamReader inputReader = new InputStreamReader(input);
			BufferedReader reader = new BufferedReader(inputReader);
			String inputLine = null;
			StringBuffer sb = new StringBuffer();
			while ((inputLine = reader.readLine()) != null) {
				sb.append(inputLine).append("\n");
			}
			System.out.println("sb==" + sb.toString());
			str = sb.toString();
			// EntityUtils.toString(connection.getre.getEntity(), "UTF-8");
			connection.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}
}
