package com.newgen.am.model;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

public class Exchange implements Serializable {
	private static final long serialVersionUID = 1L;
	@NotEmpty(message = "Required.")
	private String exchangeCode;
	@NotEmpty(message = "Required.")
	private String priceType;
	@NotEmpty(message = "Required.")
	private String processMethod;
	@Positive
	private long appliedDate;
	
	public String getExchangeCode() {
		return exchangeCode;
	}
	public void setExchangeCode(String exchangeCode) {
		this.exchangeCode = exchangeCode;
	}
	public String getPriceType() {
		return priceType;
	}
	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}
	public String getProcessMethod() {
		return processMethod;
	}
	public void setProcessMethod(String processMethod) {
		this.processMethod = processMethod;
	}
	public long getAppliedDate() {
		return appliedDate;
	}
	public void setAppliedDate(long appliedDate) {
		this.appliedDate = appliedDate;
	}
	
}
