package com.newgen.am.dto;

import java.io.Serializable;

import javax.validation.Valid;

public class ApprovalUpdateBrokerDTO implements Serializable {
	private static final long serialVersionUID = 1L;
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
