/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.io.Serializable;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.newgen.am.validation.FormatGroup;
import com.newgen.am.validation.LengthGroup;
import com.newgen.am.validation.UniqueGroup;
import com.newgen.am.validation.UniqueTaxCode;
import com.newgen.am.validation.ValidNumber;
import com.newgen.am.validation.ValidPhoneNumber;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
public class Company implements Serializable {
	private static final long serialVersionUID = 1L;
	@NotEmpty(message = "Required.")
	@Size(min = 1, max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String name;
	@NotEmpty(message = "Required.")
	@ValidNumber(groups = FormatGroup.class)
	@Size(min = 1, max = 20, message = "Invalid format.", groups = LengthGroup.class)
	@UniqueTaxCode(groups = UniqueGroup.class)
    private String taxCode;
	@NotEmpty(message = "Required.")
	@Size(min = 1, max = 300, message = "Invalid format.", groups = LengthGroup.class)
    private String address;
	@NotEmpty(message = "Required.")
	@ValidPhoneNumber(groups = FormatGroup.class)
    private String phoneNumber;
	@ValidPhoneNumber(groups = FormatGroup.class)
    private String fax;
    @NotEmpty(message = "Required.")
    @Email(message = "Invalid format.", groups = FormatGroup.class)
    @Size(min = 1, max = 100, message = "Invalid format.", groups = LengthGroup.class)
    private String email;
    @NotNull(message = "Required.")
    @Valid
    private Delegate delegate;
}
