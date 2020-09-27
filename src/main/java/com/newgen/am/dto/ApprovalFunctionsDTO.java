package com.newgen.am.dto;

import javax.validation.Valid;

import lombok.Data;

@Data
public class ApprovalFunctionsDTO {
	private FunctionsDTO oldData;
	@Valid
	private FunctionsDTO pendingData;
}
