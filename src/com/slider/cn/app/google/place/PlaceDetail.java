package com.slider.cn.app.google.place;

import java.util.List;
/**
 * java bean for place detail api 
 * @author slider
 *
 */
public class PlaceDetail {

	private String status;
	private List<String> html_attributions;
	private PlaceDetailMeta result;
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
	public PlaceDetailMeta getResult() {
		return result;
	}
	public void setResult(PlaceDetailMeta result) {
		this.result = result;
	}
	@Override
	public String toString() {
		return "PlaceDetail {status=" + status + ", html_attributions="
				+ html_attributions + ", result=" + result + "}";
	}
	
}
