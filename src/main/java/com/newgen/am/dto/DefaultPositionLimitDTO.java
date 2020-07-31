package com.newgen.am.dto;

import javax.validation.constraints.Positive;

public class DefaultPositionLimitDTO {
	@Positive (message = "Invalid format.")
	private int defaultPositionLimit;

	public int getDefaultPositionLimit() {
		return defaultPositionLimit;
	}

	public void setDefaultPositionLimit(int defaultPositionLimit) {
		this.defaultPositionLimit = defaultPositionLimit;
	}
}
