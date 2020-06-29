package com.newgen.am.dto;

import javax.validation.Valid;

public class ApprovalCommodityFeesDTO {
	private CommodityFeesDTO oldData;
	@Valid
	private CommodityFeesDTO pendingData;
	public CommodityFeesDTO getOldData() {
		return oldData;
	}
	public void setOldData(CommodityFeesDTO oldData) {
		this.oldData = oldData;
	}
	public CommodityFeesDTO getPendingData() {
		return pendingData;
	}
	public void setPendingData(CommodityFeesDTO pendingData) {
		this.pendingData = pendingData;
	}
}
