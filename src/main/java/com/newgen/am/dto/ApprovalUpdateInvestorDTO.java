package com.newgen.am.dto;

import java.io.Serializable;

import javax.validation.Valid;

public class ApprovalUpdateInvestorDTO implements Serializable {
	private static final long serialVersionUID = 1L;
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
