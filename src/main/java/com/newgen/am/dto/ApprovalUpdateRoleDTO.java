package com.newgen.am.dto;

import javax.validation.Valid;

public class ApprovalUpdateRoleDTO {
	private RoleDTO oldData;
	@Valid
	private UpdateRoleDTO pendingData;
	public RoleDTO getOldData() {
		return oldData;
	}
	public void setOldData(RoleDTO oldData) {
		this.oldData = oldData;
	}
	public UpdateRoleDTO getPendingData() {
		return pendingData;
	}
	public void setPendingData(UpdateRoleDTO pendingData) {
		this.pendingData = pendingData;
	}
}
