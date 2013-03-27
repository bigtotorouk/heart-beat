package com.slider.cn.app.google.place.resource;

import java.util.HashMap;
import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.heart.beat.cn.util.Map2;
import com.slider.cn.app.bean.response.ResponsePlaces;
import com.slider.cn.app.exception.NetException;
import com.slider.cn.app.google.place.BeatPlace;
import com.slider.cn.app.httpurlconnectionnet.HttpJsonUtil;
import com.slider.cn.app.httpurlconnectionnet.HttpResult;
import com.slider.cn.app.httpurlconnectionnet.Util;

public class PlaceResource {
	private static final String POST_PLACE = "http://192.168.1.66/api/v1/place/?format=json";
	
	private static final String PLACE_URL = "http://192.168.1.66/api/v1/place/964e32ace63221a2fcb98769d9c881c4550768f9/";
	private static final String GET_PLACES = "http://192.168.1.66/api/v1/place/";
	
	private Gson gson;
	private String status = "";
	private Context mContext;
	
	public PlaceResource(Context context){
		mContext = context;
		gson = new Gson();
	}
	public String getStatus(){
		return this.status;
	}

	public ResponsePlaces getPlaces(double lat,double lon,int raidus) throws NetException{
		double[] ds = Map2.getAround(lat, lon, raidus);
		String lng1 = String.valueOf(ds[3]);
		String lat1 = String.valueOf(ds[2]);
		String lng2 = String.valueOf(ds[1]);
		String lat2 = String.valueOf(ds[0]);
		
		ResponsePlaces places = null;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("lng__lt", lng1);
		params.put("lat__lt", lat1);
		params.put("lng__gt", lng2);
		params.put("lat__gt", lat2);
		String url = GET_PLACES +"?"+Util.encodeParams(params) +"&format=json";
		HttpJsonUtil http  = HttpJsonUtil.getInstance();
		try {
			HttpResult response = http.connectGet(url,mContext,true);
			if(response!=null&&response.isCodeOk()){
				
					places = gson.fromJson(response.getResult(), ResponsePlaces.class);
			}
			System.out.println("getPlaces is "+response);
		} catch (JsonSyntaxException e) {
			throw new NetException(NetException.ERROR_JSONSYNTEX);
		}
		return places;
	}
	/**
	 * 因为要用到postPlace的url。所以这里不返回boolean，直接返回url
	 * @param beatPlace
	 * @return
	 * @throws NetException 
	 */
	public String postPlace(BeatPlace beatPlace) throws NetException{
		/*PlaceService placeApi = new PlaceService();
		Place places = placeApi.reversePlaceSearch(31.199037, 121.401017);
		if(places.getResults().size()<1)
			return;
		PlaceMeta placeMeta = places.getResults().get(0)*/;
		/*BeatPlace beatPlace = new BeatPlace();
		beatPlace.setPlaces_id(placeMeta.getId());
		beatPlace.setPlaces_ref(placeMeta.getReference());
		beatPlace.setCity("Shanghai");
		beatPlace.setCountry("CN");
		beatPlace.setLat(31.199037);
		beatPlace.setLng(121.401017);
		beatPlace.setCoupons("there has some coupons");
		beatPlace.setLocation(placeMeta.getVicinity());
		beatPlace.setName(placeMeta.getName());*/
		
		String json = gson.toJson(beatPlace);
		HttpJsonUtil http = HttpJsonUtil.getInstance();
		HttpResult response = http.connectPost(POST_PLACE,mContext, json);
		System.out.println("post place is "+response);
		if(response!=null&&response.isCodeOk())
			return response.getResult();
		else 
			return null;
	}
	
	public static void main(String[] args) {
		//PlaceResource placeApi = new PlaceResource();
		//placeApi.postPlace(null);
	}
}
