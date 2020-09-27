package com.newgen.am.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CqgInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	private String accountId;
	private String customerId;
	private String profileId;
	private String userId;
	private String balanceId;
}
