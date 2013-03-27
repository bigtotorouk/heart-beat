package com.slider.cn.app.bean.response;

import java.util.List;

public class ResponseComments {
	private List<ResponseComment> objects;

	public List<ResponseComment> getObjects() {
		return objects;
	}

	public void setObjects(List<ResponseComment> objects) {
		this.objects = objects;
	}

	@Override
	public String toString() {
		return "ResponseComments [objects=" + objects + "]";
	}
	
}
