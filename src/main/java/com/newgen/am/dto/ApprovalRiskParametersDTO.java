package com.newgen.am.dto;

import javax.validation.Valid;

public class ApprovalRiskParametersDTO {
	private RiskParametersDTO oldData;
	@Valid
	private RiskParametersDTO pendingData;
	public RiskParametersDTO getOldData() {
		return oldData;
	}
	public void setOldData(RiskParametersDTO oldData) {
		this.oldData = oldData;
	}
	public RiskParametersDTO getPendingData() {
		return pendingData;
	}
	public void setPendingData(RiskParametersDTO pendingData) {
		this.pendingData = pendingData;
	}
}
