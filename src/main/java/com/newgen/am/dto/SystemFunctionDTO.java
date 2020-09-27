package com.newgen.am.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SystemFunctionDTO {
	private String id;
    private String code;
    private String name;
    private String orderNumber;
}
