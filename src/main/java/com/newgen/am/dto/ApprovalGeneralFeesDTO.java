package com.newgen.am.dto;

import javax.validation.Valid;

public class ApprovalGeneralFeesDTO {
	private GeneralFeesDTO oldData;
	@Valid
	private GeneralFeesDTO pendingData;
	public GeneralFeesDTO getOldData() {
		return oldData;
	}
	public void setOldData(GeneralFeesDTO oldData) {
		this.oldData = oldData;
	}
	public GeneralFeesDTO getPendingData() {
		return pendingData;
	}
	public void setPendingData(GeneralFeesDTO pendingData) {
		this.pendingData = pendingData;
	}
}
