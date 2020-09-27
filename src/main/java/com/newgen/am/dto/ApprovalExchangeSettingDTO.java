package com.newgen.am.dto;

import javax.validation.Valid;

import lombok.Data;

@Data
public class ApprovalExchangeSettingDTO {
	private ExchangeSettingDTO oldData;
	@Valid
	private ExchangeSettingDTO pendingData;
}
