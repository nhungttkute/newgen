package com.newgen.am.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

public class MarginTransCSV {
	@CsvBindByName(column = "ID")
	@CsvBindByPosition(position = 0)
    private String _id;
	@CsvBindByName(column = "MEMBER_CODE")
	@CsvBindByPosition(position = 1)
    private String memberCode;
	@CsvBindByName(column = "MEMBER_NAME")
	@CsvBindByPosition(position = 2)
    private String memberName;
	@CsvBindByName(column = "BROKER_CODE")
	@CsvBindByPosition(position = 3)
    private String brokerCode;
	@CsvBindByName(column = "BROKER_NAME")
	@CsvBindByPosition(position = 4)
    private String brokerName;
	@CsvBindByName(column = "COLLABORATOR_CODE")
	@CsvBindByPosition(position = 5)
    private String collaboratorCode;
	@CsvBindByName(column = "COLLABORATOR_NAME")
	@CsvBindByPosition(position = 6)
    private String collaboratorName;
	@CsvBindByName(column = "INVESTOR_CODE")
	@CsvBindByPosition(position = 7)
    private String investorCode;
	@CsvBindByName(column = "INVESTOR_NAME")
	@CsvBindByPosition(position = 8)
    private String investorName;
	@CsvBindByName(column = "TRANSACTION_TYPE")
	@CsvBindByPosition(position = 9)
	private String transactionType;
	@CsvBindByName(column = "AMOUNT")
	@CsvBindByPosition(position = 10)
	private long amount;
	@CsvBindByName(column = "CURRENCY")
	@CsvBindByPosition(position = 11)
	private String currency;
	@CsvBindByName(column = "APPROVAL_USER")
	@CsvBindByPosition(position = 12)
	private String approvalUser;
	@CsvBindByName(column = "APPROVAL_DATE")
	@CsvBindByPosition(position = 13)
	private String approvalDate;
	@CsvBindByName(column = "NOTE")
	@CsvBindByPosition(position = 14)
	private String note;
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
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
	public String getInvestorCode() {
		return investorCode;
	}
	public void setInvestorCode(String investorCode) {
		this.investorCode = investorCode;
	}
	public String getInvestorName() {
		return investorName;
	}
	public void setInvestorName(String investorName) {
		this.investorName = investorName;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getApprovalUser() {
		return approvalUser;
	}
	public void setApprovalUser(String approvalUser) {
		this.approvalUser = approvalUser;
	}
	public String getApprovalDate() {
		return approvalDate;
	}
	public void setApprovalDate(String approvalDate) {
		this.approvalDate = approvalDate;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
}
