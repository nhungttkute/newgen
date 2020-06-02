package com.newgen.am.model;

import java.io.Serializable;

public class CqgInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	private String accountId;

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
}
