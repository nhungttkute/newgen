/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import java.io.Serializable;

import com.newgen.am.validation.ValidUpdateStringField;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
public class UpdateRoleDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	@ValidUpdateStringField
    private String name;
    @ValidUpdateStringField
    private String description;
    @ValidUpdateStringField
    private String status;
}
