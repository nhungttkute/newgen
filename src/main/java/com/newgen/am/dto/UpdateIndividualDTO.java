package com.newgen.am.dto;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import com.newgen.am.validation.FormatGroup;
import com.newgen.am.validation.LengthGroup;
import com.newgen.am.validation.UniqueGroup;
import com.newgen.am.validation.UniqueIdentityCard;
import com.newgen.am.validation.ValidDate;
import com.newgen.am.validation.ValidNumberCharacter;
import com.newgen.am.validation.ValidPhoneNumber;
import com.newgen.am.validation.ValidUpdateStringField;

import lombok.Data;

@Data
public class UpdateIndividualDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	@ValidUpdateStringField
	@Size(max = 100, message = "Invalid format.", groups = LengthGroup.class)
    private String fullName;
	@ValidUpdateStringField
	@ValidDate(groups = FormatGroup.class)
    private String birthDay;
	@ValidUpdateStringField
	@ValidNumberCharacter(groups = FormatGroup.class)
	@Size(max = 20, groups = LengthGroup.class)
	@UniqueIdentityCard(groups = UniqueGroup.class)
    private String identityCard;
	@ValidUpdateStringField
	@ValidDate(groups = FormatGroup.class)
    private String idCreatedDate;
	@ValidUpdateStringField
	@Size(max = 100, message = "Invalid format.", groups = LengthGroup.class)
    private String idCreatedLocation;
	@ValidUpdateStringField
	@Email(message = "Invalid format.", groups = FormatGroup.class)
	@Size(max = 50, message = "Invalid format.", groups = LengthGroup.class)
    private String email;
	@ValidUpdateStringField
    @ValidPhoneNumber(groups = FormatGroup.class)
    private String phoneNumber;
	@ValidUpdateStringField
	@Size(max = 300, message = "Invalid format.", groups = LengthGroup.class)
    private String address;
	@ValidUpdateStringField
	@Size(max = 134000, message = "Invalid format.", groups = LengthGroup.class)
    private String scannedFrontIdCard; //image data
	@ValidUpdateStringField
	@Size(max = 134000, message = "Invalid format.", groups = LengthGroup.class)
    private String scannedBackIdCard; //image data
	@ValidUpdateStringField
	@Size(max = 134000, message = "Invalid format.", groups = LengthGroup.class)
    private String scannedSignature; //image data
}
