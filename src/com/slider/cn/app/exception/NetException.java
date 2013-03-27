package com.slider.cn.app.exception;

public class NetException extends Exception {
	private static final long serialVersionUID = 1L;
	/* define some type of error */
	public static final String ERROR_URL = "Url fomat error";
	public static final String ERROR_SERVER = "Server Request Error";
	public static final String ERROR_COOKIE = "You should login first";
	public static final String ERROR_JSONSYNTEX = "Parse data-json error";
	
	public static final String HTTP_404 = "Service not found";
	
	public NetException(String msg){
		super(msg);
	}
	/* for test */
	public static void main(String[] args) {
		try {
			throw new NetException("no more message");
		} catch (NetException e) {
			System.out.println(e.getMessage());
		}
	}
}
