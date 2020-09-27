/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import com.newgen.am.dto.DataObj;
import com.newgen.am.dto.ResponseObj;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
public class BaseUser extends AuditModel {
	private static final long serialVersionUID = 1L;
	private String _id;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String status; //pending, active, inactive
    private String note;
    private boolean isPasswordExpiryCheck;
    private int passwordExpiryDays;
    private int expiryAlertDays;
}
