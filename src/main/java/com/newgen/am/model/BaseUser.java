/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.util.Date;
import java.util.List;

/**
 *
 * @author nhungtt
 */
public class BaseUser {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String status; //pending, active, inactive
    private String note;
    private Boolean isPasswordExpiryCheck;
    private Integer passwordExpiryDays;
    private Integer expiryAlertDays;
    private Date createdAt;
    private Date updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getIsPasswordExpiryCheck() {
        return isPasswordExpiryCheck;
    }

    public void setIsPasswordExpiryCheck(Boolean isPasswordExpiryCheck) {
        this.isPasswordExpiryCheck = isPasswordExpiryCheck;
    }

    public Integer getPasswordExpiryDays() {
        return passwordExpiryDays;
    }

    public void setPasswordExpiryDays(Integer passwordExpiryDays) {
        this.passwordExpiryDays = passwordExpiryDays;
    }

    public Integer getExpiryAlertDays() {
        return expiryAlertDays;
    }

    public void setExpiryAlertDays(Integer expiryAlertDays) {
        this.expiryAlertDays = expiryAlertDays;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
