package com.newgen.am.dto;

import javax.validation.Valid;

public class ApprovalMarginRatioAlertDTO {
	private MarginRatioAlertDTO oldData;
	@Valid
	private MarginRatioAlertDTO pendingData;
	public MarginRatioAlertDTO getOldData() {
		return oldData;
	}
	public void setOldData(MarginRatioAlertDTO oldData) {
		this.oldData = oldData;
	}
	public MarginRatioAlertDTO getPendingData() {
		return pendingData;
	}
	public void setPendingData(MarginRatioAlertDTO pendingData) {
		this.pendingData = pendingData;
	}
}
