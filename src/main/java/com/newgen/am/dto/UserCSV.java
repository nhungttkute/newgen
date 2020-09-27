/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
public class UserCSV {
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
}
