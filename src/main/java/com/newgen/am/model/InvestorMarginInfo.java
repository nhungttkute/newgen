package com.newgen.am.model;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "investor_margin_info")
public class InvestorMarginInfo extends AuditModel implements Serializable{
	private static final long serialVersionUID = 1L;
	private String memberCode;
	private String memberName;
	private String brokerCode;
    private String brokerName;
    private String collaboratorCode;
    private String collaboratorName;
    private String investorCode;
    private String investorName;
	private double sodBalance; //So du TK dau ngay
    private double changedAmount; //Nop rut trong phien
    private double pendingWithdrawalAmount; // Rút ký quỹ treo (chờ approve)
    private double marginSurplusInterestRate; //Lãi suất dư thừa ký quỹ
    private double marginDeficitInterestRate; //Lãi suất thiếu hụt ký quỹ
    private long initialRequiredMargin; // Ky quy ban dau yeu cau
    private long actualProfitVND; // Lo lai thuc te
    private long estimatedProfitVND; // Lo lai du kien
    private long transactionFee; // Phi giao dich
    private long initialRequiredMarginProvisional; // Ky quy ban dau yeu cau tam tinh
    private long pendingTransactionFee; // Phi du thu (tren so lot cho khop)
}
