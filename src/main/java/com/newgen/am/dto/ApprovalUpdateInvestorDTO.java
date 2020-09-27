package com.newgen.am.dto;

import java.io.Serializable;

import javax.validation.Valid;

import lombok.Data;

@Data
public class ApprovalUpdateInvestorDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private InvestorDTO oldData;
	@Valid
	private UpdateInvestorDTO pendingData;
}
