package com.newgen.am.dto;

import javax.validation.Valid;

import lombok.Data;

@Data
public class ApprovalUpdateRoleDTO {
	private RoleDTO oldData;
	@Valid
	private UpdateRoleDTO pendingData;
}
