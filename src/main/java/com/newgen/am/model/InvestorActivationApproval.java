package com.newgen.am.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "investor_activation_approvals")
public class InvestorActivationApproval extends BaseApproval {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String investorCode;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getInvestorCode() {
		return investorCode;
	}
	public void setInvestorCode(String investorCode) {
		this.investorCode = investorCode;
	}
}
