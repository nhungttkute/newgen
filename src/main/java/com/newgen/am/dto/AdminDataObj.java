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
import com.newgen.am.model.WatchList;

/**
 *
 * @author nhungtt
 */
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
    private List<ListElementDTO> memberList;
    private List<ListElementDTO> brokerList;
    private List<ListElementDTO> collaboratorList;
    private List<ListElementDTO> investorList;
    
    private List<NewsUserInfo> adminUserList;
    private List<NewsUserInfo> memberUserList;
    private List<NewsUserInfo> brokerUserList;
    private List<NewsUserInfo> collaboratorUserList;
    private List<NewsUserInfo> investorUserList;
    
    // response for investor
    private List<String> investorCodes;
    private List<InvestorDTO> investors;
    private InvestorDTO investor;
    private UserDTO investorUser;
    private List<UserDTO> investorUsers;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private long withdrawableAmount;
    private List<AccountStatusDTO> accountStatusInfos;

    public LoginAdminUserOutputDTO getUser() {
        return user;
    }

    public void setUser(LoginAdminUserOutputDTO user) {
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
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

    public List<DepartmentDTO> getDepartments() {
        return departments;
    }

    public void setDepartments(List<DepartmentDTO> departments) {
        this.departments = departments;
    }

    public DepartmentDTO getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentDTO department) {
        this.department = department;
    }

    public UserDTO getDeptUser() {
        return deptUser;
    }

    public void setDeptUser(UserDTO deptUser) {
        this.deptUser = deptUser;
    }

    public List<RoleDTO> getSystemRoles() {
        return systemRoles;
    }

    public void setSystemRoles(List<RoleDTO> systemRoles) {
        this.systemRoles = systemRoles;
    }

    public List<UserDTO> getDeptUsers() {
        return deptUsers;
    }

    public void setDeptUsers(List<UserDTO> deptUsers) {
        this.deptUsers = deptUsers;
    }

	public List<SystemFunctionDTO> getSystemFunctions() {
		return systemFunctions;
	}

	public void setSystemFunctions(List<SystemFunctionDTO> systemFunctions) {
		this.systemFunctions = systemFunctions;
	}

	public List<MemberDTO> getMembers() {
		return members;
	}

	public void setMembers(List<MemberDTO> members) {
		this.members = members;
	}

	public MemberDTO getMember() {
		return member;
	}

	public void setMember(MemberDTO member) {
		this.member = member;
	}

	public UserDTO getMemberUser() {
		return memberUser;
	}

	public void setMemberUser(UserDTO memberUser) {
		this.memberUser = memberUser;
	}

	public List<UserDTO> getMemberUsers() {
		return memberUsers;
	}

	public void setMemberUsers(List<UserDTO> memberUsers) {
		this.memberUsers = memberUsers;
	}

	public List<RoleDTO> getMemberRoles() {
		return memberRoles;
	}

	public void setMemberRoles(List<RoleDTO> memberRoles) {
		this.memberRoles = memberRoles;
	}

	public List<RoleFunction> getMemberFunctions() {
		return memberFunctions;
	}

	public void setMemberFunctions(List<RoleFunction> memberFunctions) {
		this.memberFunctions = memberFunctions;
	}

	public List<String> getInvestorCodes() {
		return investorCodes;
	}

	public void setInvestorCodes(List<String> investorCodes) {
		this.investorCodes = investorCodes;
	}

	public List<LoginAdminUsersDTO> getAdminUsers() {
		return adminUsers;
	}

	public void setAdminUsers(List<LoginAdminUsersDTO> adminUsers) {
		this.adminUsers = adminUsers;
	}

	public List<BrokerDTO> getBrokers() {
		return brokers;
	}

	public void setBrokers(List<BrokerDTO> brokers) {
		this.brokers = brokers;
	}

	public BrokerDTO getBroker() {
		return broker;
	}

	public void setBroker(BrokerDTO broker) {
		this.broker = broker;
	}

	public UserDTO getBrokerUser() {
		return brokerUser;
	}

	public void setBrokerUser(UserDTO brokerUser) {
		this.brokerUser = brokerUser;
	}

	public List<CollaboratorDTO> getCollaborators() {
		return collaborators;
	}

	public void setCollaborators(List<CollaboratorDTO> collaborators) {
		this.collaborators = collaborators;
	}

	public CollaboratorDTO getCollaborator() {
		return collaborator;
	}

	public void setCollaborator(CollaboratorDTO collaborator) {
		this.collaborator = collaborator;
	}

	public UserDTO getCollaboratorUser() {
		return collaboratorUser;
	}

	public void setCollaboratorUser(UserDTO collaboratorUser) {
		this.collaboratorUser = collaboratorUser;
	}

	public List<ListElementDTO> getMemberList() {
		return memberList;
	}

	public void setMemberList(List<ListElementDTO> memberList) {
		this.memberList = memberList;
	}

	public List<ListElementDTO> getBrokerList() {
		return brokerList;
	}

	public void setBrokerList(List<ListElementDTO> brokerList) {
		this.brokerList = brokerList;
	}

	public MemberCommoditiesDTO getMemberCommodities() {
		return memberCommodities;
	}

	public void setMemberCommodities(MemberCommoditiesDTO memberCommodities) {
		this.memberCommodities = memberCommodities;
	}

	public BrokerCommoditiesDTO getBrokerCommodities() {
		return brokerCommodities;
	}

	public void setBrokerCommodities(BrokerCommoditiesDTO brokerCommodities) {
		this.brokerCommodities = brokerCommodities;
	}

	public List<InvestorDTO> getInvestors() {
		return investors;
	}

	public void setInvestors(List<InvestorDTO> investors) {
		this.investors = investors;
	}

	public InvestorDTO getInvestor() {
		return investor;
	}

	public void setInvestor(InvestorDTO investor) {
		this.investor = investor;
	}

	public UserDTO getInvestorUser() {
		return investorUser;
	}

	public void setInvestorUser(UserDTO investorUser) {
		this.investorUser = investorUser;
	}

	public List<UserDTO> getInvestorUsers() {
		return investorUsers;
	}

	public void setInvestorUsers(List<UserDTO> investorUsers) {
		this.investorUsers = investorUsers;
	}

	public List<ListElementDTO> getCollaboratorList() {
		return collaboratorList;
	}

	public void setCollaboratorList(List<ListElementDTO> collaboratorList) {
		this.collaboratorList = collaboratorList;
	}

	public List<ListElementDTO> getInvestorList() {
		return investorList;
	}

	public void setInvestorList(List<ListElementDTO> investorList) {
		this.investorList = investorList;
	}

	public List<NewsUserInfo> getMemberUserList() {
		return memberUserList;
	}

	public void setMemberUserList(List<NewsUserInfo> memberUserList) {
		this.memberUserList = memberUserList;
	}

	public List<NewsUserInfo> getBrokerUserList() {
		return brokerUserList;
	}

	public void setBrokerUserList(List<NewsUserInfo> brokerUserList) {
		this.brokerUserList = brokerUserList;
	}

	public List<NewsUserInfo> getCollaboratorUserList() {
		return collaboratorUserList;
	}

	public void setCollaboratorUserList(List<NewsUserInfo> collaboratorUserList) {
		this.collaboratorUserList = collaboratorUserList;
	}

	public List<NewsUserInfo> getInvestorUserList() {
		return investorUserList;
	}

	public void setInvestorUserList(List<NewsUserInfo> investorUserList) {
		this.investorUserList = investorUserList;
	}

	public List<NewsUserInfo> getAdminUserList() {
		return adminUserList;
	}

	public void setAdminUserList(List<NewsUserInfo> adminUserList) {
		this.adminUserList = adminUserList;
	}

	public long getWithdrawableAmount() {
		return withdrawableAmount;
	}

	public void setWithdrawableAmount(long withdrawableAmount) {
		this.withdrawableAmount = withdrawableAmount;
	}

	public List<AccountStatusDTO> getAccountStatusInfos() {
		return accountStatusInfos;
	}

	public void setAccountStatusInfos(List<AccountStatusDTO> accountStatusInfos) {
		this.accountStatusInfos = accountStatusInfos;
	}
}
