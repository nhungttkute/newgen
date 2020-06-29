package com.newgen.am.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import com.newgen.am.validation.LengthGroup;

public class GeneralFeeDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	@NotEmpty(message = "Required.")
	@Size(max = 100, message = "Invalid format.", groups = LengthGroup.class)
	private String name;
	@NotEmpty(message = "Required.")
	@Size(max = 20, message = "Invalid format.", groups = LengthGroup.class)
	private String processMethod;
	@Positive
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
