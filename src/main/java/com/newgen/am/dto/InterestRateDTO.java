package com.newgen.am.dto;

import java.io.Serializable;

import org.springframework.format.annotation.NumberFormat;

import lombok.Data;

@Data
public class InterestRateDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	@NumberFormat
	private double marginSurplusInterestRate;
	@NumberFormat
    private double marginDeficitInterestRate;
}
