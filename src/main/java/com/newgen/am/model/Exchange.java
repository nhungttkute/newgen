package com.newgen.am.model;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import lombok.Data;

@Data
public class Exchange implements Serializable {
	private static final long serialVersionUID = 1L;
	@NotEmpty(message = "Required.")
	private String exchangeCode;
	@NotEmpty(message = "Required.")
	private String priceType;
	@NotEmpty(message = "Required.")
	private String processMethod;
	@Positive
	private long appliedDate;
}
