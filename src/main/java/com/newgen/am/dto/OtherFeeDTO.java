package com.newgen.am.dto;

import javax.validation.constraints.Positive;

import lombok.Data;

@Data
public class OtherFeeDTO {
	@Positive(message = "Invalid format.")
	private long otherFee;
}
