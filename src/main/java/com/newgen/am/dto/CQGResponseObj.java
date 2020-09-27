package com.newgen.am.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CQGResponseObj {
	private String status;
    private String errMsg;
    private CQGDataObj data;
}
