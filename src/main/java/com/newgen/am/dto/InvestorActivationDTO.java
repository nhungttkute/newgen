package com.newgen.am.dto;

import java.io.Serializable;

public class InvestorActivationDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String investorCode;
	private String investorName;
	private String phoneNumber;
	private String email;
	private String identityCard;
	private String note;
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
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getIdentityCard() {
		return identityCard;
	}
	public void setIdentityCard(String identityCard) {
		this.identityCard = identityCard;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
}
