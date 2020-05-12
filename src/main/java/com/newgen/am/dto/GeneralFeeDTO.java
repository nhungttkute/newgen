package com.newgen.am.dto;

import javax.validation.constraints.Positive;

public class GeneralFeeDTO {
	@Positive(message = "Invalid format.")
	private long generalFee;

	public long getGeneralFee() {
		return generalFee;
	}

	public void setGeneralFee(long generalFee) {
		this.generalFee = generalFee;
	}
	
}
