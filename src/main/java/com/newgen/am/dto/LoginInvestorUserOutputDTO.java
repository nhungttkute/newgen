package com.newgen.am.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newgen.am.model.Commodity;
import com.newgen.am.model.WatchList;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LoginInvestorUserOutputDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
    private String username;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String accessToken;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
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
    private List<Commodity> commodities;
    private List<WatchList> watchLists;
    private String layout;
    private String language;
    private String theme;
    private String tableSetting;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int fontSize;
    private List<String> functions;
}
