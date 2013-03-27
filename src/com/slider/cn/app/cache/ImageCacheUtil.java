package com.slider.cn.app.cache;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import android.graphics.Bitmap;
import android.os.Environment;
/**
 * 图片缓存类工具类。该类把图片缓存到本地
 * @author slider
 *
 */
public class ImageCacheUtil {
	private DiskLruCache mDiskCache;
	private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
	private static final String DISK_CACHE_SUBDIR = Environment.getExternalStorageDirectory() + "/heartbeat/cachePics/";
	
	//这里把缓存图片的工具类封装成一个单例模式
	private static ImageCacheUtil instance = null;
	
	private ImageCacheUtil(){
		mDiskCache = DiskLruCache.openCache(new File(DISK_CACHE_SUBDIR), DISK_CACHE_SIZE);
	}
	public static synchronized ImageCacheUtil getInstance(){
		if(instance==null){
			instance = new ImageCacheUtil();
		}
		return instance;
	}
	
	/**
	 * 保存bitmap资源到本地. 前提要求sd卡
	 * @param url
	 * @param bitmap
	 * @throws IOException
	 */
	public void saveFile(String url, Bitmap bitmap){ 
		if(mDiskCache == null)
			return;
		String key = MD5.encodeMD5String(url);
	    if (!mDiskCache.containsKey(key)) {
	        mDiskCache.put(key, bitmap);
	    }
    } 
	
	/**
	 * 根据url获取存储本地sd卡的bitmap资源。如果sd上没有，则返回null
	 * @param url
	 * @return
	 */
	public Bitmap getImageUrl(String url) {
		/* 不知道为什么有时候会出现: mDiskCache null */
		if(mDiskCache==null)
			return null;
		String key = MD5.encodeMD5String(url);
		return mDiskCache.get(key);
	}
}
/**
 * covert url to a unique string
 * @author slider
 *
 */
class MD5 {
	public static String encodeMD5String(String str) {
		return encode(str, "MD5");
	}

	private static String encode(String str, String method) {
		MessageDigest md = null;
		String dstr = null;
		try {
			md = MessageDigest.getInstance(method);
			md.update(str.getBytes());
			dstr = new BigInteger(1, md.digest()).toString(16);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dstr;
	}
}
