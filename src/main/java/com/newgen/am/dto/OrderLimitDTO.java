package com.newgen.am.dto;

import javax.validation.constraints.Positive;

public class OrderLimitDTO {
	@Positive (message = "Invalid format.")
	private int orderLimit;

	public int getOrderLimit() {
		return orderLimit;
	}

	public void setOrderLimit(int orderLimit) {
		this.orderLimit = orderLimit;
	}
}
