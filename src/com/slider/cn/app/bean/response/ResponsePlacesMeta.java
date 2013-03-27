package com.slider.cn.app.bean.response;

public class ResponsePlacesMeta {
	private int limit;
	private String next;
	private int offset;
	private String previous;
	private int total_count;
	/*{"limit": 20, "next": null, "offset": 0, "previous": null, "total_count": 2}*/
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public String getNext() {
		return next;
	}
	public void setNext(String next) {
		this.next = next;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public String getPrevious() {
		return previous;
	}
	public void setPrevious(String previous) {
		this.previous = previous;
	}
	public int getTotal_count() {
		return total_count;
	}
	public void setTotal_count(int total_count) {
		this.total_count = total_count;
	}
	@Override
	public String toString() {
		return "ResponsePlacesMeta [limit=" + limit + ", next=" + next
				+ ", offset=" + offset + ", previous=" + previous
				+ ", total_count=" + total_count + "]";
	}
}
