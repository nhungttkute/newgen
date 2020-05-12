/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newgen.am.model.Commodity;
import com.newgen.am.model.RoleFunction;
import com.newgen.am.model.SystemFunction;
import com.newgen.am.model.WatchList;
import java.util.List;

/**
 *
 * @author nhungtt
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AdminDataObj {
    private LoginAdminUserOutputDTO user;
    private String accessToken;
    private List<WatchList> watchLists;
    private String layout;
    private String language;
    private String theme;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int fontSize;
    
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
    
    // response for investor
    private List<String> investorCodes;

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
    
}
