package com.newgen.am.dto;

import javax.validation.Valid;

import lombok.Data;

@Data
public class ApprovalOrderLimitDTO {
	private OrderLimitDTO oldData;
	@Valid
	private OrderLimitDTO pendingData;
}
