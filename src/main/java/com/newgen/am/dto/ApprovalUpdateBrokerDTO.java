package com.newgen.am.dto;

import java.io.Serializable;

import javax.validation.Valid;

import lombok.Data;

@Data
public class ApprovalUpdateBrokerDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private BrokerDTO oldData;
	@Valid
	private UpdateBrokerDTO pendingData;
}
