package com.newgen.am.dto;

import javax.validation.Valid;

public class ApprovalChangeGroupDTO {
	private ChangeGroupDTO oldData;
	@Valid
	private ChangeGroupDTO pendingData;
	public ChangeGroupDTO getOldData() {
		return oldData;
	}
	public void setOldData(ChangeGroupDTO oldData) {
		this.oldData = oldData;
	}
	public ChangeGroupDTO getPendingData() {
		return pendingData;
	}
	public void setPendingData(ChangeGroupDTO pendingData) {
		this.pendingData = pendingData;
	}
}
