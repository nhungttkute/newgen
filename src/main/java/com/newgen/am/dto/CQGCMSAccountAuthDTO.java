package com.newgen.am.dto;

public class CQGCMSAccountAuthDTO {
	private long accountId;
	private String userId;
	private boolean isViewOnly;
	public long getAccountId() {
		return accountId;
	}
	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public boolean isViewOnly() {
		return isViewOnly;
	}
	public void setViewOnly(boolean isViewOnly) {
		this.isViewOnly = isViewOnly;
	}
}
