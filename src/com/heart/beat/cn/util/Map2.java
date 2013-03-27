package com.heart.beat.cn.util;

public class Map2 {

	private static final double EARTH_RADIUS = 6378137.0;  
	/**
	 * 
	 * @param lat_a
	 * @param lng_a
	 * @param lat_b
	 * @param lng_b
	 * @return 计算出来的结果单位为千米
	 */
	public static double gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
	  double radLat1 = (lat_a * Math.PI / 180.0);
	  double radLat2 = (lat_b * Math.PI / 180.0);
	  double a = radLat1 - radLat2;
	  double b = (lng_a - lng_b) * Math.PI / 180.0;
	  double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
	             + Math.cos(radLat1) * Math.cos(radLat2)
	             * Math.pow(Math.sin(b / 2), 2)));
	  s = s * EARTH_RADIUS;
	  s = Math.round(s * 10000) / 10000;
	  return s;
	}

	/**
	 * 根据经纬度和半径计算经纬度范围
	 * @param raidus
	 * 单位米 return minLat,minLng,maxLat,maxLng
	 */
	public static double[] getAround(double lat, double lon, int raidus) {

		Double latitude = lat;
		Double longitude = lon;

		Double degree = (24901 * 1609) / 360.0;
		double raidusMile = raidus;

		Double dpmLat = 1 / degree;
		Double radiusLat = dpmLat * raidusMile;
		Double minLat = latitude - radiusLat;
		Double maxLat = latitude + radiusLat;

		Double mpdLng = degree * Math.cos(latitude * (Math.PI / 180));
		Double dpmLng = 1 / mpdLng;
		Double radiusLng = dpmLng * raidusMile;
		Double minLng = longitude - radiusLng;
		Double maxLng = longitude + radiusLng;
		return new double[] { minLat, minLng, maxLat, maxLng };
	}
	public static void main(String[] args) {
		double[] params = getAround(31.235518, 121.502177, 1000);
		for(double d:params)
			System.out.println(d);
		
	}
}
