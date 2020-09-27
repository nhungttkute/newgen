package com.newgen.am.dto;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserBaseInfo {
	private String _id;
	@NotEmpty(message = "Required.")
	private String username;
	@NotEmpty(message = "Required.")
	private String fullName;
	private String email;
	private String phoneNumber;
}
