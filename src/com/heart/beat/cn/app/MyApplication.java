package com.heart.beat.cn.app;

import com.slider.cn.app.bean.response.ResponseProfile;

import android.app.Application;
/**
 * 定义程序所需要的一些全局变量（整个应用需要的数据,这是之所以没有选择序列化传递profile对象的原因）
 * @author slider
 *
 */
public class MyApplication extends Application {
	private ResponseProfile profile;

	public ResponseProfile getProfile() {
		return profile;
	}

	public void setProfile(ResponseProfile profile) {
		this.profile = profile;
	}
}
