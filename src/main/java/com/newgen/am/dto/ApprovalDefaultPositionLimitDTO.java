package com.newgen.am.dto;

import javax.validation.Valid;

public class ApprovalDefaultPositionLimitDTO {
	private DefaultPositionLimitDTO oldData;
	@Valid
	private DefaultPositionLimitDTO pendingData;
	public DefaultPositionLimitDTO getOldData() {
		return oldData;
	}
	public void setOldData(DefaultPositionLimitDTO oldData) {
		this.oldData = oldData;
	}
	public DefaultPositionLimitDTO getPendingData() {
		return pendingData;
	}
	public void setPendingData(DefaultPositionLimitDTO pendingData) {
		this.pendingData = pendingData;
	}
}
