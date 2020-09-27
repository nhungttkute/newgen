/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import java.io.Serializable;

import javax.validation.constraints.Email;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newgen.am.validation.FormatGroup;
import com.newgen.am.validation.ValidPhoneNumber;
import com.newgen.am.validation.ValidUpdateStringField;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
/**
 * @author E7470
 *
 */
@Data
public class UpdateUserDTO implements Serializable {
	private static final long serialVersionUID = 1L;
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
}
