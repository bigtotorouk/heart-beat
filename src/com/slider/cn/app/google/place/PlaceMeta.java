package com.slider.cn.app.google.place;

import java.util.List;

public class PlaceMeta {
	private Geometry geometry;
	private String icon; //"http://maps.gstatic.com/mapfiles/place_api/icons/travel_agent-71.png";
	private String id;  //"ddd678ac0a16d83fdc25500ccb3d6aa27b7f5548";
	private String name;  //"Darling Harbour";
	private String reference;  //"CnRtAAAAHnR2IIZhq0x1uAhaLwt5_N0m4PlzRvJ1GFH_dRc7qlyku3WKYzhDLANGTb9XVSNQeSDAJraaDwRSaC2Vp1AMoxjsw_PZlUvxdmbzRGcgifm_h7MUfax0MdB_lZHXA86bz3S7xwWd_-3QmkNIdwltkhIQE24XzGVaSQycjyCLqb3wfxoUz8PQia6T73Yk-_Er3Tvo3wlPZfw";
	private List<String> types;	
	private String vicinity; //具体的address
	public Geometry getGeometry() {
		return geometry;
	}
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public List<String> getTypes() {
		return types;
	}
	public void setTypes(List<String> types) {
		this.types = types;
	}
	public String getVicinity() {
		return vicinity;
	}
	public void setVicinity(String vicinity) {
		this.vicinity = vicinity;
	}
	@Override
	public String toString() {
		return "PlaceMeta {geometry=" + geometry + ", icon=" + icon + ", id="
				+ id + ", name=" + name + ", reference=" + reference
				+ ", types=" + types + ", vicinity=" + vicinity + "}";
	}
}
