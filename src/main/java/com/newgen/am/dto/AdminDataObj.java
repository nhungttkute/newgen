/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newgen.am.model.RoleFunction;
import com.newgen.am.model.InvestorMarginTransaction;
import com.newgen.am.model.WatchList;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AdminDataObj implements Serializable {
	private static final long serialVersionUID = 1L;
	private LoginAdminUserOutputDTO user;
    private String accessToken;
    private List<WatchList> watchLists;
    private String layout;
    private String language;
    private String theme;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int fontSize;
    private List<LoginAdminUsersDTO> adminUsers;
    private List<String> functions;
    
    // response for department
    private List<DepartmentDTO> departments;
    private List<UserDTO> deptUsers;
    private DepartmentDTO department;
    private UserDTO deptUser;
    private List<RoleDTO> systemRoles;
    private List<SystemFunctionDTO> systemFunctions;
    
    // response for member
    private List<MemberDTO> members;
    private MemberDTO member;
    private UserDTO memberUser;
    private List<UserDTO> memberUsers;
    private List<RoleDTO> memberRoles;
    private List<RoleFunction> memberFunctions;
    private MemberCommoditiesDTO memberCommodities;
    
    // response for broker
    private List<BrokerDTO> brokers;
    private BrokerDTO broker;
    private UserDTO brokerUser;
    private BrokerCommoditiesDTO brokerCommodities;
    
    // response for collaborator
    private List<CollaboratorDTO> collaborators;
    private CollaboratorDTO collaborator;
    private UserDTO collaboratorUser;
    
    // response for common list
    private List<ListElementDTO> departmentList;
    private List<ListElementDTO> memberList;
    private List<ListElementDTO> brokerList;
    private List<ListElementDTO> collaboratorList;
    private List<ListElementDTO> investorList;
    
    private List<UserBaseInfo> adminUserList;
    private List<UserBaseInfo> memberUserList;
    private List<UserBaseInfo> brokerUserList;
    private List<UserBaseInfo> collaboratorUserList;
    private List<UserBaseInfo> investorUserList;
    
    // response for investor
    private List<String> investorCodes;
    private List<InvestorDTO> investors;
    private InvestorDTO investor;
    private UserDTO investorUser;
    private List<UserDTO> investorUsers;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private long withdrawableAmount;
    private List<AccountStatusDTO> accountStatusInfos;
    private List<InvestorMarginTransaction> investorMarginTransactions;
    
    private List<ExchangeSettingDTO> adminUserExchanges;
    private List<ExchangeSettingDTO> investorUserExchanges;
    
    private ExchangeSettingDTO exchangeSetting;
    
    // investor withdrawal amount
    private long amount;
    
    // session date
    private String date;
}
