package com.newgen.am.dto;

import javax.validation.Valid;

public class ApprovalOrderLimitDTO {
	private OrderLimitDTO oldData;
	@Valid
	private OrderLimitDTO pendingData;
	public OrderLimitDTO getOldData() {
		return oldData;
	}
	public void setOldData(OrderLimitDTO oldData) {
		this.oldData = oldData;
	}
	public OrderLimitDTO getPendingData() {
		return pendingData;
	}
	public void setPendingData(OrderLimitDTO pendingData) {
		this.pendingData = pendingData;
	}
}
