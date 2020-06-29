package com.newgen.am.dto;

import javax.validation.Valid;

public class ApprovalExchangeSettingDTO {
	private ExchangeSettingDTO oldData;
	@Valid
	private ExchangeSettingDTO pendingData;
	public ExchangeSettingDTO getOldData() {
		return oldData;
	}
	public void setOldData(ExchangeSettingDTO oldData) {
		this.oldData = oldData;
	}
	public ExchangeSettingDTO getPendingData() {
		return pendingData;
	}
	public void setPendingData(ExchangeSettingDTO pendingData) {
		this.pendingData = pendingData;
	}
}
