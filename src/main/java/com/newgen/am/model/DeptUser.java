/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.util.List;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
public class DeptUser extends BaseUser {
    private List<UserRole> roles;
    private List<RoleFunction> functions;
}
