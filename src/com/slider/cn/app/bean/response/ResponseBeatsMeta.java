package com.slider.cn.app.bean.response;

public class ResponseBeatsMeta {
	private int limit;
	private String next = null;
	private int offset;
	private String previous = null;
	private int total_count;
	
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
		return "Meta [limit=" + limit + ", next=" + next + ", offset=" + offset
				+ ", previous=" + previous + ", total_count=" + total_count
				+ "]";
	}
}
