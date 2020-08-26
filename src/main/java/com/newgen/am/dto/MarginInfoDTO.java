package com.newgen.am.dto;

import java.io.Serializable;

import javax.validation.constraints.Positive;

import org.springframework.format.annotation.NumberFormat;

public class MarginInfoDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String investorCode;
	private String investorName;
	private String currency;
	@NumberFormat
    private double marginSurplusInterestRate;
	@NumberFormat
    private double marginDeficitInterestRate;
    private double availableBalance; // So du kha dung
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
	public double getAvailableBalance() {
		return availableBalance;
	}
	public void setAvailableBalance(double availableBalance) {
		this.availableBalance = availableBalance;
	}
	
}
