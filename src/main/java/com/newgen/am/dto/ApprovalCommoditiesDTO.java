package com.newgen.am.dto;

import javax.validation.Valid;

public class ApprovalCommoditiesDTO {
	private String type;
	private CommoditiesDTO oldData;
	@Valid
	private CommoditiesDTO pendingData;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public CommoditiesDTO getOldData() {
		return oldData;
	}
	public void setOldData(CommoditiesDTO oldData) {
		this.oldData = oldData;
	}
	public CommoditiesDTO getPendingData() {
		return pendingData;
	}
	public void setPendingData(CommoditiesDTO pendingData) {
		this.pendingData = pendingData;
	}
}
