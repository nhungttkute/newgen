/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.io.Serializable;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
public class Contact implements Serializable {
	private static final long serialVersionUID = 1L;
	private String fullName;
    private String phoneNumber;
    private String email;
}
