package com.newgen.am.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class GeneralFee implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String processMethod;
	private long feeAmount;
	private long appliedDate;
}
