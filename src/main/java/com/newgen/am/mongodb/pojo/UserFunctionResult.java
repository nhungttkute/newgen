/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.mongodb.pojo;

import java.util.List;

/**
 *
 * @author nhungtt
 */
public class UserFunctionResult {
    List<String> userFunctions;
    List<String> roleFunctions;

    public List<String> getUserFunctions() {
        return userFunctions;
    }

    public void setUserFunctions(List<String> userFunctions) {
        this.userFunctions = userFunctions;
    }

    public List<String> getRoleFunctions() {
        return roleFunctions;
    }

    public void setRoleFunctions(List<String> roleFunctions) {
        this.roleFunctions = roleFunctions;
    }
}
