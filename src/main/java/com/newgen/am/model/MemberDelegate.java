/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.newgen.am.validation.FormatGroup;
import com.newgen.am.validation.LengthGroup;
import com.newgen.am.validation.ValidDate;
import com.newgen.am.validation.ValidNumberCharacter;
import com.newgen.am.validation.ValidPhoneNumber;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
public class MemberDelegate implements Serializable {
	private static final long serialVersionUID = 1L;
	@NotEmpty(message = "Required.")
	@Size(min = 1, max = 100, message = "Invalid format.", groups = LengthGroup.class)
    private String fullName;
	@NotEmpty(message = "Required.")
	@ValidDate(groups = FormatGroup.class)
    private String birthDay;
	@NotEmpty(message = "Required.")
	@ValidNumberCharacter(groups = FormatGroup.class)
	@Size(min = 1, max = 50, groups = LengthGroup.class)
    private String identityCard;
	@NotEmpty(message = "Required.")
	@ValidDate(groups = FormatGroup.class)
    private String idCreatedDate;
	@NotEmpty(message = "Required.")
	@Size(min = 1, max = 100, message = "Invalid format.", groups = LengthGroup.class)
    private String idCreatedLocation;
	@NotEmpty(message = "Required.")
	@Email(message = "Invalid format.", groups = FormatGroup.class)
	@Size(min = 1, max = 100, message = "Invalid format.", groups = LengthGroup.class)
    private String email;
	@NotEmpty(message = "Required.")
    @ValidPhoneNumber(groups = FormatGroup.class)
    private String phoneNumber;
	@NotEmpty(message = "Required.")
	@Size(min = 1, max = 300, message = "Invalid format.", groups = LengthGroup.class)
    private String address;
	@NotEmpty(message = "Required.")
	@Size(min = 1, max = 134000, message = "Invalid format.", groups = LengthGroup.class)
    private String scannedFrontIdCard; //image data
	@NotEmpty(message = "Required.")
	@Size(min = 1, max = 134000, message = "Invalid format.", groups = LengthGroup.class)
    private String scannedBackIdCard; //image data
	@NotEmpty(message = "Required.")
	@Size(min = 1, max = 134000, message = "Invalid format.", groups = LengthGroup.class)
    private String scannedSignature; //image data
}
