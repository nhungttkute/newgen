package com.newgen.am.dto;

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
	
	public String getMemberCode() {
		return memberCode;
	}
	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}
	public String getBrokerCode() {
		return brokerCode;
	}
	public void setBrokerCode(String brokerCode) {
		this.brokerCode = brokerCode;
	}
	public String getCollaboratorCode() {
		return collaboratorCode;
	}
	public void setCollaboratorCode(String collaboratorCode) {
		this.collaboratorCode = collaboratorCode;
	}
	public String getInvestorCode() {
		return investorCode;
	}
	public void setInvestorCode(String investorCode) {
		this.investorCode = investorCode;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public long getWithdrawableAmount() {
		return withdrawableAmount;
	}
	public void setWithdrawableAmount(long withdrawableAmount) {
		this.withdrawableAmount = withdrawableAmount;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getMemberName() {
		return memberName;
	}
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	public String getBrokerName() {
		return brokerName;
	}
	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
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
	
}
