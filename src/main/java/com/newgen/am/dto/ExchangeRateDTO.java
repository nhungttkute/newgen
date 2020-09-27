package com.newgen.am.dto;

import lombok.Data;

@Data
public class ExchangeRateDTO {
	private String _id;
	private long effectiveDate;
	private double exchangeRate;
	private String status;
	private long createDate;
	private long approvalDate;
	private String counterCurrency;
	private String monetaryBase;
	private String userCreate;
	private String approver;
	private long __v;
}
