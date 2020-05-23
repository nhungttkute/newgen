package com.newgen.am.dto;

public class LoginAdminUsersDTO {
	private String deptCode;
    private String memberCode;
    private String brokerCode;
    private String collaboratorCode;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String status;
    private Boolean logined = false;
    private int logonCounts;
    private long logonTime;
	public String getDeptCode() {
		return deptCode;
	}
	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}
	public String getMemberCode() {
		return memberCode;
	}
	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}
	public String getBrokerCode() {
		return brokerCode;
	}
	public void setBrokerCode(String brokerCode) {
		this.brokerCode = brokerCode;
	}
	public String getCollaboratorCode() {
		return collaboratorCode;
	}
	public void setCollaboratorCode(String collaboratorCode) {
		this.collaboratorCode = collaboratorCode;
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
	public Boolean getLogined() {
		return logined;
	}
	public void setLogined(Boolean logined) {
		this.logined = logined;
	}
	public int getLogonCounts() {
		return logonCounts;
	}
	public void setLogonCounts(int logonCounts) {
		this.logonCounts = logonCounts;
	}
	public long getLogonTime() {
		return logonTime;
	}
	public void setLogonTime(long logonTime) {
		this.logonTime = logonTime;
	}
    
}
