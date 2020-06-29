package com.newgen.am.dto;

import javax.validation.Valid;

public class ApprovalUpdateMemberDTO {
	private MemberDTO oldData;
	@Valid
	private UpdateMemberDTO pendingData;
	public MemberDTO getOldData() {
		return oldData;
	}
	public void setOldData(MemberDTO oldData) {
		this.oldData = oldData;
	}
	public UpdateMemberDTO getPendingData() {
		return pendingData;
	}
	public void setPendingData(UpdateMemberDTO pendingData) {
		this.pendingData = pendingData;
	}
}
