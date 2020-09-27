package com.newgen.am.dto;

import java.io.Serializable;

import javax.validation.Valid;

import lombok.Data;

@Data
public class ApprovalUpdateCollaboratorDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private CollaboratorDTO oldData;
	@Valid
	private UpdateCollaboratorDTO pendingData;
}
