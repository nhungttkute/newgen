/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newgen.am.model.Commodity;
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
    private List<DeptUserDTO> deptUsers;
    private DepartmentDTO department;
    private DeptUserDTO deptUser;
    private List<SystemRoleDTO> systemRoles;
    private List<SystemFunctionDTO> systemFunctions;
    
    // response for member
    private List<MemberDTO> members;
    private MemberDTO member;

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

    public DeptUserDTO getDeptUser() {
        return deptUser;
    }

    public void setDeptUser(DeptUserDTO deptUser) {
        this.deptUser = deptUser;
    }

    public List<SystemRoleDTO> getSystemRoles() {
        return systemRoles;
    }

    public void setSystemRoles(List<SystemRoleDTO> systemRoles) {
        this.systemRoles = systemRoles;
    }

    public List<DeptUserDTO> getDeptUsers() {
        return deptUsers;
    }

    public void setDeptUsers(List<DeptUserDTO> deptUsers) {
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
    
}
