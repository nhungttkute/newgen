/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.util.List;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 *
 * @author nhungtt
 */
@Document(collection = "investors")
public class Investor {
    @Transient
    public static final String SEQUENCE_NAME = "investor_seq";
    private Long id;
    @Field(name = "member_id")
    private Long memberId;
    @Field(name = "broker_id")
    private Long brokerId;
    @Field(name = "collaborator_id")
    private Long collaboratorId;
    @Field(name = "member_code")
    private String memberCode;
    @Field(name = "member_name")
    private String memberName;
    @Field(name = "broker_code")
    private String brokerCode;
    @Field(name = "broker_name")
    private String brokerName;
    @Field(name = "collaborator_code")
    private String collaboratorCode;
    @Field(name = "collaborator_name")
    private String collaboratorName;
    private String code;
    private String name;
    private String status;
    private String note;
    private String type;
    private Company company;
    private Individual individual;
    private Contact contact;
    private InvestorAccount account;
    private List<InvestorUser> users;
    private UserRole role;
    private Integer orderLimit;
    private List<Commodity> commodities;
    private MarginRatioAlert marginRatioAlert;
    private Double marginMultiplier;
    private Long otherFee;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(Long brokerId) {
        this.brokerId = brokerId;
    }

    public Long getCollaboratorId() {
        return collaboratorId;
    }

    public void setCollaboratorId(Long collaboratorId) {
        this.collaboratorId = collaboratorId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Double getMarginMultiplier() {
        return marginMultiplier;
    }

    public void setMarginMultiplier(Double marginMultiplier) {
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

    public Integer getOrderLimit() {
        return orderLimit;
    }

    public void setOrderLimit(Integer orderLimit) {
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

    public Long getOtherFee() {
        return otherFee;
    }

    public void setOtherFee(Long otherFee) {
        this.otherFee = otherFee;
    }
    
}
