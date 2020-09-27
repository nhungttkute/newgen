package com.newgen.am.dto;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class ChangeGroupDTO {
	@NotEmpty(message = "Required.")
	private String groupCode;
	@NotEmpty(message = "Required.")
	private String groupName;
}
