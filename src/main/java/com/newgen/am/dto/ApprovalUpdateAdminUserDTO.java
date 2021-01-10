package com.newgen.am.dto;

import javax.validation.Valid;

import lombok.Data;

@Data
public class ApprovalUpdateAdminUserDTO {
	private AdminUserDTO oldData;
	@Valid
	private UpdateAdminUserDTO pendingData;
}
