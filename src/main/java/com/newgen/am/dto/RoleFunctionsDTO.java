package com.newgen.am.dto;

import java.util.List;

import com.newgen.am.model.RoleFunction;

public class RoleFunctionsDTO {
	private List<RoleFunction> specificFunctions;
    private List<RoleFunction> roleFunctions;
	public List<RoleFunction> getSpecificFunctions() {
		return specificFunctions;
	}
	public void setSpecificFunctions(List<RoleFunction> specificFunctions) {
		this.specificFunctions = specificFunctions;
	}
	public List<RoleFunction> getRoleFunctions() {
		return roleFunctions;
	}
	public void setRoleFunctions(List<RoleFunction> roleFunctions) {
		this.roleFunctions = roleFunctions;
	}
	
}
