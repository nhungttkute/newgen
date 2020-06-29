package com.newgen.am.dto;

public class ChangeDepartmentDTO {
	private String fromDeptCode;
	private String toDeptCode;
	private String username;
	private String fullName;
	private String phoneNumber;
	private String email;
	public String getFromDeptCode() {
		return fromDeptCode;
	}
	public void setFromDeptCode(String fromDeptCode) {
		this.fromDeptCode = fromDeptCode;
	}
	public String getToDeptCode() {
		return toDeptCode;
	}
	public void setToDeptCode(String toDeptCode) {
		this.toDeptCode = toDeptCode;
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
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
