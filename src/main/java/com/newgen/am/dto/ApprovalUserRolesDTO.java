package com.newgen.am.dto;

import javax.validation.Valid;

import lombok.Data;

@Data
public class ApprovalUserRolesDTO {
	private UserRolesDTO oldData;
	@Valid
	private UserRolesDTO pendingData;
}
