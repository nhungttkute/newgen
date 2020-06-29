package com.newgen.am.dto;

import java.util.List;

import javax.validation.Valid;

import com.newgen.am.model.UserRole;

public class UserRolesDTO {
//	@NotNull(message = "Required.")
    @Valid
    private List<UserRole> roles;

	public List<UserRole> getRoles() {
		return roles;
	}

	public void setRoles(List<UserRole> roles) {
		this.roles = roles;
	}
	
}
