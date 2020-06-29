package com.newgen.am.dto;

public class ApprovalDefaultSettingDTO {
	private DefaultSettingDTO oldData;
	private DefaultSettingDTO pendingData;
	public DefaultSettingDTO getOldData() {
		return oldData;
	}
	public void setOldData(DefaultSettingDTO oldData) {
		this.oldData = oldData;
	}
	public DefaultSettingDTO getPendingData() {
		return pendingData;
	}
	public void setPendingData(DefaultSettingDTO pendingData) {
		this.pendingData = pendingData;
	}
}
