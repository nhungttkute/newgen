/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import java.util.List;

/**
 *
 * @author nhungtt
 */
public class UserFunctionOutputDTO {
    List<String> functions;
    TestUserDTO users;

    public List<String> getFunctions() {
        return functions;
    }

    public void setFunctions(List<String> functions) {
        this.functions = functions;
    }

    public TestUserDTO getUsers() {
        return users;
    }

    public void setUsers(TestUserDTO users) {
        this.users = users;
    }
}
