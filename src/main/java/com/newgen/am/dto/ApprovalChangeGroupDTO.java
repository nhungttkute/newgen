package com.newgen.am.dto;

import javax.validation.Valid;

import lombok.Data;

@Data
public class ApprovalChangeGroupDTO {
	private ChangeGroupDTO oldData;
	@Valid
	private ChangeGroupDTO pendingData;
}
