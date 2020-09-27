/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.io.Serializable;
import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.lang.NonNull;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
@Document(collection = "login_admin_users")
public class LoginAdminUser extends AuditModel implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
    private String id;
    @NonNull
    @Indexed(unique = true)
    private String username;
    @NonNull
    @JsonIgnore
    private String password;
    private String fullName;
    private String email;
    private String phoneNumber;
    @Field
    private Boolean checkPin = true;
    @NonNull
    @Field
    private String pin;
    private String status;
    private String accessToken;
    private long tokenExpiration;
    @Field
    private Boolean logined = false;
    @Field
    private Boolean mustChangePassword = true;
    private int logonCounts;
    private long logonTime;
    private String deptCode;
    private String deptName;
    private String memberCode;
    private String memberName;
    private String brokerCode;
    private String brokerName;
    private String collaboratorCode;
    private String collaboratorName;
    @Field
    @Length(max = 10000)
    private String layout;
    private String language;
    private String theme;
    private int fontSize;
    private List<WatchList> watchlists;
    private List<Exchange> exchanges;
    private String tableSetting;
}
