package com.newgen.am.dto;

import java.util.List;

import javax.validation.Valid;

import com.newgen.am.model.Exchange;
import com.newgen.am.validation.ValidUpdateStringField;

public class ExchangeSettingDTO {
	@ValidUpdateStringField
	private String deptCode;
	private String deptName;
	@ValidUpdateStringField
	private String memberCode;
	private String memberName;
	@ValidUpdateStringField
	private String brokerCode;
	private String brokerName;
	@ValidUpdateStringField
	private String collaboratorCode;
	private String collaboratorName;
	@ValidUpdateStringField
	private String investorCode;
	private String investorName;
	@ValidUpdateStringField
	private String username;
	@ValidUpdateStringField
	private String fullName;
	@Valid
	private List<UserBaseInfo> users;
	@Valid
	private List<Exchange> exchanges;

	public String getDeptCode() {
		return deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

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

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
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

	public List<UserBaseInfo> getUsers() {
		return users;
	}

	public void setUsers(List<UserBaseInfo> users) {
		this.users = users;
	}

	public List<Exchange> getExchanges() {
		return exchanges;
	}

	public void setExchanges(List<Exchange> exchanges) {
		this.exchanges = exchanges;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}
