package com.newgen.am.dto;

import java.io.Serializable;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import com.newgen.am.validation.FormatGroup;
import com.newgen.am.validation.LengthGroup;
import com.newgen.am.validation.UniqueGroup;
import com.newgen.am.validation.UniqueTaxCode;
import com.newgen.am.validation.ValidNumber;
import com.newgen.am.validation.ValidPhoneNumber;
import com.newgen.am.validation.ValidUpdateStringField;

import lombok.Data;

@Data
public class UpdateCompanyDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	@ValidUpdateStringField
	@Size(max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String name;
	@ValidUpdateStringField
	@ValidNumber(groups = FormatGroup.class)
	@Size(max = 20, message = "Invalid format.", groups = LengthGroup.class)
	@UniqueTaxCode(groups = UniqueGroup.class)
    private String taxCode;
	@ValidUpdateStringField
	@Size(max = 300, message = "Invalid format.", groups = LengthGroup.class)
    private String address;
	@ValidUpdateStringField
	@ValidPhoneNumber(groups = FormatGroup.class)
    private String phoneNumber;
	@ValidPhoneNumber(groups = FormatGroup.class)
    private String fax;
	@ValidUpdateStringField
    @Email(message = "Invalid format.", groups = FormatGroup.class)
	@Size(max = 50, message = "Invalid format.", groups = LengthGroup.class)
    private String email;
    @Valid
    private UpdateDelegateDTO delegate;
}
