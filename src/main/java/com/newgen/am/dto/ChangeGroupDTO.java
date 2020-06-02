package com.newgen.am.dto;

import javax.validation.constraints.NotEmpty;

public class ChangeGroupDTO {
	@NotEmpty(message = "Required.")
	private String groupCode;
	@NotEmpty(message = "Required.")
	private String groupName;
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
