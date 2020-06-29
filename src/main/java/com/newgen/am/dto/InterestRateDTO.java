package com.newgen.am.dto;

import java.io.Serializable;

import javax.validation.constraints.Positive;

public class InterestRateDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	@Positive
	private double marginSurplusInterestRate;
	@Positive
    private double marginDeficitInterestRate;
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
