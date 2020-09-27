package com.newgen.am.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListElementDTO {
	private String memberCode;
	private String brokerCode;
	private String collaboratorCode;
	private String investorCode;
	private String investorName;
	private String code;
	private String name;
}
