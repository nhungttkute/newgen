/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.mongodb.pojo;

import java.util.List;

/**
 *
 * @author nhungtt
 */
public class UserFunctionResult {
    private String deptId;
    private String deptCode;
    private String deptName;
    private String deptUserId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private List<String> userFunctions;
    private List<String> roleFunctions;

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

	public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getUserFunctions() {
        return userFunctions;
    }

    public void setUserFunctions(List<String> userFunctions) {
        this.userFunctions = userFunctions;
    }

    public List<String> getRoleFunctions() {
        return roleFunctions;
    }

    public void setRoleFunctions(List<String> roleFunctions) {
        this.roleFunctions = roleFunctions;
    }
}
