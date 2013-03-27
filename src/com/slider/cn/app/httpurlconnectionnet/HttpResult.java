package com.slider.cn.app.httpurlconnectionnet;

import com.slider.cn.app.exception.NetException;

/**
 * 考虑到网络请求的封装成一个单例工具类。该工具类没有状态行。所以一个return无法同是返回多个信息，除非是对象.
 * 因为我考虑到返回200（或者200-299之间）并不能完全代表执行成功，其它的http状态码代表http请求错误。比如有的接口
 * 考虑成功时候一定要返回201，失败的时候一定是403这类需求。 所以说这里reponseCode保留有它很重要的意义
 * @author slider
 *
 */
public class HttpResult {
	public static final int NONE = -1;/* default -1 means nothing, just Initialization */
	public static final String LOCATION_HEADER = "location";
	
	private int reponseCode;/* the response code of http request */
	private String result;	/* the response message of http request */
	
	public HttpResult(){	
		reponseCode = NONE;
		result = null;
	}
	public HttpResult(int responseCode,String result){
		this.reponseCode = responseCode;
		this.result = result;
	}
	/**
	 * return true only that http-reponse-code is between 200 and 299
	 * @throws NetException 
	 */
	public boolean isCodeOk() throws NetException{
		if (String.valueOf(reponseCode).equals("404")) {
			throw new NetException(NetException.HTTP_404);
		}
		return String.valueOf(reponseCode).startsWith("2");
	}
	
	public int getReponseCode() {
		return reponseCode;
	}

	public void setReponseCode(int reponseCode) {
		this.reponseCode = reponseCode;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	
	@Override
	public String toString() {
		return "HttpResult [reponseCode=" + reponseCode + ", result=" + result
				+ "]";
	}
}
