/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.util.Date;
import java.util.List;
import org.springframework.data.annotation.Transient;

/**
 *
 * @author nhungtt
 */
public class DeptUser extends BaseUser {
    @Transient
    public static final String SEQUENCE_NAME = "department_user_seq";
    private List<UserRole> roles;
    private List<RoleFunction> functions;

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

    public List<RoleFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(List<RoleFunction> functions) {
        this.functions = functions;
    }
    
}
