/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.io.Serializable;
//import org.springframework.data.redis.core.RedisHash;

/**
 *
 * @author nhungtt
 */
//@RedisHash("Student")
public class RedisUserInfo implements Serializable {
    private String decryptedAccessToken;
    private String value;

    public String getDecryptedAccessToken() {
        return decryptedAccessToken;
    }

    public void setDecryptedAccessToken(String decryptedAccessToken) {
        this.decryptedAccessToken = decryptedAccessToken;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}
