package com.newgen.am.dto;

import javax.validation.Valid;

import lombok.Data;

@Data
public class ApprovalGeneralFeeDTO {
	private GeneralFeeDTO oldData;
	@Valid
	private GeneralFeeDTO pendingData;
}
