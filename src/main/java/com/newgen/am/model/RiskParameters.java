package com.newgen.am.model;

import com.newgen.am.validation.ValidUpdateStringField;

public class RiskParameters {
	@ValidUpdateStringField
	private String newPositionOrderLock;
	@ValidUpdateStringField
	private String orderLock;
	@ValidUpdateStringField
	private String marginWithdrawalLock;
	
	public String getNewPositionOrderLock() {
		return newPositionOrderLock;
	}
	public void setNewPositionOrderLock(String newPositionOrderLock) {
		this.newPositionOrderLock = newPositionOrderLock;
	}
	public String getOrderLock() {
		return orderLock;
	}
	public void setOrderLock(String orderLock) {
		this.orderLock = orderLock;
	}
	public String getMarginWithdrawalLock() {
		return marginWithdrawalLock;
	}
	public void setMarginWithdrawalLock(String marginWithdrawalLock) {
		this.marginWithdrawalLock = marginWithdrawalLock;
	}
	
}
