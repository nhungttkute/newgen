package com.newgen.am.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import com.newgen.am.validation.FormatGroup;
import com.newgen.am.validation.LengthGroup;
import com.newgen.am.validation.UniqueGroup;
import com.newgen.am.validation.UniqueIdentityCard;
import com.newgen.am.validation.ValidDate;
import com.newgen.am.validation.ValidNumber;
import com.newgen.am.validation.ValidNumberCharacter;
import com.newgen.am.validation.ValidPhoneNumber;
import com.newgen.am.validation.ValidUpdateStringField;

public class UpdateIndividualDTO {
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
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getBirthDay() {
		return birthDay;
	}
	public void setBirthDay(String birthDay) {
		this.birthDay = birthDay;
	}
	public String getIdentityCard() {
		return identityCard;
	}
	public void setIdentityCard(String identityCard) {
		this.identityCard = identityCard;
	}
	public String getIdCreatedDate() {
		return idCreatedDate;
	}
	public void setIdCreatedDate(String idCreatedDate) {
		this.idCreatedDate = idCreatedDate;
	}
	public String getIdCreatedLocation() {
		return idCreatedLocation;
	}
	public void setIdCreatedLocation(String idCreatedLocation) {
		this.idCreatedLocation = idCreatedLocation;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getScannedFrontIdCard() {
		return scannedFrontIdCard;
	}
	public void setScannedFrontIdCard(String scannedFrontIdCard) {
		this.scannedFrontIdCard = scannedFrontIdCard;
	}
	public String getScannedSignature() {
		return scannedSignature;
	}
	public void setScannedSignature(String scannedSignature) {
		this.scannedSignature = scannedSignature;
	}
	public String getScannedBackIdCard() {
		return scannedBackIdCard;
	}
	public void setScannedBackIdCard(String scannedBackIdCard) {
		this.scannedBackIdCard = scannedBackIdCard;
	}
    
}
