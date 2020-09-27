package com.newgen.am.dto;

import javax.validation.Valid;

import lombok.Data;

@Data
public class ApprovalUpdateUserDTO {
	private UserDTO oldData;
	@Valid
	private UpdateUserDTO pendingData;
}
