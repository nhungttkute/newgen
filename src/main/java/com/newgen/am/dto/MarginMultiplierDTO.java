package com.newgen.am.dto;

import javax.validation.constraints.Positive;

import lombok.Data;

@Data
public class MarginMultiplierDTO {
	@Positive(message = "Invalid format.")
	private double marginMultiplier;
}
