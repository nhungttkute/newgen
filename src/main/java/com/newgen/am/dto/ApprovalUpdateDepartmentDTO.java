package com.newgen.am.dto;

import javax.validation.Valid;

import lombok.Data;

@Data
public class ApprovalUpdateDepartmentDTO {
	private DepartmentDTO oldData;
	@Valid
	private UpdateDepartmentDTO pendingData;
}
