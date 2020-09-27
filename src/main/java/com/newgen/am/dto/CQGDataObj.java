package com.newgen.am.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CQGDataObj {
	private String customerId;
	private String profileId;
	private String userId;
	private String accountId;
	private String balanceId;
}
