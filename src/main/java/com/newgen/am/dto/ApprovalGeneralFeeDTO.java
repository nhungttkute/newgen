package com.newgen.am.dto;

import javax.validation.Valid;

public class ApprovalGeneralFeeDTO {
	private GeneralFeeDTO oldData;
	@Valid
	private GeneralFeeDTO pendingData;
	public GeneralFeeDTO getOldData() {
		return oldData;
	}
	public void setOldData(GeneralFeeDTO oldData) {
		this.oldData = oldData;
	}
	public GeneralFeeDTO getPendingData() {
		return pendingData;
	}
	public void setPendingData(GeneralFeeDTO pendingData) {
		this.pendingData = pendingData;
	}
}
