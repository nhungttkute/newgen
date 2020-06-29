/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.util.List;

/**
 *
 * @author nhungtt
 */
public class BrokerUser extends BaseUser {
	private static final long serialVersionUID = 1L;
	private UserRole role;
    private List<RoleFunction> functions;
	public UserRole getRole() {
		return role;
	}
	public void setRole(UserRole role) {
		this.role = role;
	}
	public List<RoleFunction> getFunctions() {
		return functions;
	}
	public void setFunctions(List<RoleFunction> functions) {
		this.functions = functions;
	}
    
}
