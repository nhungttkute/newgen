package com.newgen.am.dto;

import javax.validation.Valid;

import lombok.Data;

@Data
public class ApprovalMarginMultiplierDTO {
	private MarginMultiplierDTO oldData;
	@Valid
	private MarginMultiplierDTO pendingData;
}
