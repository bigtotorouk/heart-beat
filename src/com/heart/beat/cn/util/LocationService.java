package com.heart.beat.cn.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.map.test.xml.SAXHandler;

public class LocationService {

	/**
	 * 借助Google MAP 通过用户当前经纬度 获得用户当前城市
	 */
	static final String GOOGLE_MAPS_API_KEY = "0835yI5X5qhWP3POFjEeQhZwN1xgAt9rmv5688w";

	private Context mContext;
	private LocationManager locationManager;
	private Location currentLocation;

	
	private String city = "not found";
	private String address = "not found";
	private String countryCode = "CN";

	public LocationService(Context context) {
		mContext = context;
		
		
	}

	public String getCity() {
		return city;
	}
	
	public String getAddress(){
		return address;
	}
	
	public String getCountryCode(){
		return countryCode;
	}
	/**
	 * get the current-local Location
	 * @return
	 */
	public Location getLocation(){
		return updateLocation();
		/*this.locationManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
		currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(currentLocation == null)
			currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		return currentLocation;*/
	}
	
	/* 获取经纬度信息 */
	private Location updateLocation(){
		locationManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
        Criteria crit = new Criteria();
        String provider = locationManager.getBestProvider(crit, false);
        currentLocation = locationManager.getLastKnownLocation(provider);
        return currentLocation;
	}
	
	/**
	 * parse the location, the you could call getCity() or getAddress()
	 * @param location
	 */
	public void reverseGeocode(Location location) {
		// http://maps.google.com/maps/geo?q=40.714224,-73.961452&output=json&oe=utf8&sensor=true_or_false&key=your_api_key
		HttpURLConnection connection = null;
		URL serverAddress = null;
		if(location==null)
			location = getLocation();
		try {
			// build the URL using the latitude & longitude you want to lookup
			// NOTE: I chose XML return format here but you can choose something
			serverAddress = new URL("http://maps.google.com/maps/geo?q="
					+ Double.toString(location.getLatitude()) + ","
					+ Double.toString(location.getLongitude())
					+ "&output=xml&oe=utf8&sensor=true&key="
					+ GOOGLE_MAPS_API_KEY);
			System.out.println("serverAddress is "+serverAddress);
			connection = null;
			// Set up the initial connection
			connection = (HttpURLConnection) serverAddress.openConnection();
			connection.setRequestMethod("GET");
			//connection.setDoOutput(true);
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			connection.connect();

			try {
				//InputStreamReader isr = new InputStreamReader(connection.getInputStream());
				//String result = read(connection.getInputStream());
				//System.out.println("result: " + result);

				SAXParserFactory factory=SAXParserFactory.newInstance();
				InputSource is = new InputSource(connection.getInputStream());
				XMLReader reader;
				SAXHandler saxHandler = new SAXHandler();
				try {
					reader = factory.newSAXParser().getXMLReader();
					reader.setContentHandler(saxHandler); 
					is.setEncoding("utf-8");
					reader.parse(is); 
				} catch (Exception e) {
					e.printStackTrace();
				} 
                
                city = saxHandler.getCity();
                address = saxHandler.getAddress();
                countryCode = saxHandler.getCountryCode();
				System.out.println("address: "+saxHandler.getAddress());
				System.out.println("city: "+saxHandler.getCity());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("GetCity.reverseGeocode()" + ex);
		}

	}
	
	private  String read(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(in), 1024);
        for (String line = r.readLine(); line != null; line = r.readLine()) {
            sb.append(line);
        }
        in.close();
        return sb.toString();
    }
	
	
}
