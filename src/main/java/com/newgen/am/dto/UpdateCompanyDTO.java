package com.newgen.am.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.newgen.am.model.Delegate;
import com.newgen.am.validation.FormatGroup;
import com.newgen.am.validation.LengthGroup;
import com.newgen.am.validation.ValidNumber;
import com.newgen.am.validation.ValidPhoneNumber;
import com.newgen.am.validation.ValidUpdateStringField;

public class UpdateCompanyDTO {
	@ValidUpdateStringField
	@Size(min = 1, max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String name;
	@ValidUpdateStringField
	@ValidNumber(groups = FormatGroup.class)
	@Size(min = 1, max = 20, message = "Invalid format.", groups = LengthGroup.class)
    private String taxCode;
	@ValidUpdateStringField
	@Size(min = 1, max = 300, message = "Invalid format.", groups = LengthGroup.class)
    private String address;
	@ValidUpdateStringField
	@ValidPhoneNumber(groups = FormatGroup.class)
    private String phoneNumber;
	@ValidPhoneNumber(groups = FormatGroup.class)
    private String fax;
	@ValidUpdateStringField
    @Email(message = "Invalid format.", groups = FormatGroup.class)
    private String email;
    private UpdateDelegateDTO delegate;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTaxCode() {
		return taxCode;
	}
	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public UpdateDelegateDTO getDelegate() {
		return delegate;
	}
	public void setDelegate(UpdateDelegateDTO delegate) {
		this.delegate = delegate;
	}
	
}
