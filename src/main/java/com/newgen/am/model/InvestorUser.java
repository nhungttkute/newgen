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
public class InvestorUser extends MemberUser {
    @Transient
    public static final String SEQUENCE_NAME = "investor_user_seq";
}
