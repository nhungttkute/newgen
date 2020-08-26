package com.newgen.am.model;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

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
    
	public String getInvestorCode() {
		return investorCode;
	}
	public void setInvestorCode(String investorCode) {
		this.investorCode = investorCode;
	}
	public double getMarginSurplusInterestRate() {
		return marginSurplusInterestRate;
	}
	public void setMarginSurplusInterestRate(double marginSurplusInterestRate) {
		this.marginSurplusInterestRate = marginSurplusInterestRate;
	}
	public double getMarginDeficitInterestRate() {
		return marginDeficitInterestRate;
	}
	public void setMarginDeficitInterestRate(double marginDeficitInterestRate) {
		this.marginDeficitInterestRate = marginDeficitInterestRate;
	}
	public long getInitialRequiredMargin() {
		return initialRequiredMargin;
	}
	public void setInitialRequiredMargin(long initialRequiredMargin) {
		this.initialRequiredMargin = initialRequiredMargin;
	}
	public long getActualProfitVND() {
		return actualProfitVND;
	}
	public void setActualProfitVND(long actualProfitVND) {
		this.actualProfitVND = actualProfitVND;
	}
	public long getEstimatedProfitVND() {
		return estimatedProfitVND;
	}
	public void setEstimatedProfitVND(long estimatedProfitVND) {
		this.estimatedProfitVND = estimatedProfitVND;
	}
	public long getTransactionFee() {
		return transactionFee;
	}
	public void setTransactionFee(long transactionFee) {
		this.transactionFee = transactionFee;
	}
	public String getMemberCode() {
		return memberCode;
	}
	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}
	public String getMemberName() {
		return memberName;
	}
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	public String getBrokerCode() {
		return brokerCode;
	}
	public void setBrokerCode(String brokerCode) {
		this.brokerCode = brokerCode;
	}
	public String getBrokerName() {
		return brokerName;
	}
	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
	}
	public String getCollaboratorCode() {
		return collaboratorCode;
	}
	public void setCollaboratorCode(String collaboratorCode) {
		this.collaboratorCode = collaboratorCode;
	}
	public String getCollaboratorName() {
		return collaboratorName;
	}
	public void setCollaboratorName(String collaboratorName) {
		this.collaboratorName = collaboratorName;
	}
	public String getInvestorName() {
		return investorName;
	}
	public void setInvestorName(String investorName) {
		this.investorName = investorName;
	}
	public long getInitialRequiredMarginProvisional() {
		return initialRequiredMarginProvisional;
	}
	public void setInitialRequiredMarginProvisional(long initialRequiredMarginProvisional) {
		this.initialRequiredMarginProvisional = initialRequiredMarginProvisional;
	}
	public long getPendingTransactionFee() {
		return pendingTransactionFee;
	}
	public void setPendingTransactionFee(long pendingTransactionFee) {
		this.pendingTransactionFee = pendingTransactionFee;
	}
	public double getSodBalance() {
		return sodBalance;
	}
	public void setSodBalance(double sodBalance) {
		this.sodBalance = sodBalance;
	}
	public double getChangedAmount() {
		return changedAmount;
	}
	public void setChangedAmount(double changedAmount) {
		this.changedAmount = changedAmount;
	}
	public double getPendingWithdrawalAmount() {
		return pendingWithdrawalAmount;
	}
	public void setPendingWithdrawalAmount(double pendingWithdrawalAmount) {
		this.pendingWithdrawalAmount = pendingWithdrawalAmount;
	}
    
}
