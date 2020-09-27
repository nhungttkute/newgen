package com.newgen.am.model;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "investor_margin_trans")
public class InvestorMarginTransaction extends AuditModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
    private String memberCode;
    private String memberName;
    private String brokerCode;
    private String brokerName;
    private String collaboratorCode;
    private String collaboratorName;
    private String investorCode;
    private String investorName;
    private String transactionType;
    private long amount;
    private String currency;
    private String approvalUser;
    private long approvalDate;
    private String note;
    private String sessionDate;
}
