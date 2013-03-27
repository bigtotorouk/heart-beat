package com.slider.cn.app.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;

import com.google.gson.Gson;
import com.slider.cn.app.bean.response.ResponseBeat;
import com.slider.cn.app.bean.response.ResponseBeats;
import com.slider.cn.app.httpurlconnectionnet.SharedStore;
/**
 * 本地缓存工具类。该类用户存取缓存数据(这里是json字符串)和一些处理
 * 对象(内存)<----->json串(本地)
 * @author slider
 *
 */
public class TxtCacheUtil extends SharedStore{
	private static final int MAX_LENGTH = 50;
	public static final String CACHE_FRIENDBEATS = "cache_friendbeats";
	public static final String CACHE_HOTBEATS = "cache_hotbeats";
	public static final String CACHE_NEWBEATS = "cache_newsbeats";
	
	private String which;
	private Comparator<ResponseBeat> comparator;
	private ResponseBeats mObject;
	private Gson gson;
	/**
	 * 三个构造函数
	 */
	public TxtCacheUtil(Context context,String which){
		gson = new Gson();
		this.which = which; 
		String json = getValue(context, which);
		if (json!=null&&!json.equals("")) {
			mObject = gson.fromJson(json, ResponseBeats.class);
		}
		
	}
	public TxtCacheUtil(ResponseBeats object){
		gson = new Gson();
		initData(object);
		
	}
	private void initData(ResponseBeats object){
		if(object.getObjects().size()>MAX_LENGTH){
			mObject = new ResponseBeats();
			List<ResponseBeat> data = new ArrayList<ResponseBeat>(MAX_LENGTH); 
			for(int i = 0;i<MAX_LENGTH;i++)
				data.add(object.getObjects().get(i));
			mObject.setObjects(data);
				
		}else {
			mObject = object;
		}
	}
	// -------------------------------------------------------------------------
	public ResponseBeats getObject() {
		return mObject;
	}
	public void saveObject(Context context){
		if (mObject==null) {
			return;
		}
		String json = gson.toJson(mObject);
		save(context, which, json);
	}

	/**
	 * 因为考虑到传递进去的object是有顺序的，所以一次没有添加进入，后面的就全都不能添加进去了
	 * @param object
	 */
	public void setObject(ResponseBeats object) {
		if(mObject==null){
			initData(object);
			return;
		}
		List<ResponseBeat> temps = object.getObjects();
		for(ResponseBeat e:temps){
			//容器未满
			if(mObject.getObjects().size()<MAX_LENGTH){
				//add
				if(!isConains(e))
					mObject.getObjects().add(e);
				
			}else{
				//replace
				int minIndex = minIndex();
				if(!isConains(mObject.getObjects().get(minIndex))&&Integer.valueOf(mObject.getObjects().get(minIndex).getId())<Integer.valueOf(e.getId()))
					mObject.getObjects().set(minIndex(),e);
				else
					break;
			}
			
		}
		sort();
	}
	private int minIndex(){
		int index = 0;
		ResponseBeat o = mObject.getObjects().get(0);
		for(int i=1;i<mObject.getObjects().size();i++){
			if(Integer.valueOf(o.getId())>Integer.valueOf(mObject.getObjects().get(i).getId())){
				o = mObject.getObjects().get(i);
				index = i;
			}
		}
		return index;
	}
	private boolean isConains(ResponseBeat o){
		for(ResponseBeat e:mObject.getObjects()){
			if(o.getId().equals(e.getId()))
				return true;
		}
		return false;
	}
	/**
	 * 获取缓存格式的json字符串
	 * @return
	 */
	public String getJsonStr(){
		if(mObject==null)
			return null;
		return gson.toJson(mObject);
	}
	/**
	 * 缓存数据排序
	 */
	public void sort(){
		if(comparator==null){
			comparator = new ComparatorCacheObject();
		}
		Collections.sort(mObject.getObjects(),comparator);
	}
	
	
	
	public static void main(String[] args) {
		/*CacheObject data1 = new CacheObject();
		List<CaseObject> objects = new ArrayList<CaseObject>();
		CaseObject object;
		for(int i = 0;i<20;i++){
			object = new CaseObject((int)(Math.random()*MAX_LENGTH));
			objects.add(object);
		}
		data1.setData(objects);
		CacheUtil cache = new CacheUtil(data1);
		System.out.println("data1  ");
		for(CaseObject e:cache.getObject().getObjects())
			System.out.println(e);
		
		CacheObject data2 = new CacheObject();
		int num = 30;
		List<CaseObject> objects2 = new ArrayList<CaseObject>();
		for(int i = 0;i<num;i++){
			object = new CaseObject((int)(Math.random()*60));
			objects2.add(object);
		}
		
		data2.setData(objects2);
		Collections.sort(objects2,new ComparatorCacheObject());
		System.out.println("data2  ");
		for(CaseObject e:objects2)
			System.out.println(e);
		
		
		cache.setObject(data2);//-----------------------
		cache.sort();
		System.out.println("after set size"+cache.getObject().getObjects().size());
		for(CaseObject e:cache.getObject().getObjects())
			System.out.println(e);*/
		
	}
}
