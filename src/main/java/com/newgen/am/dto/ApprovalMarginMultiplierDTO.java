package com.newgen.am.dto;

import javax.validation.Valid;

public class ApprovalMarginMultiplierDTO {
	private MarginMultiplierDTO oldData;
	@Valid
	private MarginMultiplierDTO pendingData;
	public MarginMultiplierDTO getOldData() {
		return oldData;
	}
	public void setOldData(MarginMultiplierDTO oldData) {
		this.oldData = oldData;
	}
	public MarginMultiplierDTO getPendingData() {
		return pendingData;
	}
	public void setPendingData(MarginMultiplierDTO pendingData) {
		this.pendingData = pendingData;
	}
}
