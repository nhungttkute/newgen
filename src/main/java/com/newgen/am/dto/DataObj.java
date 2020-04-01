/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newgen.am.model.Commodity;
import com.newgen.am.model.WatchList;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author nhungtt
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataObj implements Serializable {

    private LoginUserOutputDTO user;
    private String accessToken;
    private List<Commodity> commodities;
    private List<WatchList> watchLists;
    private String layout;
    private String language;
    private String theme;
    private int fontSize;
    private List<ListUserDTO> users;
    private InvestorAccountDTO investorAccount;

    public LoginUserOutputDTO getUser() {
        return user;
    }

    public void setUser(LoginUserOutputDTO user) {
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public List<Commodity> getCommodities() {
        return commodities;
    }

    public void setCommodities(List<Commodity> commodities) {
        this.commodities = commodities;
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

    public List<ListUserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<ListUserDTO> users) {
        this.users = users;
    }

    public InvestorAccountDTO getInvestorAccount() {
        return investorAccount;
    }

    public void setInvestorAccount(InvestorAccountDTO investorAccount) {
        this.investorAccount = investorAccount;
    }
    
}
