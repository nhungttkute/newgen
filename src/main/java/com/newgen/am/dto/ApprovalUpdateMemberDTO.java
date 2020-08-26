package com.newgen.am.dto;

import java.io.Serializable;

import javax.validation.Valid;

public class ApprovalUpdateMemberDTO implements Serializable {
	private static final long serialVersionUID = 1L;
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
