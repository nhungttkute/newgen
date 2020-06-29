package com.newgen.am.dto;

import javax.validation.constraints.NotEmpty;

import com.newgen.am.validation.ValidPassword;

public class ChangePasswordDTO {
	@NotEmpty(message = "Required.")
	private String oldPassword;
	@NotEmpty(message = "Required.")
	@ValidPassword
    private String newPassword;
	public String getOldPassword() {
		return oldPassword;
	}
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
}
