package com.newgen.am.dto;

import javax.validation.constraints.Positive;

public class OtherFeeDTO {
	@Positive(message = "Invalid format.")
	private long otherFee;

	public long getOtherFee() {
		return otherFee;
	}

	public void setOtherFee(long otherFee) {
		this.otherFee = otherFee;
	}
	
}
