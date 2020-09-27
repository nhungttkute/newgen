package com.newgen.am.dto;

import java.util.List;

import com.newgen.am.model.RoleFunction;

import lombok.Data;

@Data
public class RoleFunctionsDTO {
	private List<RoleFunction> specificFunctions;
    private List<RoleFunction> roleFunctions;
}
