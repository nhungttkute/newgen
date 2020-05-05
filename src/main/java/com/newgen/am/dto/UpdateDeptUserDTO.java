/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import javax.validation.constraints.Email;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newgen.am.validation.FormatGroup;
import com.newgen.am.validation.ValidPhoneNumber;
import com.newgen.am.validation.ValidUpdateIntegerField;
import com.newgen.am.validation.ValidUpdateStringField;

/**
 *
 * @author nhungtt
 */
/**
 * @author E7470
 *
 */
public class UpdateDeptUserDTO {
	@ValidUpdateStringField
    private String fullName;
	@ValidUpdateStringField
    @Email(message = "Invalid format.", groups = FormatGroup.class)
    private String email;
	@ValidUpdateStringField
    @ValidPhoneNumber(groups = FormatGroup.class)
    private String phoneNumber;
	@ValidUpdateStringField
    private String status; //pending, active, inactive
	@ValidUpdateStringField
    private String note;
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Boolean isPasswordExpiryCheck;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int passwordExpiryDays;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int expiryAlertDays;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Boolean getIsPasswordExpiryCheck() {
		return isPasswordExpiryCheck;
	}

	public void setIsPasswordExpiryCheck(Boolean isPasswordExpiryCheck) {
		this.isPasswordExpiryCheck = isPasswordExpiryCheck;
	}

	public int getPasswordExpiryDays() {
		return passwordExpiryDays;
	}

	public void setPasswordExpiryDays(int passwordExpiryDays) {
		this.passwordExpiryDays = passwordExpiryDays;
	}

	public int getExpiryAlertDays() {
        return expiryAlertDays;
    }

    public void setExpiryAlertDays(int expiryAlertDays) {
        this.expiryAlertDays = expiryAlertDays;
    }
}
