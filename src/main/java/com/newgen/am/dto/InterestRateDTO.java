package com.newgen.am.dto;

import java.io.Serializable;

import org.springframework.format.annotation.NumberFormat;

public class InterestRateDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	@NumberFormat
	private double marginSurplusInterestRate;
	@NumberFormat
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
