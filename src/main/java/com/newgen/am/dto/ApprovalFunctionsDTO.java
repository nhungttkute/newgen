package com.newgen.am.dto;

import javax.validation.Valid;

public class ApprovalFunctionsDTO {
	private FunctionsDTO oldData;
	@Valid
	private FunctionsDTO pendingData;
	public FunctionsDTO getOldData() {
		return oldData;
	}
	public void setOldData(FunctionsDTO oldData) {
		this.oldData = oldData;
	}
	public FunctionsDTO getPendingData() {
		return pendingData;
	}
	public void setPendingData(FunctionsDTO pendingData) {
		this.pendingData = pendingData;
	}
}
