package com.slider.cn.app.bean.response;

import java.util.List;

public class ResponseIds {
	private List<String> ids;

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}

	@Override
	public String toString() {
		return "ResponseIds [ids=" + ids + "]";
	}
}
