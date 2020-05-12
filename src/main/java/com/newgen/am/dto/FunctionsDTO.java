package com.newgen.am.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.newgen.am.model.RoleFunction;

public class FunctionsDTO {
	@NotNull(message = "Required.")
    @Valid
	private List<RoleFunction> functions;

	public List<RoleFunction> getFunctions() {
		return functions;
	}

	public void setFunctions(List<RoleFunction> functions) {
		this.functions = functions;
	}
	
}
