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
    private String id;
    @NonNull
    @Indexed(unique = true)
    private String username;
    @NonNull
    @JsonIgnore
    private String password;
    @Field
    private Boolean checkPin = true;
    @NonNull
    @Field
    private String pin;
    private String status;
    private String accessToken;
    private long tokenExpiration;
    @Field
    private Boolean logined = false;
    @Field
    private Boolean mustChangePassword = true;
    private int logonCounts;
    private long logonTime;
    private String deptId;
    private String deptUserId;
    private String memberId;
    private String memberUserId;
    private String brokerId;
    private String brokerUserId;
    private String collaboratorId;
    private String collaboratorUserId;
    @Field
    @Length(max = 10000)
    private String layout;
    private String language;
    private String theme;
    private int fontSize;
    private List<WatchList> watchlists;

    public String getId() {
		return id;
	}

	public void setId(String id) {
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

    public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getDeptUserId() {
		return deptUserId;
	}

	public void setDeptUserId(String deptUserId) {
		this.deptUserId = deptUserId;
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

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberUserId() {
		return memberUserId;
	}

	public void setMemberUserId(String memberUserId) {
		this.memberUserId = memberUserId;
	}

	public String getBrokerId() {
		return brokerId;
	}

	public void setBrokerId(String brokerId) {
		this.brokerId = brokerId;
	}

	public String getBrokerUserId() {
		return brokerUserId;
	}

	public void setBrokerUserId(String brokerUserId) {
		this.brokerUserId = brokerUserId;
	}

	public String getCollaboratorId() {
		return collaboratorId;
	}

	public void setCollaboratorId(String collaboratorId) {
		this.collaboratorId = collaboratorId;
	}

	public String getCollaboratorUserId() {
		return collaboratorUserId;
	}

	public void setCollaboratorUserId(String collaboratorUserId) {
		this.collaboratorUserId = collaboratorUserId;
	}

	public int getLogonCounts() {
        return logonCounts;
    }

    public void setLogonCounts(int logonCounts) {
        this.logonCounts = logonCounts;
    }

    public long getLogonTime() {
        return logonTime;
    }

    public void setLogonTime(long logonTime) {
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
