package com.newgen.am.dto;

public class DefaultSettingDTO {
	private int orderLimit;
    private int defaultPositionLimit;
	public int getOrderLimit() {
		return orderLimit;
	}
	public void setOrderLimit(int orderLimit) {
		this.orderLimit = orderLimit;
	}
	public int getDefaultPositionLimit() {
		return defaultPositionLimit;
	}
	public void setDefaultPositionLimit(int defaultPositionLimit) {
		this.defaultPositionLimit = defaultPositionLimit;
	}
}
