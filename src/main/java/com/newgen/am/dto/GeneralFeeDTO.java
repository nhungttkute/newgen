package com.newgen.am.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import com.newgen.am.validation.LengthGroup;

import lombok.Data;

@Data
public class GeneralFeeDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	@NotEmpty(message = "Required.")
	@Size(max = 100, message = "Invalid format.", groups = LengthGroup.class)
	private String name;
	@NotEmpty(message = "Required.")
	@Size(max = 20, message = "Invalid format.", groups = LengthGroup.class)
	private String processMethod;
	@Positive
	private long feeAmount;
	private long appliedDate;
}
