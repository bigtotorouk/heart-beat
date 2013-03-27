package com.slider.cn.app.httpurlconnectionnet;

import java.io.File;
/**
 * 上传文件类型：在使用httpurlConnection上传文件时候对文件的封装
 * 它可以包含该文件的许多信息：比如该文件的相信描述，拥有者等
 * @author ken
 *
 */
public class FileEntity{
	private String name; /* 这里应该默认'image' */
	private File value;  /* 这里是本地的文件名 */
	
	public FileEntity(String name, File value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public File getValue() {
		return value;
	}
	public void setValue(File value) {
		this.value = value;
	}
	
}
