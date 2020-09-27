package com.newgen.am.dto;

import java.util.List;

import javax.validation.Valid;

import com.newgen.am.model.RoleFunction;

import lombok.Data;

@Data
public class FunctionsDTO {
    @Valid
	private List<RoleFunction> functions;
}
