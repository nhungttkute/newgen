package com.newgen.am.dto;

import lombok.Data;

@Data
public class MarginTransactionDTO {
	private String memberCode;
    private String memberName;
    private String brokerCode;
    private String brokerName;
    private String collaboratorCode;
    private String collaboratorName;
    private String investorCode;
    private String investorName;
	private String transactionType;
	private long withdrawableAmount;
	private long amount;
	private String note;
}
