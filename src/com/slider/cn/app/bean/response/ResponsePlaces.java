package com.slider.cn.app.bean.response;

import java.util.List;


public class ResponsePlaces {
	private ResponsePlacesMeta meta;
	private List<ResponsePlace> objects;
	public ResponsePlacesMeta getMeta() {
		return meta;
	}
	public void setMeta(ResponsePlacesMeta meta) {
		this.meta = meta;
	}
	public List<ResponsePlace> getObjects() {
		return objects;
	}
	public void setObjects(List<ResponsePlace> objects) {
		this.objects = objects;
	}
	@Override
	public String toString() {
		return "ResponsePlaces [meta=" + meta + ", objects=" + objects + "]";
	}
	
}
