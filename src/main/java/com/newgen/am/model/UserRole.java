/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
public class UserRole implements Serializable {
	private static final long serialVersionUID = 1L;
	@NotEmpty(message = "Required.")
    private String name;
	@NotEmpty(message = "Required.")
    private String description;
	@NotEmpty(message = "Required.")
    private String status;
}
