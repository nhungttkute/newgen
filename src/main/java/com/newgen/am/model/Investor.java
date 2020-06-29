/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author nhungtt
 */
@Document(collection = "investors")
public class Investor extends AuditModel implements Serializable {
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
    private String status;
    private String note;
    private String type;
    private Company company;
    private Individual individual;
    private Contact contact;
    private CqgInfo cqgInfo;
	private InvestorAccount account;
    private List<InvestorUser> users;
    private UserRole role;
    private int orderLimit;
    private int defaultPositionLimit;
    private long defaultCommodityFee;
    private double marginMultiplier;
    private List<GeneralFee> generalFees;
    private MarginRatioAlert marginRatioAlert;
    private RiskParameters riskParameters;
    private List<Commodity> commodities;
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}
	public Individual getIndividual() {
		return individual;
	}
	public void setIndividual(Individual individual) {
		this.individual = individual;
	}
	public Contact getContact() {
		return contact;
	}
	public void setContact(Contact contact) {
		this.contact = contact;
	}
	public InvestorAccount getAccount() {
		return account;
	}
	public void setAccount(InvestorAccount account) {
		this.account = account;
	}
	public List<InvestorUser> getUsers() {
		return users;
	}
	public void setUsers(List<InvestorUser> users) {
		this.users = users;
	}
	public UserRole getRole() {
		return role;
	}
	public void setRole(UserRole role) {
		this.role = role;
	}
	public int getOrderLimit() {
		return orderLimit;
	}
	public void setOrderLimit(int orderLimit) {
		this.orderLimit = orderLimit;
	}
	public int getDefaultPositionLimit() {
		return defaultPositionLimit;
	}
	public void setDefaultPositionLimit(int defaultPositionLimit) {
		this.defaultPositionLimit = defaultPositionLimit;
	}
	public long getDefaultCommodityFee() {
		return defaultCommodityFee;
	}
	public void setDefaultCommodityFee(long defaultCommodityFee) {
		this.defaultCommodityFee = defaultCommodityFee;
	}
	public double getMarginMultiplier() {
		return marginMultiplier;
	}
	public void setMarginMultiplier(double marginMultiplier) {
		this.marginMultiplier = marginMultiplier;
	}
	public List<GeneralFee> getGeneralFees() {
		return generalFees;
	}
	public void setGeneralFees(List<GeneralFee> generalFees) {
		this.generalFees = generalFees;
	}
	public MarginRatioAlert getMarginRatioAlert() {
		return marginRatioAlert;
	}
	public void setMarginRatioAlert(MarginRatioAlert marginRatioAlert) {
		this.marginRatioAlert = marginRatioAlert;
	}
	public RiskParameters getRiskParameters() {
		return riskParameters;
	}
	public void setRiskParameters(RiskParameters riskParameters) {
		this.riskParameters = riskParameters;
	}
	public List<Commodity> getCommodities() {
		return commodities;
	}
	public void setCommodities(List<Commodity> commodities) {
		this.commodities = commodities;
	}
	public CqgInfo getCqgInfo() {
		return cqgInfo;
	}
	public void setCqgInfo(CqgInfo cqgInfo) {
		this.cqgInfo = cqgInfo;
	}
}
