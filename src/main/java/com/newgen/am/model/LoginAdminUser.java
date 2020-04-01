/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import org.springframework.data.mongodb.core.mapping.Field;
import com.mongodb.lang.NonNull;
import java.util.Date;
import java.util.List;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author nhungtt
 */
@Document(collection = "login_admin_users")
public class LoginAdminUser extends AuditModel implements Serializable {
    @Transient
    public static final String SEQUENCE_NAME = "login_admin_user_seq";
    @Id
    private long id;
    @NonNull
    @Indexed(unique = true)
    private String username;
    @NonNull
    @JsonIgnore
    private String password;
    @Field
    private Boolean checkPin = true;
    @NonNull
    private String pin;
    private String status;
    @Field(name = "access_token")
    private String accessToken;
    @Field(name = "token_expiration")
    private long tokenExpiration;
    @Field
    private Boolean logined = false;
    @Field
    private Boolean mustChangePassword = true;
    private int logonCounts;
    private Date logonTime;
    @Field(name = "department_id")
    private long departmentId;
    @Field(name = "user_id")
    private long userId;
    @Field(name = "member_id")
    private long memberId;
    @Field(name = "member_user_id")
    private long memberUserId;
    @Field(name = "broker_id")
    private long brokerId;
    @Field(name = "broker_user_id")
    private long brokerUserId;
    @Field(name = "collaborator_id")
    private long collaboratorId;
    @Field(name = "collaborator_user_id")
    private long collaboratorUserId;
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

    public long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(long departmentId) {
        this.departmentId = departmentId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public long getMemberUserId() {
        return memberUserId;
    }

    public void setMemberUserId(long memberUserId) {
        this.memberUserId = memberUserId;
    }

    public long getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(long brokerId) {
        this.brokerId = brokerId;
    }

    public long getBrokerUserId() {
        return brokerUserId;
    }

    public void setBrokerUserId(long brokerUserId) {
        this.brokerUserId = brokerUserId;
    }

    public long getCollaboratorId() {
        return collaboratorId;
    }

    public void setCollaboratorId(long collaboratorId) {
        this.collaboratorId = collaboratorId;
    }

    public long getCollaboratorUserId() {
        return collaboratorUserId;
    }

    public void setCollaboratorUserId(long collaboratorUserId) {
        this.collaboratorUserId = collaboratorUserId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<WatchList> getWatchlists() {
        return watchlists;
    }

    public void setWatchlists(List<WatchList> watchlists) {
        this.watchlists = watchlists;
    }

}
