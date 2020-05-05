package com.newgen.am.dto;

import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newgen.am.model.RoleFunction;
import com.newgen.am.model.UserRole;
import com.newgen.am.validation.FormatGroup;
import com.newgen.am.validation.LengthGroup;
import com.newgen.am.validation.ValidPhoneNumber;
import com.newgen.am.validation.ValidUsername;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class MemberUserDTO {
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String _id;
    @NotEmpty(message = "Required.")
    @ValidUsername(groups = FormatGroup.class)
    private String username;
    @NotEmpty(message = "Required.")
    @Size(min = 1, max = 100, message = "Invalid format.", groups = LengthGroup.class)
    private String fullName;
    @NotEmpty(message = "Required.")
    @Email(message = "Invalid format.", groups = FormatGroup.class)
    private String email;
    @NotEmpty(message = "Required.")
    @ValidPhoneNumber(groups = FormatGroup.class)
    private String phoneNumber;
    @NotEmpty(message = "Required.")
    @Size(min = 1, max = 20, message = "Invalid format.", groups = LengthGroup.class)
    private String status; //pending, active, inactive
    private String note;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Boolean isPasswordExpiryCheck;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int passwordExpiryDays;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int expiryAlertDays;
    private String createdDate;
    private List<UserRole> roles;
    private List<RoleFunction> functions;
    
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Boolean getIsPasswordExpiryCheck() {
		return isPasswordExpiryCheck;
	}
	public void setIsPasswordExpiryCheck(Boolean isPasswordExpiryCheck) {
		this.isPasswordExpiryCheck = isPasswordExpiryCheck;
	}
	public int getPasswordExpiryDays() {
		return passwordExpiryDays;
	}
	public void setPasswordExpiryDays(int passwordExpiryDays) {
		this.passwordExpiryDays = passwordExpiryDays;
	}
	public int getExpiryAlertDays() {
		return expiryAlertDays;
	}
	public void setExpiryAlertDays(int expiryAlertDays) {
		this.expiryAlertDays = expiryAlertDays;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public List<UserRole> getRoles() {
		return roles;
	}
	public void setRoles(List<UserRole> roles) {
		this.roles = roles;
	}
	public List<RoleFunction> getFunctions() {
		return functions;
	}
	public void setFunctions(List<RoleFunction> functions) {
		this.functions = functions;
	}
    
}
