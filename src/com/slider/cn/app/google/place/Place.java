package com.slider.cn.app.google.place;

import java.util.List;
/**
 * java bean for place search api 
 * @author slider
 *
 */
public class Place {
	/**
	 * status：
	 *"OK" 表示未发生任何错误；已成功检测到相应位置，并且至少传回一个结果。
	 *"ZERO_RESULTS" 表示搜索成功，但未传回结果。如果在远程位置向搜索传递一个 latlng，则可能会发生这种情况。
	 *"OVER_QUERY_LIMIT" 表示您超出了配额。
	 *"REQUEST_DENIED" 表示您的请求被拒绝，通常是由于缺少 sensor 参数。
	 *"INVALID_REQUEST" 通常表示缺少必填的查询参数（location 或 radius）。
	 */
	private String status;
	/**
	 * html_attributions : 包含有关此商家信息的属性集（必须呈现给用户）
	 */
	private List<String> html_attributions;
	private List<PlaceMeta> results;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<String> getHtml_attributions() {
		return html_attributions;
	}
	public void setHtml_attributions(List<String> html_attributions) {
		this.html_attributions = html_attributions;
	}
	public List<PlaceMeta> getResults() {
		return results;
	}
	public void setResults(List<PlaceMeta> results) {
		this.results = results;
	}
	@Override
	public String toString() {
		return "Place {status=" + status + ", html_attributions="
				+ html_attributions + ", results=" + results + "}";
	}
	
	
}
