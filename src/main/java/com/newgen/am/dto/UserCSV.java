/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import com.newgen.am.model.AuditModel;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

/**
 *
 * @author nhungtt
 */
public class UserCSV {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@CsvBindByName(column = "ID")
	@CsvBindByPosition(position = 0)
    private String _id;
	@CsvBindByName(column = "USERNAME")
	@CsvBindByPosition(position = 1)
    private String username;
	@CsvBindByName(column = "FULLNAME")
	@CsvBindByPosition(position = 2)
    private String fullName;
	@CsvBindByName(column = "EMAIL")
	@CsvBindByPosition(position = 3)
    private String email;
	@CsvBindByName(column = "PHONE_NUMBER")
	@CsvBindByPosition(position = 4)
    private String phoneNumber;
	@CsvBindByName(column = "STATUS")
	@CsvBindByPosition(position =5)
    private String status; //pending, active, inactive
	@CsvBindByName(column = "NOTE")
	@CsvBindByPosition(position = 6)
    private String note;
	@CsvBindByName(column = "PASSWORD_EXPIRY_CHECK")
	@CsvBindByPosition(position = 7)
    private Boolean isPasswordExpiryCheck;
	@CsvBindByName(column = "PASSWORD_EXPIRY_DAYS")
	@CsvBindByPosition(position = 8)
    private int passwordExpiryDays;
	@CsvBindByName(column = "EXPIRY_ALERT_DAYS")
	@CsvBindByPosition(position = 9)
    private int expiryAlertDays;
	@CsvBindByName(column = "CREATED_DATE")
	@CsvBindByPosition(position = 10)
	private String createdDate;

    public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
    
}
