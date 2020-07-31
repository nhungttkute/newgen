package com.newgen.am.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "investor_activation_approvals")
public class InvestorActivationApproval extends BaseApproval {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String investorCode;
	private double marginSurplusInterestRate;
    private double marginDeficitInterestRate;
    
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
	public double getMarginSurplusInterestRate() {
		return marginSurplusInterestRate;
	}
	public void setMarginSurplusInterestRate(double marginSurplusInterestRate) {
		this.marginSurplusInterestRate = marginSurplusInterestRate;
	}
	public double getMarginDeficitInterestRate() {
		return marginDeficitInterestRate;
	}
	public void setMarginDeficitInterestRate(double marginDeficitInterestRate) {
		this.marginDeficitInterestRate = marginDeficitInterestRate;
	}
}
