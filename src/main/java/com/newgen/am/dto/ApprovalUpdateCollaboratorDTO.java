package com.newgen.am.dto;

import java.io.Serializable;

import javax.validation.Valid;

public class ApprovalUpdateCollaboratorDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private CollaboratorDTO oldData;
	@Valid
	private UpdateCollaboratorDTO pendingData;
	public CollaboratorDTO getOldData() {
		return oldData;
	}
	public void setOldData(CollaboratorDTO oldData) {
		this.oldData = oldData;
	}
	public UpdateCollaboratorDTO getPendingData() {
		return pendingData;
	}
	public void setPendingData(UpdateCollaboratorDTO pendingData) {
		this.pendingData = pendingData;
	}
}
