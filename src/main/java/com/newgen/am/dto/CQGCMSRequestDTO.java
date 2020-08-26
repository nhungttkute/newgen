package com.newgen.am.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class CQGCMSRequestDTO {
	private String name;
	private String email;
	private String phone;
	private String fax;
	private String fullName;
	private String address;
	private String username;
	private String customerId;
	private String profileId;
	private String number;
	private String accountId;
	private String currency;
	private double balance;
	private long balanceId;
	private List<CQGCMSCommodityDTO> commodities;
	private List<CQGCMSAccountAuthDTO> linksToSet;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getProfileId() {
		return profileId;
	}
	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public long getBalanceId() {
		return balanceId;
	}
	public void setBalanceId(long balanceId) {
		this.balanceId = balanceId;
	}
	public List<CQGCMSCommodityDTO> getCommodities() {
		return commodities;
	}
	public void setCommodities(List<CQGCMSCommodityDTO> commodities) {
		this.commodities = commodities;
	}
	public List<CQGCMSAccountAuthDTO> getLinksToSet() {
		return linksToSet;
	}
	public void setLinksToSet(List<CQGCMSAccountAuthDTO> linksToSet) {
		this.linksToSet = linksToSet;
	}
}
