package com.newgen.am.dto;

import javax.validation.Valid;

public class ApprovalUpdateUserDTO {
	private UserDTO oldData;
	@Valid
	private UpdateUserDTO pendingData;
	public UserDTO getOldData() {
		return oldData;
	}
	public void setOldData(UserDTO oldData) {
		this.oldData = oldData;
	}
	public UpdateUserDTO getPendingData() {
		return pendingData;
	}
	public void setPendingData(UpdateUserDTO pendingData) {
		this.pendingData = pendingData;
	}
}
