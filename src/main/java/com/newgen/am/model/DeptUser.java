/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.util.Date;
import org.springframework.data.annotation.Transient;

/**
 *
 * @author nhungtt
 */
public class DeptUser extends BaseUser {
    @Transient
    public static final String SEQUENCE_NAME = "department_user_seq";
}
