package com.newgen.am.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newgen.am.model.RoleFunction;
import com.newgen.am.model.WatchList;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LoginUserOutputDTO {

    private long id;
    private String username;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String accessToken;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private long tokenExpiration;
    private Boolean logined;
    private Boolean mustChangePassword;
    private long memberId;
    private long brokerId;
    private long collaboratorId;
    private long investorId;
    private long investorUserId;
    private List<WatchList> watchLists;
    private String layout;
    private String language;
    private String theme;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int fontSize;
    private List<String> functions;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public List<WatchList> getWatchLists() {
        return watchLists;
    }

    public void setWatchLists(List<WatchList> watchLists) {
        this.watchLists = watchLists;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Boolean getMustChangePassword() {
        return mustChangePassword;
    }

    public void setMustChangePassword(Boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }

    public List<String> getFunctions() {
        return functions;
    }

    public void setFunctions(List<String> functions) {
        this.functions = functions;
    }

}
