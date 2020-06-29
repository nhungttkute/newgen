package com.newgen.am.dto;

import javax.validation.Valid;

public class ApprovalUpdateDepartmentDTO {
	private DepartmentDTO oldData;
	@Valid
	private UpdateDepartmentDTO pendingData;
	public DepartmentDTO getOldData() {
		return oldData;
	}
	public void setOldData(DepartmentDTO oldData) {
		this.oldData = oldData;
	}
	public UpdateDepartmentDTO getPendingData() {
		return pendingData;
	}
	public void setPendingData(UpdateDepartmentDTO pendingData) {
		this.pendingData = pendingData;
	}
}
