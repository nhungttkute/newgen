/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import com.newgen.am.model.Commodity;
import com.newgen.am.model.InvestorAccount;
import com.newgen.am.model.MarginRatioAlert;
import com.newgen.am.model.RoleFunction;
import java.util.List;

/**
 *
 * @author nhungtt
 */
public class UserInfoDTO {
    private Long id;
    private String username;
    private String pin;
    private String status;
    private Long tokenExpiration;
    private String memberCode;
    private String brokerCode;
    private String collaboratorCode;
    private String investorCode;
    private Long investorUserId;
    private InvestorAccount account;
    private List<RoleFunction> functions;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTokenExpiration() {
        return tokenExpiration;
    }

    public void setTokenExpiration(Long tokenExpiration) {
        this.tokenExpiration = tokenExpiration;
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

    public Long getInvestorUserId() {
        return investorUserId;
    }

    public void setInvestorUserId(Long investorUserId) {
        this.investorUserId = investorUserId;
    }

    public InvestorAccount getAccount() {
        return account;
    }

    public void setAccount(InvestorAccount account) {
        this.account = account;
    }

    public List<RoleFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(List<RoleFunction> functions) {
        this.functions = functions;
    }

    public Integer getOrderLimit() {
        return orderLimit;
    }

    public void setOrderLimit(Integer orderLimit) {
        this.orderLimit = orderLimit;
    }

    public List<Commodity> getCommodities() {
        return commodities;
    }

    public void setCommodities(List<Commodity> commodities) {
        this.commodities = commodities;
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

    public Long getOtherFee() {
        return otherFee;
    }

    public void setOtherFee(Long otherFee) {
        this.otherFee = otherFee;
    }
    
    
}
