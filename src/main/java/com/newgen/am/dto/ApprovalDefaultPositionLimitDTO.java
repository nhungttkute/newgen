package com.newgen.am.dto;

import javax.validation.Valid;

import lombok.Data;

@Data
public class ApprovalDefaultPositionLimitDTO {
	private DefaultPositionLimitDTO oldData;
	@Valid
	private DefaultPositionLimitDTO pendingData;
}
