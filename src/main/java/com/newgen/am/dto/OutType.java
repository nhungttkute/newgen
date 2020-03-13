/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

/**
 *
 * @author nhungtt
 */
public class OutType {
    private Long invUserId;
    private String invUserName;
    private String invPassword;

    public Long getInvUserId() {
        return invUserId;
    }

    public void setInvUserId(Long invUserId) {
        this.invUserId = invUserId;
    }

    public String getInvUserName() {
        return invUserName;
    }

    public void setInvUserName(String invUserName) {
        this.invUserName = invUserName;
    }

    public String getInvPassword() {
        return invPassword;
    }

    public void setInvPassword(String invPassword) {
        this.invPassword = invPassword;
    }
    
}
