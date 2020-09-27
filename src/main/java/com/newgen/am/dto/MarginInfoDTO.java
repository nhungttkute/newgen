package com.newgen.am.dto;

import java.io.Serializable;

import org.springframework.format.annotation.NumberFormat;

import lombok.Data;

@Data
public class MarginInfoDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String investorCode;
	private String investorName;
	private String currency;
	@NumberFormat
    private double marginSurplusInterestRate;
	@NumberFormat
    private double marginDeficitInterestRate;
    private double availableBalance; // So du kha dung
}
