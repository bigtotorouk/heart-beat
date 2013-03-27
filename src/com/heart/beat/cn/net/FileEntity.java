package com.heart.beat.cn.net;

import java.io.File;
/**
 * 上传文件类型：在使用httpurlConnection上传文件时候对文件的封装
 * 它可以包含该文件的许多信息：比如该文件的相信描述，拥有者等
 * @author ken
 *
 */
class FileEntity{
	private String name;
	private File value;
	
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
