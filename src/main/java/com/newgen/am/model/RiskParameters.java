package com.newgen.am.model;

import java.io.Serializable;

import com.newgen.am.validation.ValidUpdateStringField;

public class RiskParameters implements Serializable {
	private static final long serialVersionUID = 1L;
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
