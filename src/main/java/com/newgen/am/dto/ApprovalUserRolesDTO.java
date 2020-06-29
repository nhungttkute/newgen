package com.newgen.am.dto;

import javax.validation.Valid;

public class ApprovalUserRolesDTO {
	private UserRolesDTO oldData;
	@Valid
	private UserRolesDTO pendingData;
	public UserRolesDTO getOldData() {
		return oldData;
	}
	public void setOldData(UserRolesDTO oldData) {
		this.oldData = oldData;
	}
	public UserRolesDTO getPendingData() {
		return pendingData;
	}
	public void setPendingData(UserRolesDTO pendingData) {
		this.pendingData = pendingData;
	}
}
