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
public class MemberUser extends BaseUser {
    @Transient
    public static final String SEQUENCE_NAME = "member_user_seq";
    private String title;
    private String department;

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
