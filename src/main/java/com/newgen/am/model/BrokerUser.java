/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import org.springframework.data.annotation.Transient;

/**
 *
 * @author nhungtt
 */
public class BrokerUser extends BaseUser {
    @Transient
    public static final String SEQUENCE_NAME = "broker_user_seq";
    private String title;
    private String department;
    private String identityCard;
    private String idCreatedDate;
    private String idCreatedLocation;
    private String address;
    private String scannedIdCard; //image data
    private String scannedSignature; //image data

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getScannedIdCard() {
        return scannedIdCard;
    }

    public void setScannedIdCard(String scannedIdCard) {
        this.scannedIdCard = scannedIdCard;
    }

    public String getScannedSignature() {
        return scannedSignature;
    }

    public void setScannedSignature(String scannedSignature) {
        this.scannedSignature = scannedSignature;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
