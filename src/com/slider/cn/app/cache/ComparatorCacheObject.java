package com.slider.cn.app.cache;

import java.util.Comparator;

import com.slider.cn.app.bean.response.ResponseBeat;

public class ComparatorCacheObject implements Comparator<ResponseBeat> {

	@Override
	public int compare(ResponseBeat arg0, ResponseBeat arg1) {
		return (Integer.valueOf(arg1.getId())-Integer.valueOf(arg0.getId()));
	}

}
