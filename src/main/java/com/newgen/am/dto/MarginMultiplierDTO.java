package com.newgen.am.dto;

import javax.validation.constraints.Positive;

public class MarginMultiplierDTO {
	@Positive(message = "Invalid format.")
	private double marginMultiplier;

	public double getMarginMultiplier() {
		return marginMultiplier;
	}

	public void setMarginMultiplier(double marginMultiplier) {
		this.marginMultiplier = marginMultiplier;
	}
	
}
