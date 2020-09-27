package com.newgen.am.dto;

import java.io.Serializable;

import javax.validation.Valid;

import lombok.Data;

@Data
public class ApprovalUpdateMemberDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private MemberDTO oldData;
	@Valid
	private UpdateMemberDTO pendingData;
}
