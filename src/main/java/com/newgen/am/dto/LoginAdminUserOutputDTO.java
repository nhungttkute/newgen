/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newgen.am.model.WatchList;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LoginAdminUserOutputDTO implements Serializable {
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
    private List<String> functions;
    private String tableSetting;
}
