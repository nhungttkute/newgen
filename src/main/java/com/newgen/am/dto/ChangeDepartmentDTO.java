package com.newgen.am.dto;

import lombok.Data;

@Data
public class ChangeDepartmentDTO {
	private String fromDeptCode;
	private String toDeptCode;
	private String username;
	private String fullName;
	private String phoneNumber;
	private String email;
}
