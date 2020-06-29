package com.newgen.am.dto;

import javax.validation.Valid;

public class ApprovalUpdateBrokerDTO {
	private BrokerDTO oldData;
	@Valid
	private UpdateBrokerDTO pendingData;
	public BrokerDTO getOldData() {
		return oldData;
	}
	public void setOldData(BrokerDTO oldData) {
		this.oldData = oldData;
	}
	public UpdateBrokerDTO getPendingData() {
		return pendingData;
	}
	public void setPendingData(UpdateBrokerDTO pendingData) {
		this.pendingData = pendingData;
	}
}
