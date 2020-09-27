/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newgen.am.model.Commodity;
import com.newgen.am.model.CqgInfo;
import com.newgen.am.model.Exchange;
import com.newgen.am.model.GeneralFee;
import com.newgen.am.model.InvestorAccount;
import com.newgen.am.model.MarginRatioAlert;
import com.newgen.am.model.RiskParameters;
import com.newgen.am.model.WatchList;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author nhungtt
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserInfoDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
    private String username;
    private String status;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String accessToken;
    private long tokenExpiration;
    private Boolean logined;
    private Boolean mustChangePassword;
    private String memberCode;
    private String memberName;
    private String brokerCode;
    private String brokerName;
    private String collaboratorCode;
    private String collaboratorName;
    private String investorCode;
    private String investorName;
    private String deptCode;
    private String deptName;
    private List<WatchList> watchLists;
    private String layout;
    private String language;
    private String theme;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int fontSize;
    private InvestorAccount account;
    private List<String> functions;
    private int orderLimit;
    private List<Commodity> commodities;
    private MarginRatioAlert marginRatioAlert;
    private double marginMultiplier;
    private List<GeneralFee> generalFees;
    private CqgInfo cqgInfo;
    private RiskParameters riskParameters;
    private List<Exchange> exchanges;
    private String tableSetting;
}
