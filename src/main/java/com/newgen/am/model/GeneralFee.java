package com.newgen.am.model;

import java.io.Serializable;

public class GeneralFee implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String processMethod;
	private long feeAmount;
	private long appliedDate;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProcessMethod() {
		return processMethod;
	}
	public void setProcessMethod(String processMethod) {
		this.processMethod = processMethod;
	}
	public long getFeeAmount() {
		return feeAmount;
	}
	public void setFeeAmount(long feeAmount) {
		this.feeAmount = feeAmount;
	}
	public long getAppliedDate() {
		return appliedDate;
	}
	public void setAppliedDate(long appliedDate) {
		this.appliedDate = appliedDate;
	}
}
