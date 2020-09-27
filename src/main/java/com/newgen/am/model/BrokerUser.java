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
public class BrokerUser extends BaseUser {
	private static final long serialVersionUID = 1L;
	private UserRole role;
    private List<RoleFunction> functions;
}
