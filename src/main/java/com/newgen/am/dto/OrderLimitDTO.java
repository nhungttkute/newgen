package com.newgen.am.dto;

import javax.validation.constraints.Positive;

import lombok.Data;

@Data
public class OrderLimitDTO {
	@Positive (message = "Invalid format.")
	private int orderLimit;
}
