/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.lang.NonNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 *
 * @author nhungtt
 */
@Document(collection = "login_investor_users")
public class LoginInvestorUser extends AuditModel implements Serializable {

    @Transient
    public static final String SEQUENCE_NAME = "login_investor_user_seq";
    @Id
    private long id;
    @Indexed(unique = true)
    @NonNull
    private String username;
    @NonNull
    @JsonIgnore
    private String password;
    @Field
    private Boolean checkPin = true;
    @NonNull
    private String pin;
    private String status;
    private String accessToken;
    private long tokenExpiration;
    @Field
    private Boolean logined = false;
    @Field
    private Boolean mustChangePassword  = true;
    @Field
    private int logonCounts = 0;
    private Date logonTime;
    private long memberId;
    private long brokerId;
    private long collaboratorId;
    private long investorId;
    private long investorUserId;
    @Field
    @Length(max = 10000)
    private String layout;
    private String language;
    private String theme;
    private int fontSize;
    private List<WatchList> watchlists;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getCheckPin() {
        return checkPin;
    }

    public void setCheckPin(Boolean checkPin) {
        this.checkPin = checkPin;
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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getTokenExpiration() {
        return tokenExpiration;
    }

    public void setTokenExpiration(long tokenExpiration) {
        this.tokenExpiration = tokenExpiration;
    }

    public Boolean getLogined() {
        return logined;
    }

    public void setLogined(Boolean logined) {
        this.logined = logined;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public long getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(long brokerId) {
        this.brokerId = brokerId;
    }

    public long getCollaboratorId() {
        return collaboratorId;
    }

    public void setCollaboratorId(long collaboratorId) {
        this.collaboratorId = collaboratorId;
    }

    public long getInvestorId() {
        return investorId;
    }

    public void setInvestorId(long investorId) {
        this.investorId = investorId;
    }

    public long getInvestorUserId() {
        return investorUserId;
    }

    public void setInvestorUserId(long investorUserId) {
        this.investorUserId = investorUserId;
    }

    public List<WatchList> getWatchlists() {
        return watchlists;
    }

    public void setWatchlists(List<WatchList> watchlists) {
        this.watchlists = watchlists;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getLogonCounts() {
        return logonCounts;
    }

    public void setLogonCounts(int logonCounts) {
        this.logonCounts = logonCounts;
    }

    public Date getLogonTime() {
        return logonTime;
    }

    public void setLogonTime(Date logonTime) {
        this.logonTime = logonTime;
    }

    public Boolean getMustChangePassword() {
        return mustChangePassword;
    }

    public void setMustChangePassword(Boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }
    
}
