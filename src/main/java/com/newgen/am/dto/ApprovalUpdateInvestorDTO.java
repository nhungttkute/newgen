package com.newgen.am.dto;

import javax.validation.Valid;

public class ApprovalUpdateInvestorDTO {
	private InvestorDTO oldData;
	@Valid
	private UpdateInvestorDTO pendingData;
	public InvestorDTO getOldData() {
		return oldData;
	}
	public void setOldData(InvestorDTO oldData) {
		this.oldData = oldData;
	}
	public UpdateInvestorDTO getPendingData() {
		return pendingData;
	}
	public void setPendingData(UpdateInvestorDTO pendingData) {
		this.pendingData = pendingData;
	}
}
