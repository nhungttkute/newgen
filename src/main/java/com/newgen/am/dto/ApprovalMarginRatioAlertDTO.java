package com.newgen.am.dto;

import javax.validation.Valid;

import lombok.Data;

@Data
public class ApprovalMarginRatioAlertDTO {
	private MarginRatioAlertDTO oldData;
	@Valid
	private MarginRatioAlertDTO pendingData;
}
