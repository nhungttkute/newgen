package com.newgen.am.dto;

import javax.validation.constraints.NotEmpty;

import com.newgen.am.validation.ValidPassword;

import lombok.Data;

@Data
public class ChangePasswordDTO {
	@NotEmpty(message = "Required.")
	private String oldPassword;
	@NotEmpty(message = "Required.")
	@ValidPassword
    private String newPassword;
}
