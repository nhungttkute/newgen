/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.io.Serializable;
import java.util.List;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author nhungtt
 */
@Document(collection = "investors")
public class Investor extends AuditModel implements Serializable {
    @Transient
    public static final String SEQUENCE_NAME = "investor_seq";
    private String id;
    private String memberId;
    private String brokerId;
    private String collaboratorId;
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
    private InvestorAccount account;
    private List<InvestorUser> users;
    private UserRole role;
    private int orderLimit;
    private List<Commodity> commodities;
    private MarginRatioAlert marginRatioAlert;
    private double marginMultiplier;
    private long otherFee;

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getBrokerId() {
		return brokerId;
	}

	public void setBrokerId(String brokerId) {
		this.brokerId = brokerId;
	}

	public String getCollaboratorId() {
		return collaboratorId;
	}

	public void setCollaboratorId(String collaboratorId) {
		this.collaboratorId = collaboratorId;
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

    public List<InvestorUser> getUsers() {
        return users;
    }

    public void setUsers(List<InvestorUser> users) {
        this.users = users;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public MarginRatioAlert getMarginRatioAlert() {
        return marginRatioAlert;
    }

    public void setMarginRatioAlert(MarginRatioAlert marginRatioAlert) {
        this.marginRatioAlert = marginRatioAlert;
    }

    public double getMarginMultiplier() {
        return marginMultiplier;
    }

    public void setMarginMultiplier(double marginMultiplier) {
        this.marginMultiplier = marginMultiplier;
    }

    public List<Commodity> getCommodities() {
        return commodities;
    }

    public void setCommodities(List<Commodity> commodities) {
        this.commodities = commodities;
    }

    public InvestorAccount getAccount() {
        return account;
    }

    public void setAccount(InvestorAccount account) {
        this.account = account;
    }

    public int getOrderLimit() {
        return orderLimit;
    }

    public void setOrderLimit(int orderLimit) {
        this.orderLimit = orderLimit;
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

    public long getOtherFee() {
        return otherFee;
    }

    public void setOtherFee(long otherFee) {
        this.otherFee = otherFee;
    }
    
}
