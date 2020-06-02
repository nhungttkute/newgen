package com.newgen.am.dto;

import java.io.Serializable;

import javax.validation.constraints.Positive;

public class MarginInfoDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String investorCode;
	private String investorName;
	private String currency;
	@Positive(message = "Invalid format.")
    private double marginSurplusInterestRate;
	@Positive(message = "Invalid format.")
    private double marginDeficitInterestRate;
    private long availableBalance; // So du kha dung
	public String getInvestorCode() {
		return investorCode;
	}
	public void setInvestorCode(String investorCode) {
		this.investorCode = investorCode;
	}
	public String getInvestorName() {
		return investorName;
	}
	public void setInvestorName(String investorName) {
		this.investorName = investorName;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
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
	public long getAvailableBalance() {
		return availableBalance;
	}
	public void setAvailableBalance(long availableBalance) {
		this.availableBalance = availableBalance;
	}
}
