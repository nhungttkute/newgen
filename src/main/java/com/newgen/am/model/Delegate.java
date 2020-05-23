/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;

import com.newgen.am.validation.FormatGroup;
import com.newgen.am.validation.LengthGroup;
import com.newgen.am.validation.UniqueGroup;
import com.newgen.am.validation.UniqueIdentityCard;
import com.newgen.am.validation.ValidDate;
import com.newgen.am.validation.ValidNumber;
import com.newgen.am.validation.ValidPhoneNumber;
import com.newgen.am.validation.ValidationSequence;

/**
 *
 * @author nhungtt
 */
public class Delegate {
	@NotEmpty(message = "Required.")
	@Size(min = 1, max = 100, message = "Invalid format.", groups = LengthGroup.class)
    private String fullName;
	@NotEmpty(message = "Required.")
	@ValidDate(groups = FormatGroup.class)
    private String birthDay;
	@NotEmpty(message = "Required.")
	@ValidNumber(groups = FormatGroup.class)
	@Size(min = 1, max = 20, groups = LengthGroup.class)
	@UniqueIdentityCard(groups = UniqueGroup.class)
    private String identityCard;
	@NotEmpty(message = "Required.")
	@ValidDate(groups = FormatGroup.class)
    private String idCreatedDate;
	@NotEmpty(message = "Required.")
	@Size(min = 1, max = 100, message = "Invalid format.", groups = LengthGroup.class)
    private String idCreatedLocation;
	@NotEmpty(message = "Required.")
	@Email(message = "Invalid format.", groups = FormatGroup.class)
	@Size(min = 1, max = 50, message = "Invalid format.", groups = LengthGroup.class)
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

	public String getScannedBackIdCard() {
		return scannedBackIdCard;
	}

	public void setScannedBackIdCard(String scannedBackIdCard) {
		this.scannedBackIdCard = scannedBackIdCard;
	}

	public String getScannedSignature() {
        return scannedSignature;
    }

    public void setScannedSignature(String scannedSignature) {
        this.scannedSignature = scannedSignature;
    }
    
}
